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
import java.util.Timer;
import java.util.TimerTask;

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
        mapShapeDrawer.addMarkersToMapForPoints(CitiesLoader.CITIES_16);

        nearestNeighbourCalc = new NearestNeighbourCalc(mapShapeDrawer, this);
        randomPathCalc = new RandomPathCalc(mapShapeDrawer, this);
        twoOptCalc = new TwoOptCalc(mapShapeDrawer, this);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        cbData.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals("Polish cities (16)"))
                mapShapeDrawer.addMarkersToMapForPoints(CitiesLoader.CITIES_16);
            if (newValue.equals("Polish cities (50)"))
                mapShapeDrawer.addMarkersToMapForPoints(CitiesLoader.CITIES_50);
            if (newValue.equals("Polish cities (100)"))
                mapShapeDrawer.addMarkersToMapForPoints(CitiesLoader.CITIES_100);
        });

        mapView = new GoogleMapView();
        mapView.addMapInializedListener(this);
        mapPane.getChildren().add(mapView);
    }

    public void startTsp(ActionEvent actionEvent) {
        Thread thread = new Thread(() -> {
            Platform.runLater(() -> lDuration.setText(Integer.toString(0)));
            Timer timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> lDuration.setText(Integer.toString(Integer.valueOf(lDuration.getText()) + 1)));
                }
            }, 0, 1000);

            long startTime = System.currentTimeMillis();
            List<City> points = isChosenData("Polish cities (16)") ? CitiesLoader.CITIES_16 : isChosenData("Polish cities (50)") ? CitiesLoader.CITIES_50 : CitiesLoader.CITIES_100;
            List<PointsDistance> pointsDistances = isChosenData("Polish cities (16)") ? CitiesDistancesLoader.DISTANCES_16 : isChosenData("Polish cities (50)") ? CitiesDistancesLoader.DISTANCES_50 : CitiesDistancesLoader.DISTANCES_100;
            mapShapeDrawer.clearAllMapPolylines();

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
            timer.cancel();
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
