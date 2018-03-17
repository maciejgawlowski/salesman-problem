package model.domain;

import lombok.Data;

@Data
public class City {
    private String name;
    private double latitude; //converted to decimal
    private double longitude; //converted to decimal
}
