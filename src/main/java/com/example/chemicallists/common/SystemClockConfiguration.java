package com.example.chemicallists.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class SystemClockConfiguration {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
