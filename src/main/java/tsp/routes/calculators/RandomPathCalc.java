package tsp.routes.calculators;

import controller.MapShapeDrawer;
import tsp.domain.City;
import tsp.domain.PointsDistance;
import tsp.domain.TSPResult;

import java.util.*;

public class RandomPathCalc {
    private MapShapeDrawer mapShapeDrawer;

    public RandomPathCalc(MapShapeDrawer mapShapeDrawer) {
        this.mapShapeDrawer = mapShapeDrawer;
    }

    public TSPResult getPath(List<City> cities, List<PointsDistance> citiesDistances) {
        List<City> points = new ArrayList<>(cities);
        City startPoint = points.get((new Random()).nextInt(points.size()));
        buildPointsOrder(points, startPoint);
        int totalDistance = calculateDistanceAndDrawLineOnMap(points, citiesDistances);

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
        while (iterator.hasNext()) {
            System.out.println("Current point: " + currentPoint);
            City nextPoint = iterator.next();
            totalDistance += pointsDistances.stream().filter(Predicates.findDistance(currentPoint.getName(), nextPoint.getName())).map(PointsDistance::getDistance).findAny().orElseThrow(NullPointerException::new);
            mapShapeDrawer.addLineToBasicAlgorithmPolylines(currentPoint, nextPoint);
            currentPoint = nextPoint;
        }
        return totalDistance;
    }
}