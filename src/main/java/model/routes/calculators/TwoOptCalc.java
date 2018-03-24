package model.routes.calculators;

import com.google.common.collect.Lists;
import controller.MapShapeDrawer;
import model.domain.City;
import model.domain.PointsDistance;
import model.domain.TSPResult;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static model.routes.calculators.Predicates.findDistance;

public class TwoOptCalc {
    private MapShapeDrawer mapShapeDrawer;

    public TwoOptCalc(MapShapeDrawer mapShapeDrawer) {
        this.mapShapeDrawer = mapShapeDrawer;
    }

    public TSPResult getPath(List<PointsDistance> pointsDistances, TSPResult currentResult) {
        int bestDistance = currentResult.getTotalDistance();
        List<City> bestRoute = currentResult.getPointsOrder();

        outerLoop:
        while (true) {
            for (int i = 1; i < bestRoute.size() - 1; i++) {
                for (int k = i + 1; k < bestRoute.size() - 1; k++) {
                    long time = System.currentTimeMillis();
                    int newDistance = recalculate(i, k, bestRoute, bestDistance, pointsDistances);
                    System.out.println("[" + i + "." + k + "] calculate distance duration: " + (System.currentTimeMillis() - time));
                    if (newDistance < bestDistance) {
                        System.out.println("newBestDistance: " + newDistance + " previousBestDistance: " + bestDistance + " (i = " + i + ", k = " + k + ")");
                        List<City> newPointsOrder = swapTwoEdges(bestRoute, i, k);
                        bestDistance = newDistance;
                        bestRoute = newPointsOrder;
                        continue outerLoop;
                    }
                }
            }
            break;
        }

        drawNewRoute(bestRoute);
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

    private int recalculate(int i, int k, List<City> bestRoute, int bestDistance, List<PointsDistance> pointsDistances) {
        return bestDistance
                - pointsDistances.stream().filter(findDistance(bestRoute.get(i - 1).getName(), bestRoute.get(i).getName())).map(PointsDistance::getDistance).findAny().orElseThrow(NullPointerException::new)
                - pointsDistances.stream().filter(findDistance(bestRoute.get(k).getName(), bestRoute.get(k + 1).getName())).map(PointsDistance::getDistance).findAny().orElseThrow(NullPointerException::new)
                + pointsDistances.stream().filter(findDistance(bestRoute.get(i - 1).getName(), bestRoute.get(k).getName())).map(PointsDistance::getDistance).findAny().orElseThrow(NullPointerException::new)
                + pointsDistances.stream().filter(findDistance(bestRoute.get(i).getName(), bestRoute.get(k + 1).getName())).map(PointsDistance::getDistance).findAny().orElseThrow(NullPointerException::new);
    }

    private void drawNewRoute(List<City> bestRoute) {
        mapShapeDrawer.clearOptimizionAlgorithmPolylines();
        Iterator<City> routeIterator = bestRoute.iterator();
        City currentCity = routeIterator.next();
        while (routeIterator.hasNext()) {
            City nextCity = routeIterator.next();
            mapShapeDrawer.addLineToOptimizionAlgorithmPolylines(currentCity, nextCity);
            currentCity = nextCity;
        }
    }
}
