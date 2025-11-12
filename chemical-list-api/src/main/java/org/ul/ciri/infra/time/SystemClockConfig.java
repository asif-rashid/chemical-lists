package org.ul.ciri.infra.time;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;

@Configuration
public class SystemClockConfig {

    @Bean
    public Clock clock() {
        return Clock.systemUTC();
    }
}
