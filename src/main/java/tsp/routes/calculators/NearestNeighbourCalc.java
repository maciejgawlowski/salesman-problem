package tsp.routes.calculators;

import controller.MainController;
import controller.MapShapeDrawer;
import javafx.application.Platform;
import javafx.scene.control.TextArea;
import org.joda.time.DateTime;
import tsp.domain.City;
import tsp.domain.PointsDistance;
import tsp.domain.TSPResult;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static tsp.routes.calculators.Predicates.findDistance;

public class NearestNeighbourCalc {
    private MapShapeDrawer mapShapeDrawer;
    private MainController mainController;

    public NearestNeighbourCalc(MapShapeDrawer mapShapeDrawer, MainController mainController) {
        this.mapShapeDrawer = mapShapeDrawer;
        this.mainController = mainController;
    }

    public TSPResult getPath(List<City> cities, List<PointsDistance> citiesDistances) {
        mainController.log("Nearest Neighbour TSP resolving started");
        List<City> points = new ArrayList<>(cities);
        List<City> pointsOrder = new ArrayList<>();
        int totalDistance = 0;
        City startPoint = points.get((new Random()).nextInt(points.size()));
        mainController.log("Start point: " + startPoint.getName());

        City currentPoint = startPoint;
        pointsOrder.add(currentPoint);
        while (!allCitiesAreVisited(points)) {
            PointsDistance nearestNeighbour = getNearestNeighbourPointsDistance(citiesDistances, points, currentPoint);
            String nearestNeighbourPoint = nearestNeighbour.getPointA().equals(currentPoint.getName()) ? nearestNeighbour.getPointB() : nearestNeighbour.getPointA();
            mainController.log("Next point " + nearestNeighbourPoint);
            pointsOrder.add(points.stream().filter(city -> city.getName().equals(nearestNeighbourPoint)).findAny().orElseThrow(NullPointerException::new));
            mapShapeDrawer.addLineToBasicAlgorithmPolylines(currentPoint, points.stream().filter(point -> point.getName().equals(nearestNeighbourPoint)).findAny().get());
            totalDistance += nearestNeighbour.getDistance();
            points.remove(currentPoint);
            currentPoint = points.stream().filter(point -> point.getName().equals(nearestNeighbourPoint)).findAny().get();
        }

        totalDistance += citiesDistances.stream().filter(findDistance(points.get(0).getName(), startPoint.getName())).map(PointsDistance::getDistance).findAny().orElseThrow(NullPointerException::new);
        mapShapeDrawer.addLineToBasicAlgorithmPolylines(currentPoint, startPoint);
        pointsOrder.add(startPoint);
        mainController.log("End point: " + startPoint.getName());
        mainController.log("Nearest Neighbour TSP resolving finished");

        return TSPResult.builder().pointsOrder(pointsOrder).totalDistance(totalDistance).build();
    }

    private boolean allCitiesAreVisited(List<City> points) {
        return points.size() == 1;
    }

    private PointsDistance getNearestNeighbourPointsDistance(List<PointsDistance> pointsDistances, List<City> points, City currentPoint) {
        return pointsDistances.stream()
                .filter(pointsDistance -> (pointsDistance.getPointA().equals(currentPoint.getName()) && points.stream().map(City::getName).collect(Collectors.toList()).contains(pointsDistance.getPointB()))
                        || (pointsDistance.getPointB().equals(currentPoint.getName()) && points.stream().map(City::getName).collect(Collectors.toList()).contains(pointsDistance.getPointA())))
                .min(Comparator.comparing(PointsDistance::getDistance))
                .orElseThrow(NullPointerException::new);
    }
}