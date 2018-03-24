package routes.calculators

import tsp.domain.PointsDistance
import tsp.domain.TSPResult
import tsp.routes.calculators.TwoOptCalc
import spock.lang.Specification
import spock.lang.Unroll
import tsp.data.loader.CitiesDistancesLoader

@Unroll
class TwoOptCalcSpec extends Specification {
    private static final String DISTANCES_FILENAME = "D:/IdeaProjects/salesman-problem/src/test/resources/test_main_polish_cities_distances.txt"

    List<PointsDistance> pointsDistances

    def setup() {
        pointsDistances = CitiesDistancesLoader.load(DISTANCES_FILENAME)
    }

    def "Should do swap between Szczecin-Bia�ystok-Gda�sk-Rzesz�w"() {
        expect:
        TwoOptCalc.getPath(pointsDistances, currentResult) == expectedResult

        where:
        currentResult << [
                TSPResult.builder().pointsOrder(Arrays.asList("Szczecin", "Bia�ystok", "Gda�sk", "Rzesz�w", "Wroc�aw", "Szczecin")).totalDistance(572+328+532+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Bia�ystok", "Gda�sk", "Rzesz�w", "Wroc�aw", "Szczecin", "Bia�ystok")).totalDistance(572+328+532+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Gda�sk", "Rzesz�w", "Wroc�aw", "Szczecin", "Bia�ystok", "Gda�sk")).totalDistance(572+328+532+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Rzesz�w", "Wroc�aw", "Szczecin", "Bia�ystok", "Gda�sk", "Rzesz�w")).totalDistance(572+328+532+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Wroc�aw", "Szczecin", "Bia�ystok", "Gda�sk", "Rzesz�w", "Wroc�aw")).totalDistance(572+328+532+371+307).build()
        ]

        expectedResult << [
                TSPResult.builder().pointsOrder(Arrays.asList("Szczecin", "Gda�sk", "Bia�ystok", "Rzesz�w", "Wroc�aw", "Szczecin")).totalDistance(286+328+352+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Bia�ystok", "Gda�sk", "Szczecin", "Wroc�aw", "Rzesz�w", "Bia�ystok")).totalDistance(286+328+352+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Gda�sk", "Szczecin", "Wroc�aw", "Rzesz�w", "Bia�ystok", "Gda�sk")).totalDistance(286+328+352+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Rzesz�w", "Wroc�aw", "Szczecin", "Gda�sk", "Bia�ystok", "Rzesz�w")).totalDistance(286+328+352+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Wroc�aw", "Szczecin", "Gda�sk", "Bia�ystok", "Rzesz�w", "Wroc�aw")).totalDistance(286+328+352+371+307).build()
        ]
    }

    def "Should not do swap"() {
        given:
        TSPResult currentResult = TSPResult.builder().pointsOrder(Arrays.asList("Szczecin", "Gda�sk", "Bia�ystok", "Rzesz�w", "Wroc�aw", "Szczecin")).totalDistance(286+328+352+371+307).build()

        expect:
        TwoOptCalc.getPath(pointsDistances, currentResult) == currentResult
    }
}