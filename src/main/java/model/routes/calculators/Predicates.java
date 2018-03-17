package model.routes.calculators;

import model.domain.PointsDistance;

import java.util.function.Predicate;

public class Predicates {

    public static Predicate<PointsDistance> findDistance(String currentPoint, String nextPoint) {
        return pointsDistance ->
                pointsDistance.getPointA().equals(currentPoint) && pointsDistance.getPointB().equals(nextPoint)
                        || pointsDistance.getPointB().equals(currentPoint) && pointsDistance.getPointA().equals(nextPoint);
    }
}
