package com.sample.demo.configuration;

import com.sample.demo.constant.Constant;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfig {

    @Bean
    public Counter totalRequestsCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter(Constant.TOTAL_REQUESTS, Constant.TYPE, Constant.HTTP);
    }

    @Bean
    public Counter cacheHitsCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter(Constant.CACHE_HIT, Constant.TYPE, Constant.CACHE_HIT);
    }

    @Bean
    public Counter cacheMissesCounter(MeterRegistry meterRegistry) {
        return meterRegistry.counter(Constant.CACHE_MISS, Constant.TYPE, Constant.CACHE_MISS);
    }
}
