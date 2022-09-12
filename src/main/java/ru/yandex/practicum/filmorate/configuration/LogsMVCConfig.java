package ru.yandex.practicum.filmorate.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.yandex.practicum.filmorate.interceptor.LogsInterceptor;

@Configuration
public class LogsMVCConfig implements WebMvcConfigurer {
    @Autowired
    private LogsInterceptor logsInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logsInterceptor);
    }
}
