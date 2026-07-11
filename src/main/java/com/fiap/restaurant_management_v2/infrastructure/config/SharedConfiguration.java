package com.fiap.restaurant_management_v2.infrastructure.config;

import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import com.fiap.restaurant_management_v2.application.gateways.PasswordEncoderGateway;
import com.fiap.restaurant_management_v2.infrastructure.logging.Slf4jLoggerGateway;
import com.fiap.restaurant_management_v2.infrastructure.web.filter.RequestLoggingFilter;
import com.fiap.restaurant_management_v2.infrastructure.web.filter.SensitiveDataMasker;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SharedConfiguration {
    @Bean
    public PasswordEncoderGateway passwordEncoderGateway() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return new PasswordEncoderGateway() {
            @Override public String encode(String rawPassword) { return encoder.encode(rawPassword); }
            @Override public boolean matches(String rawPassword, String encodedPassword) { return encoder.matches(rawPassword, encodedPassword); }
        };
    }

    @Bean
    public LoggerGateway loggerGateway() {
        return new Slf4jLoggerGateway();
    }

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> requestLoggingFilter() {
        FilterRegistrationBean<RequestLoggingFilter> registration =
            new FilterRegistrationBean<>(
                new RequestLoggingFilter(new SensitiveDataMasker())
            );
        registration.addUrlPatterns("/api/*");
        registration.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registration;
    }
}
