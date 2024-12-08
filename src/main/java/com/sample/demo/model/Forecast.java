package com.sample.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Forecast {
    private LocalDate date;
    private Double averageTemperature;
    private Double maximumTemperature;
    private Double minimumTemperature;
    private String weather;
}
