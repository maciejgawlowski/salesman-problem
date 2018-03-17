package model.data.loader;

import com.google.common.base.CharMatcher;
import lombok.Getter;
import model.domain.City;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Getter
public class CitiesLoader {
    private static final String FILENAME_MAIN_CITIES = "D:/IdeaProjects/salesman-problem/src/main/resources/main_polish_cities.txt";
    private static final String FILENAME_ALL_CITIES = "D:/IdeaProjects/salesman-problem/src/main/resources/polish_cities.txt";

    public static final List<City> MAIN_CITIES = load(FILENAME_MAIN_CITIES);
    public static final List<City> ALL_CITIES = load(FILENAME_ALL_CITIES);

    private static List<City> load(String fileName) {
        List<City> cities = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
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
