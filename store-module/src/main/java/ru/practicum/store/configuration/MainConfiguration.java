package ru.practicum.store.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.store.ApiClient;
import ru.practicum.store.api.AccountApi;

@Configuration
public class MainConfiguration {

    @Bean
    protected AccountApi getAccountApi(@Value("${account.api}") final String accountApiAddress) {
        var apiClient = new ApiClient();
        apiClient.setBasePath(accountApiAddress);
        return new AccountApi(apiClient);
    }
}
