package com.ecran.users.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;

import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

    @Bean
    public ModelMapper objectMapper() {
        return new ModelMapper();
    }

}


