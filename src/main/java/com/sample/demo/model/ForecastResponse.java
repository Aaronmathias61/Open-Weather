package com.sample.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class ForecastResponse {
    @JsonProperty("cod")
    private String cod;
    @JsonProperty("message")
    private int message;
    @JsonProperty("cnt")
    private int cnt;
    @JsonProperty("list")
    private List<WeatherData> list;
    @JsonProperty("city")
    private City city;
    @Data
    public static class WeatherData {
        @JsonProperty("dt")
        private long dt;
        @JsonProperty("main")
        private Main main;
        @JsonProperty("weather")
        private List<Weather> weather;
        @JsonProperty("clouds")
        private Clouds clouds;
        @JsonProperty("wind")
        private Wind wind;
        @JsonProperty("visibility")
        private int visibility;
        @JsonProperty("pop")
        private double pop;
        @JsonProperty("sys")
        private Sys sys;
        @JsonProperty("dt_txt")
        private String dtTxt;
    }

    @Data
    public static class Main {
        @JsonProperty("temp")
        private double temperature;
        @JsonProperty("feels_like")
        private double feelsLike;
        @JsonProperty("temp_min")
        private double temperatureMinimum;
        @JsonProperty("temp_max")
        private double temperatureMaximum;
        @JsonProperty("pressure")
        private int pressure;
        @JsonProperty("sea_level")
        private int seaLevel;
        @JsonProperty("grnd_level")
        private int groundLevel;
        @JsonProperty("humidity")
        private int humidity;
        @JsonProperty("temp_kf")
        private double tempKf;
    }
    @Data
    public static class Clouds {
        @JsonProperty("all")
        private int all;
    }
    @Data
    public static class Wind {
        @JsonProperty("speed")
        private double speed;
        @JsonProperty("deg")
        private int deg;
        @JsonProperty("gust")
        private double gust;
    }
    @Data
    public static class Sys {
        @JsonProperty("pod")
        private String pod;
    }
    @Data
    public static class City {
        @JsonProperty("id")
        private int id;
        @JsonProperty("name")
        private String name;
        @JsonProperty("coord")
        private Coordinates coordinates;
        @JsonProperty("country")
        private String country;
        @JsonProperty("population")
        private int population;
        @JsonProperty("timezone")
        private int timezone;
        @JsonProperty("sunrise")
        private long sunrise;
        @JsonProperty("sunset")
        private long sunset;
    }
    @Data
    public static class Coordinates {
        @JsonProperty("lat")
        private double latitude;
        @JsonProperty("lon")
        private double longitude;
    }
}
