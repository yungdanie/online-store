package ru.practicum.onlinestore.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;

@Configuration
public class RandomIdConfiguration {

    @Bean(name = "id")
    public Long random() {
        return new Random().nextLong();
    }
}
