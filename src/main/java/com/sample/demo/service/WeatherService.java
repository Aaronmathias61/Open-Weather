package com.sample.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sample.demo.constant.Constant;
import com.sample.demo.exception.GlobalException;
import com.sample.demo.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class WeatherService {
    @Value("${weather.api.key}")
    private String apiKey;
    @Value("${weather.url}")
    private String weatherURL;
    @Value("${geocode.url}")
    private String goeCodeURL;
    @Value("${forecast.url}")
    private String forecastURL;
    Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final RestTemplate restTemplate;


    public WeatherService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Cacheable(value = Constant.WEATHER_CACHE, key = Constant.WEATHER_CACHE_KEY, unless = Constant.UNLESS_RESULT)
    public WeatherResponse getWeather(String cityName) throws JsonProcessingException {
        logger.info(String.format(Constant.FETCHING_WEATHER_DATA, cityName, Instant.now().toString()));
        try {
            String url = String.format(weatherURL, cityName, apiKey);
            ResponseEntity<RawWeatherResponse> weatherResponse = restTemplate.getForEntity(url, RawWeatherResponse.class);
            logger.info(String.format(Constant.FETCHED_WEATHER_DATA, cityName, Instant.now().toString()));
            return parseWeatherResponse(weatherResponse.getBody());
        } catch (HttpClientErrorException.NotFound exception) {
            logger.error(String.format(Constant.CITY_NOT_FOUND_EXCEPTION, cityName, Instant.now().toString()));
            throw new GlobalException(String.format(Constant.CITY_NOT_FOUND, cityName));
        } catch (HttpClientErrorException.Unauthorized exception) {
            logger.error(String.format(Constant.UNAUTHORIZED_ACCESS_EXCEPTION, Instant.now().toString()));
            throw new GlobalException(Constant.UNAUTHORIZED_ACCESS);
        }
    }

    private WeatherResponse parseWeatherResponse(RawWeatherResponse body) throws JsonProcessingException {
        WeatherResponse weatherResponse = new WeatherResponse();
        weatherResponse.setCity(body.getCity());
        weatherResponse.setWeatherDescription(body.getWeather().get(0).getDescription());
        weatherResponse.setTimezone(body.getTimezone());
        weatherResponse.setHumidity(body.getMainTemperature().getHumidity());
        weatherResponse.setTemperature(body.getMainTemperature().getTemperature());
        weatherResponse.setWindSpeed(body.getWind().getSpeed());
        weatherResponse.setComfortIndex(body.getMainTemperature().getTemperature() - (body.getMainTemperature().getHumidity() / 5));
        return weatherResponse;
    }

    @Cacheable(value = Constant.FORECAST_CACHE, key = Constant.FORECAST_CACHE_KEY, unless = Constant.UNLESS_RESULT)
    public List<Forecast> getWeatherForecast(String cityName, Integer numberOfDays) {
        logger.info(String.format(Constant.FETCHING_FORECAST_DATA, cityName, Instant.now().toString()));
        try {
//            String actualUr = "https://10.255.255.1:8080";
            String geoCode = String.format(goeCodeURL, cityName, apiKey);
            ResponseEntity<GeoCodingResponse[]> geoCodeResponse = restTemplate.getForEntity(geoCode, GeoCodingResponse[].class);
            GeoCodingResponse[] geoCodingResponses = geoCodeResponse.getBody();
            GeoCodingResponse geoCodingResponse = null;
            if (geoCodingResponses.length > 0) {
                geoCodingResponse = geoCodingResponses[0];
            } else {
                logger.error(String.format(Constant.CITY_NOT_FOUND_EXCEPTION, cityName, Instant.now().toString()));
                throw new GlobalException(String.format(Constant.CITY_NOT_FOUND, cityName));
            }
            String url = String.format(forecastURL, geoCodingResponse.getLatitude(), geoCodingResponse.getLongitude(), apiKey);
            ResponseEntity<ForecastResponse> rawForecastResponse = restTemplate.getForEntity(url, ForecastResponse.class);
//          Processing the Raw Forecast data to get only the needed fields using streams
            logger.info(String.format(Constant.FETCHED_FORECAST_DATA, cityName,Instant.now().toString()));
            return rawForecastResponse.getBody().getList().stream()
                    .map(forecastResponse -> new Forecast(
                            LocalDate.parse(forecastResponse.getDtTxt().substring(0, 10)),
                            forecastResponse.getMain().getTemperature(),
                            forecastResponse.getMain().getTemperatureMaximum(),
                            forecastResponse.getMain().getTemperatureMinimum(),
                            forecastResponse.getWeather().stream()
                                    .map(Weather::getDescription).distinct().collect(Collectors.joining(", "))))
                    .collect(Collectors.groupingBy(Forecast::getDate)).entrySet().stream()
                    .map(entry -> {
                        List<Forecast> dailyForecasts = entry.getValue();
                        double avgTemp = dailyForecasts.stream().mapToDouble(Forecast::getAverageTemperature).average().orElse(Double.NaN);
                        double maxTemp = dailyForecasts.stream().mapToDouble(Forecast::getMaximumTemperature).max().orElse(Double.NaN);
                        double minTemp = dailyForecasts.stream().mapToDouble(Forecast::getMinimumTemperature).min().orElse(Double.NaN);
                        String weatherDescriptions = dailyForecasts.stream().map(Forecast::getWeather).distinct().collect(Collectors.joining(", "));

                        return new Forecast(entry.getKey(), avgTemp, maxTemp, minTemp, weatherDescriptions);
                    }).sorted(Comparator.comparing(Forecast::getDate)).limit(numberOfDays).collect(Collectors.toList());
        } catch (HttpClientErrorException.NotFound exception) {
            logger.error(String.format(Constant.CITY_NOT_FOUND_EXCEPTION, cityName, Instant.now().toString()));
            throw new GlobalException(String.format(Constant.CITY_NOT_FOUND, cityName));
        } catch (HttpClientErrorException.Unauthorized exception) {
            logger.error(String.format(Constant.UNAUTHORIZED_ACCESS_EXCEPTION, Instant.now().toString()));
            throw new GlobalException(Constant.UNAUTHORIZED_ACCESS);
        }
    }
}
