package tsp.data.loader;

import lombok.Getter;
import tsp.domain.PointsDistance;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CitiesDistancesLoader {
    private static final String DISTANCES_16_CITIES = "src/main/resources/16_polish_cities_distances.txt";
//    private static final String DISTANCES_16_CITIES = "16_polish_cities_distances.txt";
    private static final String DISTANCES_50_CITIES = "src/main/resources/50_polish_cities_distances.txt";
//    private static final String DISTANCES_50_CITIES = "50_polish_cities_distances.txt";
    private static final String DISTANCES_100_CITIES = "src/main/resources/100_polish_cities_distances.txt";
//    private static final String DISTANCES_100_CITIES = "100_polish_cities_distances.txt";

    public static final List<PointsDistance> DISTANCES_16 = load(DISTANCES_16_CITIES);
    public static final List<PointsDistance> DISTANCES_50 = load(DISTANCES_50_CITIES);
    public static final List<PointsDistance> DISTANCES_100 = load(DISTANCES_100_CITIES);

    private static List<PointsDistance> load(String fileName) {
        List<PointsDistance> pointsDistances = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
            br.lines().forEach(line -> {
                String[] splited = line.split("\\s{2,}");
                PointsDistance pointsDistance = new PointsDistance();
                pointsDistance.setPointA(splited[0]);
                pointsDistance.setPointB(splited[1]);
                pointsDistance.setDistance(Integer.valueOf(splited[2]));
                pointsDistances.add(pointsDistance);
            });
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pointsDistances;
    }
}
