package model.routes.calculators;

import com.google.common.collect.Lists;
import controller.MainController;
import lombok.Setter;
import model.domain.City;
import model.domain.PointsDistance;
import model.domain.TSPResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static model.routes.calculators.Predicates.findDistance;

public class TwoOptCalc {
    @Setter
    private MainController mainController;

    public TSPResult getPath(List<PointsDistance> pointsDistances, TSPResult currentResult) {
        int bestDistance = currentResult.getTotalDistance();
        List<City> bestRoute = currentResult.getPointsOrder();

        outerLoop:
        while (true) {
            for (int i = 1; i < bestRoute.size() - 1; i++) {
                for (int k = i + 1; k < bestRoute.size() - 1; k++) {
                    List<City> newPointsOrder = swapTwoEdges(bestRoute, i, k);
                    int newDistance = calculateTotalDistance(newPointsOrder, pointsDistances);
                    if (newDistance < bestDistance) {
//                        System.out.println("newBestDistance: " + newDistance + " previousBestDistance: " + bestDistance + " (i = " + i + ", k = " + k + ")");
                        bestDistance = newDistance;
                        bestRoute = newPointsOrder;
                        drawNewRoute(bestRoute);
                        continue outerLoop;
                    }
                }
            }
            break;
        }

        return TSPResult.builder().totalDistance(bestDistance).pointsOrder(bestRoute).build();
    }

    private List<City> swapTwoEdges(List<City> pointsOrder, int i, int k) {
        List<City> newRoute = new ArrayList<>();
//        1. take route[0] to route[i-1] and add them in order to new_route
        newRoute.addAll(pointsOrder.subList(0, i)); //last element (here: i) is not included in sublist
//        2. take route[i] to route[k] and add them in reverse order to new_route
        newRoute.addAll(Lists.reverse(pointsOrder.subList(i, k + 1)));
//        3. take route[k+1] to end and add them in order to new_route
        newRoute.addAll(pointsOrder.subList(k + 1, pointsOrder.size()));
        return newRoute;
    }

    private int calculateTotalDistance(List<City> newPointsOrder, List<PointsDistance> pointsDistances) {
        int totalDistance = 0;
        Iterator<City> iterator = newPointsOrder.iterator();
        City currentPoint = iterator.next();
        while (iterator.hasNext()) {
            City nextPoint = iterator.next();
            totalDistance += pointsDistances.stream().filter(findDistance(currentPoint.getName(), nextPoint.getName())).map(PointsDistance::getDistance).findAny().orElseThrow(NullPointerException::new);
            currentPoint = nextPoint;
        }
        return totalDistance;
    }

    private void drawNewRoute(List<City> bestRoute) {
        mainController.clearOptimizionAlgorithmPolylines();
        Iterator<City> routeIterator = bestRoute.iterator();
        City currentCity = routeIterator.next();
        while (routeIterator.hasNext()) {
            City nextCity = routeIterator.next();
            mainController.addLineToOptimizionAlgorithmPolylines(currentCity, nextCity);
            currentCity = nextCity;
        }
    }
}
