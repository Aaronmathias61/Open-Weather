package com.sample.demo.model;

import lombok.Data;

@Data
public class WeatherResponse {
    private String city;
    private String timezone;
    private Double temperature;
    private Integer humidity;
    private String weatherDescription;
    private Double windSpeed;
    private Double comfortIndex;
}
