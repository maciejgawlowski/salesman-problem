package tsp.data.loader;

import com.google.common.base.CharMatcher;
import lombok.Getter;
import tsp.domain.City;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CitiesLoader {
    private static final String CITIES_16_FILENAME = "src/main/resources/16_polish_cities.txt";
//    private static final String CITIES_16_FILENAME = "16_polish_cities.txt";
    private static final String CITIES_50_FILENAME = "src/main/resources/50_polish_cities.txt";
//    private static final String CITIES_50_FILENAME = "50_polish_cities.txt";
    private static final String CITIES_100_FILENAME = "src/main/resources/100_polish_cities.txt";
//    private static final String CITIES_100_FILENAME = "100_polish_cities.txt";

    public static final List<City> CITIES_16 = load(CITIES_16_FILENAME);
    public static final List<City> CITIES_50 = load(CITIES_50_FILENAME);
    public static final List<City> CITIES_100 = load(CITIES_100_FILENAME);

    private static List<City> load(String fileName) {
        List<City> cities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(new File(fileName)))) {
            br.lines().forEach(line -> {
                City city = new City();
                city.setName(line.split("\\s{2,}")[0]);
                String coordiantes = CharMatcher.inRange('0', '9').retainFrom(line);
                city.setLongitude(Double.valueOf(coordiantes.substring(0, 2)) + Double.valueOf(coordiantes.substring(2, 4)) / 60.0);
                city.setLatitude(Double.valueOf(coordiantes.substring(4, 6)) + Double.valueOf(coordiantes.substring(6, 8)) / 60.0);
                cities.add(city);
            });
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cities;
    }
}
