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

    def "Should do swap between Szczecin-Bia³ystok-Gdañsk-Rzeszów"() {
        expect:
        TwoOptCalc.getPath(pointsDistances, currentResult) == expectedResult

        where:
        currentResult << [
                TSPResult.builder().pointsOrder(Arrays.asList("Szczecin", "Bia³ystok", "Gdañsk", "Rzeszów", "Wroc³aw", "Szczecin")).totalDistance(572+328+532+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Bia³ystok", "Gdañsk", "Rzeszów", "Wroc³aw", "Szczecin", "Bia³ystok")).totalDistance(572+328+532+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Gdañsk", "Rzeszów", "Wroc³aw", "Szczecin", "Bia³ystok", "Gdañsk")).totalDistance(572+328+532+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Rzeszów", "Wroc³aw", "Szczecin", "Bia³ystok", "Gdañsk", "Rzeszów")).totalDistance(572+328+532+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Wroc³aw", "Szczecin", "Bia³ystok", "Gdañsk", "Rzeszów", "Wroc³aw")).totalDistance(572+328+532+371+307).build()
        ]

        expectedResult << [
                TSPResult.builder().pointsOrder(Arrays.asList("Szczecin", "Gdañsk", "Bia³ystok", "Rzeszów", "Wroc³aw", "Szczecin")).totalDistance(286+328+352+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Bia³ystok", "Gdañsk", "Szczecin", "Wroc³aw", "Rzeszów", "Bia³ystok")).totalDistance(286+328+352+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Gdañsk", "Szczecin", "Wroc³aw", "Rzeszów", "Bia³ystok", "Gdañsk")).totalDistance(286+328+352+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Rzeszów", "Wroc³aw", "Szczecin", "Gdañsk", "Bia³ystok", "Rzeszów")).totalDistance(286+328+352+371+307).build(),
                TSPResult.builder().pointsOrder(Arrays.asList("Wroc³aw", "Szczecin", "Gdañsk", "Bia³ystok", "Rzeszów", "Wroc³aw")).totalDistance(286+328+352+371+307).build()
        ]
    }

    def "Should not do swap"() {
        given:
        TSPResult currentResult = TSPResult.builder().pointsOrder(Arrays.asList("Szczecin", "Gdañsk", "Bia³ystok", "Rzeszów", "Wroc³aw", "Szczecin")).totalDistance(286+328+352+371+307).build()

        expect:
        TwoOptCalc.getPath(pointsDistances, currentResult) == currentResult
    }
}