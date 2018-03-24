package tsp.routes.calculators;

import controller.MainController;
import controller.MapShapeDrawer;
import tsp.domain.City;
import tsp.domain.PointsDistance;
import tsp.domain.TSPResult;

import java.util.*;

public class RandomPathCalc {
    private MapShapeDrawer mapShapeDrawer;
    private MainController mainController;

    public RandomPathCalc(MapShapeDrawer mapShapeDrawer, MainController mainController) {
        this.mapShapeDrawer = mapShapeDrawer;
        this.mainController = mainController;
    }

    public TSPResult getPath(List<City> cities, List<PointsDistance> citiesDistances) {
        mainController.log("Random Path TSP resolving started");
        List<City> points = new ArrayList<>(cities);
        City startPoint = points.get((new Random()).nextInt(points.size()));
        buildPointsOrder(points, startPoint);
        int totalDistance = calculateDistanceAndDrawLineOnMap(points, citiesDistances);
        mainController.log("Random Path TSP resolving finished");

        return TSPResult.builder().pointsOrder(points).totalDistance(totalDistance).build();
    }

    private void buildPointsOrder(List<City> points, City startPoint) {
        points.remove(startPoint);
        Collections.shuffle(points);
        points.add(0, startPoint);
        points.add(startPoint);
    }

    private int calculateDistanceAndDrawLineOnMap(List<City> points, List<PointsDistance> pointsDistances) {
        int totalDistance = 0;
        Iterator<City> iterator = points.iterator();
        City currentPoint = iterator.next();
        mainController.log("Start point: " + currentPoint.getName());
        while (iterator.hasNext()) {
            City nextPoint = iterator.next();
            mainController.log("Next point " + nextPoint.getName());
            totalDistance += pointsDistances.stream().filter(Predicates.findDistance(currentPoint.getName(), nextPoint.getName())).map(PointsDistance::getDistance).findAny().orElseThrow(NullPointerException::new);
            mapShapeDrawer.addLineToBasicAlgorithmPolylines(currentPoint, nextPoint);
            currentPoint = nextPoint;
        }
        return totalDistance;
    }
}