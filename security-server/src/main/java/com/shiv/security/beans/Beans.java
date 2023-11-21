package com.shiv.security.beans;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class Beans {
    @Bean
    public List<String> secretKeys(){
        return new ArrayList<>();
    }
}
