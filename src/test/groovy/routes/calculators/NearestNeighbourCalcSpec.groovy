import com.google.common.collect.Iterables
import tsp.domain.PointsDistance
import tsp.routes.calculators.NearestNeighbourCalc
import tsp.data.loader.CitiesDistancesLoader
import tsp.data.loader.CitiesLoader
import spock.lang.Specification
import spock.lang.Unroll

@Unroll
class NearestNeighbourCalcSpec extends Specification {
    private static final String FILENAME_CITIES = "D:/IdeaProjects/salesman-problem/src/test/resources/test_main_polish_cities.txt"
    private static final String DISTANCES_FILENAME = "D:/IdeaProjects/salesman-problem/src/test/resources/test_main_polish_cities_distances.txt"

    List<String> points
    List<PointsDistance> pointsDistances
    String startPoint

    def setup() {
        points = CitiesLoader.load(FILENAME_CITIES)
        pointsDistances = CitiesDistancesLoader.load(DISTANCES_FILENAME)
        startPoint = "Pozna�"
    }

    def "Result points size should be bigger by one than input points"() {
        expect:
        NearestNeighbourCalc.getPath(points, pointsDistances).pointsOrder.size() == 17
    }

    def "Result points first element should be start point"() {
        expect:
        NearestNeighbourCalc.getPath(points, pointsDistances).pointsOrder.get(0) == startPoint
    }

    def "Result points last element should be start point"() {
        expect:
        Iterables.getLast(NearestNeighbourCalc.getPath(points, pointsDistances).pointsOrder) == startPoint
    }

    def "Assert point position"() {
        expect:
        NearestNeighbourCalc.getPath(points, pointsDistances).pointsOrder.get(i) == point

        where:
        i  || point
        0  || "Pozna�"
        1  || "Bydgoszcz"
        2  || "Gda�sk"
        3  || "Olsztyn"
        4  || "Warszawa"
        5  || "��d�"
        6  || "Kielce"
        7  || "Krak�w"
        8  || "Katowice"
        9  || "Opole"
        10 || "Wroc�aw"
        11 || "Gorz�w Wielkopolski"
        12 || "Szczecin"
        13 || "Bia�ystok"
        14 || "Lublin"
        15 || "Rzesz�w"
        16 || "Pozna�"
    }

    def "Assert total distance"() {
        def expectedDistance = 107 + 145 + 138 + 180 + 117 + 128 + 104 + 71 + 89 + 81 + 218 + 90 + 572 + 215 + 137 + 442

        expect:
        NearestNeighbourCalc.getPath(points, pointsDistances).totalDistance == expectedDistance
    }
}