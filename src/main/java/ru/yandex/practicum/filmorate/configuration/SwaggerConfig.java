package ru.yandex.practicum.filmorate.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.core.GroupedOpenApi;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import io.swagger.v3.oas.models.info.Info;

@Configuration
public class SwaggerConfig {
    @Bean
    public GroupedOpenApi publicUserApi() {
        return GroupedOpenApi.builder()
                .group("Filmorate")
                .pathsToMatch("/users/**")
                .build();
    }

    @Bean
    public OpenAPI customOpenApi(@Value("${application-description:demo}") String appDescription,
                                 @Value("${application-version:0.01}") String appVersion) {
        return new OpenAPI().info(new Info().title("Application API")
                .version(appVersion)
                .description(appDescription));
    }
}
