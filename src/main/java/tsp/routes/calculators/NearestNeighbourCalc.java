package tsp.routes.calculators;

import controller.MapShapeDrawer;
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

    public NearestNeighbourCalc(MapShapeDrawer mapShapeDrawer) {
        this.mapShapeDrawer = mapShapeDrawer;
    }

    public TSPResult getPath(List<City> cities, List<PointsDistance> citiesDistances) {
        List<City> points = new ArrayList<>(cities);
        List<City> pointsOrder = new ArrayList<>();
        int totalDistance = 0;
        City startPoint = points.get((new Random()).nextInt(points.size()));

        City currentPoint = startPoint;
        pointsOrder.add(currentPoint);
        while (!allCitiesAreVisited(points)) {
            System.out.println("Current point: " + currentPoint);
            PointsDistance nearestNeighbour = getNearestNeighbourPointsDistance(citiesDistances, points, currentPoint);
            String nearestNeighbourPoint = nearestNeighbour.getPointA().equals(currentPoint.getName()) ? nearestNeighbour.getPointB() : nearestNeighbour.getPointA();
            pointsOrder.add(points.stream().filter(city -> city.getName().equals(nearestNeighbourPoint)).findAny().orElseThrow(NullPointerException::new));
            mapShapeDrawer.addLineToBasicAlgorithmPolylines(currentPoint, points.stream().filter(point -> point.getName().equals(nearestNeighbourPoint)).findAny().get());
            totalDistance += nearestNeighbour.getDistance();

            points.remove(currentPoint);
            currentPoint = points.stream().filter(point -> point.getName().equals(nearestNeighbourPoint)).findAny().get();
        }

        totalDistance += citiesDistances.stream().filter(findDistance(points.get(0).getName(), startPoint.getName())).map(PointsDistance::getDistance).findAny().orElseThrow(NullPointerException::new);
        mapShapeDrawer.addLineToBasicAlgorithmPolylines(currentPoint, startPoint);
        pointsOrder.add(startPoint);

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