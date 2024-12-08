package com.sample.demo.controller;

import com.sample.demo.constant.Constant;
import com.sample.demo.exception.GlobalException;
import com.sample.demo.model.APIResponse;
import com.sample.demo.service.WeatherService;
import io.github.resilience4j.retry.annotation.Retry;
import io.micrometer.core.instrument.Counter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.Instant;

@RestController
@RequestMapping("/api")
@Validated
public class WeatherController {
    Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final WeatherService weatherService;
    private final HttpServletRequest httpServletRequest;

    private final Counter totalRequestsCounter;

    private final Counter cacheHitsCounter;
    private final Counter cacheMissesCounter;
    private final CacheManager cacheManager;

    public WeatherController(
            WeatherService weatherService,
            HttpServletRequest httpServletRequest,
            Counter totalRequestsCounter, Counter cacheHitsCounter, Counter cacheMissesCounter, CacheManager cacheManager) {
        this.weatherService = weatherService;
        this.httpServletRequest = httpServletRequest;
        this.totalRequestsCounter = totalRequestsCounter;
        this.cacheHitsCounter = cacheHitsCounter;
        this.cacheMissesCounter = cacheMissesCounter;
        this.cacheManager = cacheManager;
    }

    @GetMapping("/weather")
    @Retry(name = Constant.DEMO, fallbackMethod = Constant.GET_WHETHER_CALLBACK)
    public ResponseEntity<APIResponse> getWeather(
            @RequestParam String cityName) throws Exception {
        logger.info(Constant.API_LOGGER, Instant.now().toString(),
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        totalRequestsCounter.increment();
        String cacheKey = String.format(Constant.WEATHER, cityName);
        Cache cache = cacheManager.getCache(Constant.SIMPLE);

        if (cache != null && cache.get(cacheKey) != null) {
            cacheHitsCounter.increment();
            logger.info(String.format(Constant.CACHE_HITS, cityName));

        } else {
            logger.info(String.format(Constant.CACHE_MISSES,cityName));
            cacheMissesCounter.increment();
            weatherService.getWeather(cityName);
            if (cache != null) {
                cache.put(cacheKey, cityName);
            }
        }
        return ResponseEntity.ok().body(new APIResponse(Constant.SUCCESS,
                weatherService.getWeather((cityName))));
    }

    @GetMapping("/forecast")
    @Retry(name = Constant.DEMO, fallbackMethod = Constant.GET_FORECAST_CALLBACK)
    public ResponseEntity<APIResponse> getWeatherForecast(
            @RequestParam String cityName,
            @RequestParam  @Min(1) @Max(5) Integer numberOfDays) {
        logger.info(Constant.API_LOGGER, Instant.now().toString(),
                httpServletRequest.getMethod(), httpServletRequest.getRequestURI());
        totalRequestsCounter.increment();
        String cacheKey = String.format(Constant.FORECAST, cityName);
        Cache cache = cacheManager.getCache(Constant.SIMPLE);

        if (cache != null && cache.get(cacheKey) != null) {
            cacheHitsCounter.increment();
            logger.info(String.format(Constant.CACHE_HITS, cityName));

        } else {
            logger.info(String.format(Constant.CACHE_MISSES,cityName));
            cacheMissesCounter.increment();
            weatherService.getWeatherForecast(cityName,numberOfDays);
            if (cache != null) {
                cache.put(cacheKey, cityName);
            }
        }
        return ResponseEntity.ok().body(new APIResponse(Constant.SUCCESS,
                weatherService.getWeatherForecast(cityName, numberOfDays)));
    }

    public ResponseEntity<APIResponse> getWeatherCallBack(Exception exception) {
        if (exception instanceof GlobalException) {
            throw (GlobalException) exception;
        }
        throw new GlobalException(Constant.RESOURCE_NOT_FOUND);
    }

    public ResponseEntity<APIResponse> getWeatherForecastCallBack(Exception exception) {
        if (exception instanceof GlobalException) {
            throw (GlobalException) exception;
        }
        throw new GlobalException(Constant.RESOURCE_NOT_FOUND);
    }
}
