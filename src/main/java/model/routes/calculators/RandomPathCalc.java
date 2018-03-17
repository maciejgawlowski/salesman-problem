package model.routes.calculators;

import controller.MainController;
import lombok.Setter;
import model.domain.City;
import model.domain.PointsDistance;
import model.domain.TSPResult;

import java.util.*;

public class RandomPathCalc {
    @Setter
    private MainController mainController;

    public TSPResult getPath(List<City> cities, List<PointsDistance> citiesDistances) {
//        List<String> points = cities.stream().map(City::getName).collect(Collectors.toList());
        List<City> points = new ArrayList<>(cities);;
//        String startPoint = points.get((new Random()).nextInt(points.size()));
        City startPoint = points.get((new Random()).nextInt(points.size()));
        buildPointsOrder(points, startPoint);
        int totalDistance = calculateDistanceAndDrawLineOnMap(points, citiesDistances);

//        Iterator<String> pointsIterator = points.iterator();
//        String currentPoint = pointsIterator.next();
//        while (pointsIterator.hasNext()){
//            String nextPoint = pointsIterator.next();
//            mainController.addLineToBasicAlgorithmPolylines(currentPoint, nextPoint);

//        }

        return TSPResult.builder().pointsOrder(points).totalDistance(totalDistance).build();
    }

//    private void buildPointsOrder(List<String> points, String startPoint) {
    private void buildPointsOrder(List<City> points, City startPoint) {
        points.remove(startPoint);
        Collections.shuffle(points);
        points.add(0, startPoint);
        points.add(startPoint);
    }

//    private int calculateDistanceAndDrawLineOnMap(List<String> points, List<PointsDistance> pointsDistances) {
    private int calculateDistanceAndDrawLineOnMap(List<City> points, List<PointsDistance> pointsDistances) {
        int totalDistance = 0;
        Iterator<City> iterator = points.iterator();
        City currentPoint = iterator.next();
        while (iterator.hasNext()) {
//            System.out.println("Current point: " + currentPoint);
            City nextPoint = iterator.next();
            totalDistance += pointsDistances.stream().filter(Predicates.findDistance(currentPoint.getName(), nextPoint.getName())).map(PointsDistance::getDistance).findAny().orElseThrow(NullPointerException::new);
            mainController.addLineToBasicAlgorithmPolylines(currentPoint, nextPoint);
            currentPoint = nextPoint;
        }
        return totalDistance;
    }

}
