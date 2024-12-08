package com.sample.demo.config;

import com.sample.demo.configuration.MetricsConfig;
import com.sample.demo.constant.Constant;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class MetricsConfigTest {

    @Mock
    private MeterRegistry meterRegistry; // Mocked MeterRegistry bean

    @InjectMocks
    private MetricsConfig metricsConfig; // The class under test

    @Test
    void contextLoads() {
    }

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testTotalRequestsCounter() {
        Counter counterMock = mock(Counter.class);
        when(meterRegistry.counter(Constant.TOTAL_REQUESTS, Constant.TYPE, Constant.HTTP)).thenReturn(counterMock);
        Counter counter = metricsConfig.totalRequestsCounter(meterRegistry);
        assertNotNull(counter);
        verify(meterRegistry).counter(Constant.TOTAL_REQUESTS, Constant.TYPE, Constant.HTTP);
    }

    @Test
    public void testCacheHitsCounter() {
        Counter counterMock = mock(Counter.class);
        when(meterRegistry.counter(Constant.CACHE_HIT, Constant.TYPE, Constant.CACHE_HIT)).thenReturn(counterMock);
        Counter counter = metricsConfig.cacheHitsCounter(meterRegistry);
        assertNotNull(counter);
        verify(meterRegistry).counter(Constant.CACHE_HIT, Constant.TYPE, Constant.CACHE_HIT);
    }

    @Test
    public void testCacheMissesCounter() {
        Counter counterMock = mock(Counter.class);
        when(meterRegistry.counter(Constant.CACHE_MISS, Constant.TYPE, Constant.CACHE_MISS)).thenReturn(counterMock);
        Counter counter = metricsConfig.cacheMissesCounter(meterRegistry);
        assertNotNull(counter);
        verify(meterRegistry).counter(Constant.CACHE_MISS, Constant.TYPE, Constant.CACHE_MISS);
    }
}