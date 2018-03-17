package controller;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import com.lynden.gmapsfx.util.MarkerImageFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import lombok.Getter;
import model.data.loader.CitiesDistancesLoader;
import model.data.loader.CitiesLoader;
import model.domain.City;
import model.domain.PointsDistance;
import model.domain.TSPResult;
import model.routes.calculators.NearestNeighbourCalc;
import model.routes.calculators.RandomPathCalc;
import model.routes.calculators.TwoOptCalc;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable, MapComponentInitializedListener {

    @FXML
    private ComboBox<String> cbData, cbAlgorithm, cbOptimization;
    @FXML
    private TextField tfIterations;
    @FXML
    private CheckBox chboxBasicAlgorithm, chboxOptimizationAlgorithm;
    @FXML
    private Label lDuration;
    @FXML
    private TextArea taLogs;
    @FXML
    private Pane mapPane;

    private GoogleMapView mapView;
    private GoogleMap map;
    private MapShapeDrawer mapShapeDrawer;

    private NearestNeighbourCalc nearestNeighbourCalc;
    private RandomPathCalc randomPathCalc;
    private TwoOptCalc twoOptCalc;

    @Override
    public void mapInitialized() {
        MapOptions mapOptions = new MapOptions();
        mapOptions.center(new LatLong(52, 19))
                .mapType(MapTypeIdEnum.ROADMAP)
                .overviewMapControl(false)
                .panControl(false)
                .rotateControl(false)
                .scaleControl(false)
                .streetViewControl(false)
                .zoomControl(false)
                .zoom(6);

        mapView.setPrefSize(1000, 600);
        map = mapView.createMap(mapOptions);

        mapShapeDrawer = new MapShapeDrawer(map);
        mapShapeDrawer.addMarkersToMapForPoints(CitiesLoader.MAIN_CITIES);

        nearestNeighbourCalc = new NearestNeighbourCalc(mapShapeDrawer);
        randomPathCalc = new RandomPathCalc(mapShapeDrawer);
        twoOptCalc = new TwoOptCalc(mapShapeDrawer);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tfIterations.textProperty().addListener((observable, oldValue, newValue) -> removeTextIfNotDigit(newValue));
        taLogs.setEditable(false);
        taLogs.setWrapText(true);
        cbData.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Polish cities (16)"))
                mapShapeDrawer.addMarkersToMapForPoints(CitiesLoader.MAIN_CITIES);
            else
                mapShapeDrawer.addMarkersToMapForPoints(CitiesLoader.ALL_CITIES);
        });

        OutputStream out = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                appendText(String.valueOf((char) b));
            }
        };
        System.setOut(new PrintStream(out, true));

        mapView = new GoogleMapView();
        mapView.addMapInializedListener(this);
        mapPane.getChildren().add(mapView);
    }

    private void removeTextIfNotDigit(String newValue) {
        tfIterations.setText(newValue.matches("\\d*") ? newValue : newValue.replaceAll("[^\\d]", ""));
    }

    public void startTsp(ActionEvent actionEvent) {
//        new Thread(() -> {
//            try {
//                int i = 0;
//                Thread.sleep(1000);
//                lDuration.setText(String.valueOf(++i));
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }).run();
//        System.out.println("Started resolving TSP");

        List<City> points = isChosenData("Polish cities (16)") ? CitiesLoader.MAIN_CITIES : CitiesLoader.ALL_CITIES;
        List<PointsDistance> pointsDistances = isChosenData("Polish cities (16)") ? CitiesDistancesLoader.MAIN_CITIES_DISTANCES : CitiesDistancesLoader.ALL_CITIES_DISTANCES;
        mapShapeDrawer.clearAllMapPolylines();

        for (int i = 1; i <= Integer.valueOf(tfIterations.getText()); i++) {
            long startTime = System.currentTimeMillis();
            TSPResult tspResult = isChosenAlgorithm("Nearest neighbour") ? nearestNeighbourCalc.getPath(points, pointsDistances) : randomPathCalc.getPath(points, pointsDistances);
            System.out.println("Iteration #" + i + ": " + tspResult.getTotalDistance() + " km");
            System.out.println("Iteration #" + i + " duration: " + (System.currentTimeMillis() - startTime) + " ms");

            if (isChosenOptimization()) {
                startTime = System.currentTimeMillis();
                TSPResult optimizedTspResult = twoOptCalc.getPath(pointsDistances, tspResult);
                System.out.println("Iteration #" + i + " optimized: " + optimizedTspResult.getTotalDistance() + " km");
                System.out.println("Iteration #" + i + " optimization duration: " + (System.currentTimeMillis() - startTime) + " ms");
            }
        }
        changeOptimizationAlgorithmSolutionVisibility();
        changeBasicAlgorithmSolutionVisibility();
    }

    private boolean isChosenData(String data) {
        return cbData.getSelectionModel().selectedItemProperty().getValue().equals(data);
    }

    private boolean isChosenAlgorithm(String algorithmName) {
        return cbAlgorithm.getSelectionModel().selectedItemProperty().getValue().equals(algorithmName);
    }

    private boolean isChosenOptimization() {
        return cbOptimization.getSelectionModel().selectedItemProperty().getValue().equals("2-opt");
    }

    public void changeOptimizationAlgorithmSolutionVisibility(){
        mapShapeDrawer.changeOptimizationAlgorithmSolutionVisibility(chboxOptimizationAlgorithm.isSelected());
    }

    public void changeBasicAlgorithmSolutionVisibility(){
        mapShapeDrawer.changeBasicAlgorithmSolutionVisibility(chboxBasicAlgorithm.isSelected());
    }
    private void appendText(String text) {
        Platform.runLater(() -> taLogs.appendText(text));
    }
}
