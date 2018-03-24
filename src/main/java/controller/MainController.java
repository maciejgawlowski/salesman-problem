package controller;

import com.lynden.gmapsfx.GoogleMapView;
import com.lynden.gmapsfx.MapComponentInitializedListener;
import com.lynden.gmapsfx.javascript.object.GoogleMap;
import com.lynden.gmapsfx.javascript.object.LatLong;
import com.lynden.gmapsfx.javascript.object.MapOptions;
import com.lynden.gmapsfx.javascript.object.MapTypeIdEnum;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import tsp.data.loader.CitiesDistancesLoader;
import tsp.data.loader.CitiesLoader;
import tsp.domain.City;
import tsp.domain.PointsDistance;
import tsp.domain.TSPResult;
import tsp.routes.calculators.NearestNeighbourCalc;
import tsp.routes.calculators.RandomPathCalc;
import tsp.routes.calculators.TwoOptCalc;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainController implements Initializable, MapComponentInitializedListener {

    @FXML
    private ComboBox<String> cbData, cbAlgorithm, cbOptimization;
    @FXML
    private CheckBox chboxBasicAlgorithm, chboxOptimizationAlgorithm;
    @FXML
    private Label lDuration;
    @FXML
    private Pane mapPane;
    @FXML
    private TextArea taLogs;

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

        nearestNeighbourCalc = new NearestNeighbourCalc(mapShapeDrawer, this);
        randomPathCalc = new RandomPathCalc(mapShapeDrawer, this);
        twoOptCalc = new TwoOptCalc(mapShapeDrawer, this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbData.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Polish cities (16)"))
                mapShapeDrawer.addMarkersToMapForPoints(CitiesLoader.MAIN_CITIES);
            else
                mapShapeDrawer.addMarkersToMapForPoints(CitiesLoader.ALL_CITIES);
        });

        mapView = new GoogleMapView();
        mapView.addMapInializedListener(this);
        mapPane.getChildren().add(mapView);
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
        Thread thread = new Thread(() -> {
            List<City> points = isChosenData("Polish cities (16)") ? CitiesLoader.MAIN_CITIES : CitiesLoader.ALL_CITIES;
            List<PointsDistance> pointsDistances = isChosenData("Polish cities (16)") ? CitiesDistancesLoader.MAIN_CITIES_DISTANCES : CitiesDistancesLoader.ALL_CITIES_DISTANCES;
            mapShapeDrawer.clearAllMapPolylines();

            long startTime = System.currentTimeMillis();
            TSPResult tspResult = isChosenAlgorithm("Nearest neighbour") ? nearestNeighbourCalc.getPath(points, pointsDistances) : randomPathCalc.getPath(points, pointsDistances);
            String logPrefix = isChosenAlgorithm("Nearest neighbour") ? "Nearest Neighbour" : "Random Path";
            log(logPrefix + " TSP resolving duration: " + ((System.currentTimeMillis() - startTime) / 1000.0) + " s");
            log(logPrefix + " TSP distance: " + tspResult.getTotalDistance() + " km");

            if (isChosenOptimization()) {
                startTime = System.currentTimeMillis();
                TSPResult optimizedTspResult = twoOptCalc.getPath(pointsDistances, tspResult);
                log("Two opt TSP result optimization duration: " + ((System.currentTimeMillis() - startTime) / 1000.0) + " s");
                log("Two opt TSP distance: " + optimizedTspResult.getTotalDistance() + " km");
            }
            changeOptimizationAlgorithmSolutionVisibility();
            changeBasicAlgorithmSolutionVisibility();
        });
        thread.start();
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

    public void changeOptimizationAlgorithmSolutionVisibility() {
        mapShapeDrawer.changeOptimizationAlgorithmSolutionVisibility(chboxOptimizationAlgorithm.isSelected());
    }

    public void changeBasicAlgorithmSolutionVisibility() {
        mapShapeDrawer.changeBasicAlgorithmSolutionVisibility(chboxBasicAlgorithm.isSelected());
    }

    public void log(String text) {
        Platform.runLater(() -> taLogs.appendText(DateTime.now().toString(DateTimeFormat.forPattern("[HH:mm:ss] ")) + text + "\n"));
    }
}
