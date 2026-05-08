package com.codemonks.gameservice.config;

import com.codemonks.gameservice.feign.FeignErrorDecoder;
import feign.Logger;
import feign.codec.ErrorDecoder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class FeignConfig {

    private final FeignErrorDecoder feignErrorDecoder;

    @Bean
    public ErrorDecoder errorDecoder() {
        return feignErrorDecoder;
    }

    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
