package controller;

import com.lynden.gmapsfx.javascript.object.*;
import com.lynden.gmapsfx.shapes.Polyline;
import com.lynden.gmapsfx.shapes.PolylineOptions;
import com.lynden.gmapsfx.util.MarkerImageFactory;
import javafx.application.Platform;
import tsp.domain.City;

import java.util.ArrayList;
import java.util.List;

public class MapShapeDrawer {
    private GoogleMap map;

    private List<Polyline> basicAlgorithmPolylinesOnMap = new ArrayList<>();
    private List<Polyline> optimizionAlgorithmPolylinesOnMap = new ArrayList<>();
    private List<Marker> currentMarkersOnMap = new ArrayList<>();

    MapShapeDrawer(GoogleMap map) {
        this.map = map;
    }

    void addMarkersToMapForPoints(List<City> cities) {
        Platform.runLater(() -> {
            map.removeMarkers(currentMarkersOnMap);
            currentMarkersOnMap.clear();
            for (City city : cities) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.title(city.getName());
                markerOptions.position(new LatLong(city.getLatitude(), city.getLongitude()));
                markerOptions.icon(MarkerImageFactory.createMarkerImage("file:///D:/IdeaProjects/salesman-problem/src/main/resources/map_marker_icon.png", "png").replace("(", "").replace(")", ""));
                Marker marker = new Marker(markerOptions);
                currentMarkersOnMap.add(marker);
                map.addMarker(marker);
            }
        });

    }

    public void addLineToBasicAlgorithmPolylines(City startCity, City endCity) {
        Platform.runLater(() -> {
            LatLong[] path = {new LatLong(startCity.getLatitude(), startCity.getLongitude()), new LatLong(endCity.getLatitude(), endCity.getLongitude())};
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.path(new MVCArray(path));
            polylineOptions.strokeWeight(1.5);
            Polyline polyline = new Polyline(polylineOptions);
            basicAlgorithmPolylinesOnMap.add(polyline);
            map.addMapShape(polyline);
        });
    }

    public void addLineToOptimizionAlgorithmPolylines(City startCity, City endCity) {
        Platform.runLater(() -> {
            LatLong[] path = {new LatLong(startCity.getLatitude(), startCity.getLongitude()), new LatLong(endCity.getLatitude(), endCity.getLongitude())};
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.path(new MVCArray(path));
            polylineOptions.strokeColor("Blue");
            polylineOptions.strokeWeight(1.5);
            Polyline polyline = new Polyline(polylineOptions);
            optimizionAlgorithmPolylinesOnMap.add(polyline);
            map.addMapShape(polyline);
        });
    }

    public void clearAllMapPolylines() {
        Platform.runLater(() -> {
            basicAlgorithmPolylinesOnMap.forEach(polyline -> map.removeMapShape(polyline));
            basicAlgorithmPolylinesOnMap.clear();
            clearOptimizionAlgorithmPolylines();
        });
    }

    public void clearOptimizionAlgorithmPolylines() {
        Platform.runLater(() -> {
            optimizionAlgorithmPolylinesOnMap.forEach(polyline -> map.removeMapShape(polyline));
            optimizionAlgorithmPolylinesOnMap.clear();
        });
    }

    public void changeOptimizationAlgorithmSolutionVisibility(boolean isSelectedChboxOptimizationAlgorithm) {
        Platform.runLater(() -> optimizionAlgorithmPolylinesOnMap.forEach(polyline -> polyline.setVisible(isSelectedChboxOptimizationAlgorithm)));
    }

    public void changeBasicAlgorithmSolutionVisibility(boolean isSelectedChboxBasicAlgorithm) {
        Platform.runLater(() -> basicAlgorithmPolylinesOnMap.forEach(polyline -> polyline.setVisible(isSelectedChboxBasicAlgorithm)));
    }
}
