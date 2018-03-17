package model.domain;

import lombok.*;

import java.util.List;

@ToString
@Getter
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class TSPResult {
//    private List<String> pointsOrder;
    private List<City> pointsOrder;
    private int totalDistance;
}
