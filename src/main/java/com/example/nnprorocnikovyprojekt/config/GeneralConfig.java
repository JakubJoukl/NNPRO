package com.example.nnprorocnikovyprojekt.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;

@Configuration
@EnableAsync
public class GeneralConfig {
    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }
}
