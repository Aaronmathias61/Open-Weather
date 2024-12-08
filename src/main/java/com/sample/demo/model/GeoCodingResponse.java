package com.sample.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GeoCodingResponse {
    @JsonProperty("name")
    private String name;
    @JsonProperty("local_names")
    private Map<String, String> localNames;
    @JsonProperty("lat")
    private double latitude;
    @JsonProperty("lon")
    private double longitude;
    @JsonProperty("country")
    private String country;
    @JsonProperty("state")
    private String state;

    public GeoCodingResponse(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
