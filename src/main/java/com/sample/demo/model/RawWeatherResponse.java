package com.sample.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class RawWeatherResponse {

    @JsonProperty("name")
    private String city;
    @JsonProperty("id")
    private String cityId;
    @JsonProperty("timezone")
    private String timezone;
    @JsonProperty("coord")
    private Coordinates coordinates;
    @JsonProperty("weather")
    private List<Weather> weather;
    @JsonProperty("base")
    private String base;
    @JsonProperty("main")
    private MainTemperature mainTemperature;
    @JsonProperty("visibility")
    private Integer visibility;
    @JsonProperty("wind")
    private Wind wind;
    @JsonProperty("clouds")
    private Clouds clouds;
    @JsonProperty("dt")
    private Long dateTime;
    @JsonProperty("sys")
    private RiseAndSet riseAndSet;

    @Data
    public static class Wind {
        @JsonProperty("speed")
        private Double speed;
        @JsonProperty("deg")
        private Integer direction;
        @JsonProperty("gust")
        private Double gust;
    }
    @Data
    public static class Weather {
        private Integer id;
        private String main;
        private String description;
        private String icon;
    }
    @Data
    public static class RiseAndSet {
        @JsonProperty("country")
        private String country;
        @JsonProperty("sunrise")
        private Long sunrise;
        @JsonProperty("sunset")
        private Long sunset;
    }
    @Data
    public static class MainTemperature {
        @JsonProperty("temp")
        private Double temperature;
        @JsonProperty("feels_like")
        private Double feelsLike;
        @JsonProperty("temp_min")
        private Double temperatureMinimum;
        @JsonProperty("temp_max")
        private Double temperatureMaximum;
        @JsonProperty("pressure")
        private Integer pressure;
        @JsonProperty("humidity")
        private Integer humidity;
        @JsonProperty("sea_level")
        private Integer seaLevel;
        @JsonProperty("grnd_level")
        private Integer groundLevel;
    }
    @Data
    public static class Coordinates {
        @JsonProperty("lat")
        private double latitude;
        @JsonProperty("lon")
        private double longitude;
    }
    @Data
    public static class Clouds {
        @JsonProperty("all")
        private Integer cloudyPercentage;
    }

}
