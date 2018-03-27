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
//    private static final String DISTANCES_FILENAME_MAIN_CITIES = "src/main/resources/main_polish_cities_distances.txt";
    private static final String DISTANCES_FILENAME_MAIN_CITIES = "main_polish_cities_distances.txt";
//    private static final String DISTANCES_FILENAME_ALL_CITIES = "src/main/resources/polish_cities_distances.txt";
    private static final String DISTANCES_FILENAME_ALL_CITIES = "polish_cities_distances.txt";

    public static final List<PointsDistance> MAIN_CITIES_DISTANCES = load(DISTANCES_FILENAME_MAIN_CITIES);
    public static final List<PointsDistance> ALL_CITIES_DISTANCES = load(DISTANCES_FILENAME_ALL_CITIES);

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
