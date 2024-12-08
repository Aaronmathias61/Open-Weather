package com.sample.demo.controller;

import com.sample.demo.constant.Constant;
import com.sample.demo.constant.MockConstants;
import com.sample.demo.exception.GlobalException;
import com.sample.demo.model.APIResponse;
import com.sample.demo.service.WeatherService;
import io.micrometer.core.instrument.Counter;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class WeatherControllerTest {
    @InjectMocks
    private WeatherController weatherController;
    @Mock
    private WeatherService weatherService;
    @Mock
    private HttpServletRequest httpServletRequest;
    @Mock
    private Counter totalRequestsCounter;
    @Mock
    private Counter cacheHitsCounter;
    @Mock
    private Counter cacheMissesCounter;
    @Mock
    private CacheManager cacheManager;
    @Mock
    private Cache cache;
    @Mock
    private Cache.ValueWrapper valueWrapper;

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetWeather() throws Exception {
        String cacheKey = String.format(MockConstants.WEATHER, MockConstants.CITY_NAME);
        when(httpServletRequest.getMethod()).thenReturn(MockConstants.GET);
        when(httpServletRequest.getRequestURI()).thenReturn(MockConstants.WEATHER_API);
        when(cacheManager.getCache(MockConstants.SIMPLE)).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(valueWrapper);
        ResponseEntity<APIResponse> response = weatherController.getWeather(MockConstants.CITY_NAME);
        assertNotNull(response);
    }

    @Test
    public void testGetWeatherForecast() throws Exception {
        String cacheKey = String.format(MockConstants.WEATHER, MockConstants.NUMBER_OF_DAYS);
        when(httpServletRequest.getMethod()).thenReturn(MockConstants.GET);
        when(httpServletRequest.getRequestURI()).thenReturn(MockConstants.FORECAST_API);
        when(cacheManager.getCache(MockConstants.SIMPLE)).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(valueWrapper);
        ResponseEntity<APIResponse> response = weatherController.getWeatherForecast(MockConstants.CITY_NAME, MockConstants.NUMBER_OF_DAYS);
        assertNotNull(response);
    }

    @Test
    public void testGetWeatherNotFound() throws Exception {
        String cacheKey = String.format(Constant.WEATHER, MockConstants.CITY_NAME);
        when(httpServletRequest.getMethod()).thenReturn(MockConstants.GET);
        when(httpServletRequest.getRequestURI()).thenReturn(MockConstants.WEATHER_API);
        when(cacheManager.getCache(MockConstants.SIMPLE)).thenReturn(null);
        when(cache.get(cacheKey)).thenReturn(null);
        ResponseEntity<APIResponse> response = weatherController.getWeather(MockConstants.CITY_NAME);
        assertEquals(response.getBody().getMessage(), MockConstants.SUCCESS);
    }

    @Test
    public void testGetWeatherForecastNot_Found() throws Exception {
        String cacheKey = String.format(MockConstants.WEATHER, MockConstants.NUMBER_OF_DAYS);
        when(httpServletRequest.getMethod()).thenReturn(MockConstants.GET);
        when(httpServletRequest.getRequestURI()).thenReturn(MockConstants.WEATHER_API);
        when(cacheManager.getCache(MockConstants.SIMPLE)).thenReturn(cache);
        when(cache.get(cacheKey)).thenReturn(valueWrapper);
        ResponseEntity<APIResponse> response = weatherController.getWeatherForecast(MockConstants.INCORRECT_CITY_NAME, MockConstants.NUMBER_OF_DAYS);
        assertEquals(response.getBody().getMessage(), MockConstants.SUCCESS);

    }

    @Test
    void testGetWeatherCallBack_WithGlobalException() throws Exception {
        GlobalException exception = new GlobalException(MockConstants.CITY_NOT_FOUND);
        when(weatherService.getWeather(anyString())).thenThrow(exception);
        mockMvc.perform(get(MockConstants.WEATHER_API)
                        .param(MockConstants.CITY__NAME, MockConstants.CITY_NOT_FOUND)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetWeatherForecastCallBack_WithGlobalException() throws Exception {
        GlobalException exception = new GlobalException(MockConstants.CITY_NOT_FOUND);
        when(weatherService.getWeatherForecast(anyString(), anyInt())).thenThrow(exception);
        mockMvc.perform(get(MockConstants.FORECAST_API)
                        .param(MockConstants.CITY__NAME, MockConstants.INVALID_CITY)
                        .param(MockConstants.NUMBER__OF_DAYS, MockConstants.CALLBACK)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath(MockConstants.ERROR).value(MockConstants.CITY_NOT_FOUND));
    }

}
