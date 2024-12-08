package com.sample.demo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.sample.demo.constant.Constant;
import com.sample.demo.constant.MockConstants;
import com.sample.demo.exception.GlobalException;
import com.sample.demo.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(weatherService, "apiKey", "testApiKey");
        ReflectionTestUtils.setField(weatherService, "weatherURL", "http://test-weather-url?q=%s&appid=%s");
        ReflectionTestUtils.setField(weatherService, "goeCodeURL", "http://test-geocode-url?q=%s&appid=%s");
        ReflectionTestUtils.setField(weatherService, "forecastURL", "http://test-forecast-url?lat=%s&lon=%s&appid=%s");
    }

    @Test
    void testGetWeather_Success() throws JsonProcessingException {

        RawWeatherResponse rawResponse = createMockRawWeatherResponse();
        ResponseEntity<RawWeatherResponse> responseEntity = ResponseEntity.ok(rawResponse);
        when(restTemplate.getForEntity(anyString(), eq(RawWeatherResponse.class)))
                .thenReturn(responseEntity);
        WeatherResponse weatherResponse = weatherService.getWeather(MockConstants.CITY_NAME);

        assertNotNull(weatherResponse);
        assertEquals(rawResponse.getCity(), weatherResponse.getCity());
        assertEquals(rawResponse.getWeather().get(0).getDescription(), weatherResponse.getWeatherDescription());
        verify(restTemplate).getForEntity(anyString(), eq(RawWeatherResponse.class));
    }

    @Test
    void testGetWeatherForecast_Success() {
        GeoCodingResponse[] geoCodingResponses = new GeoCodingResponse[]{
                new GeoCodingResponse(MockConstants.CITY_NAME, 51.5074, -0.1278)
        };
        ResponseEntity<GeoCodingResponse[]> geoCodeResponseEntity = ResponseEntity.ok(geoCodingResponses);
        ForecastResponse forecastResponse = createMockForecastResponse();
        ResponseEntity<ForecastResponse> forecastResponseEntity = ResponseEntity.ok(forecastResponse);
        when(restTemplate.getForEntity(contains("geocode"), eq(GeoCodingResponse[].class)))
                .thenReturn(geoCodeResponseEntity);
        when(restTemplate.getForEntity(contains("forecast"), eq(ForecastResponse.class)))
                .thenReturn(forecastResponseEntity);
        List<Forecast> forecasts = weatherService.getWeatherForecast(MockConstants.CITY_NAME, MockConstants.NUMBER_OF_DAYS);
        assertNotNull(forecasts);
        assertEquals(MockConstants.NUMBER_OF_DAYS, forecasts.size());
        assertTrue(forecasts.stream().allMatch(f -> f.getDate() != null));
    }

    @Test
    void testGetWeatherForecast_CityNotFound() {
        String cityName = "InvalidCity";
        when(restTemplate.getForEntity(contains("geocode"), eq(GeoCodingResponse[].class)))
                .thenReturn(ResponseEntity.ok(new GeoCodingResponse[0]));
        GlobalException exception = assertThrows(GlobalException.class,
                () -> weatherService.getWeatherForecast(cityName, 5));
        assertEquals(String.format(Constant.CITY_NOT_FOUND, cityName), exception.getErrorMessage());
    }

    private RawWeatherResponse createMockRawWeatherResponse() {
        RawWeatherResponse response = new RawWeatherResponse();
        response.setCity(MockConstants.CITY_NAME);
        response.setTimezone(String.valueOf(0));

        RawWeatherResponse.MainTemperature mainTemperature = new RawWeatherResponse.MainTemperature();
        mainTemperature.setTemperature(20.0);
        mainTemperature.setHumidity(65);
        response.setMainTemperature(mainTemperature);

        RawWeatherResponse.Weather weather = new RawWeatherResponse.Weather();
        weather.setDescription("Cloudy");
        response.setWeather(List.of(weather));

        RawWeatherResponse.Wind wind = new RawWeatherResponse.Wind();
        wind.setSpeed(5.0);
        response.setWind(wind);

        return response;
    }

    private ForecastResponse createMockForecastResponse() {
        ForecastResponse response = new ForecastResponse();
        response.setList(createMockForecastList());
        return response;
    }

    private List<ForecastResponse.WeatherData> createMockForecastList() {
        return List.of(
                createForecastDetail("2024-01-01 12:00:00"),
                createForecastDetail("2024-01-02 12:00:00"),
                createForecastDetail("2024-01-03 12:00:00"),
                createForecastDetail("2024-01-04 12:00:00"),
                createForecastDetail("2024-01-05 12:00:00")
        );
    }

    private ForecastResponse.WeatherData createForecastDetail(String dateTime) {
        ForecastResponse.WeatherData detail = new ForecastResponse.WeatherData();
        detail.setDtTxt(dateTime);

        ForecastResponse.Main main = new ForecastResponse.Main();
        main.setTemperature(20.0);
        main.setTemperatureMaximum(25.0);
        main.setTemperatureMinimum(15.0);
        detail.setMain(main);

        Weather weather = new Weather();
        weather.setDescription("Cloudy");
        detail.setWeather(List.of(weather));

        return detail;
    }


//    @Test
//    void testGetWeatherForecast_Unauthorized() {
//        String cityName = "London";
//        when(restTemplate.getForEntity(contains("geocode"), eq(GeoCodingResponse[].class)))
//                .thenThrow(new HttpClientErrorException (HttpStatus.UNAUTHORIZED,"Unauthorized Access"));
//        GlobalException exception = assertThrows(GlobalException.class,
//                () -> weatherService.getWeatherForecast(cityName, 5));
//        assertEquals(MockConstants.UNAUTHORIZED_ACCESS, exception.getErrorMessage());
//    }
}