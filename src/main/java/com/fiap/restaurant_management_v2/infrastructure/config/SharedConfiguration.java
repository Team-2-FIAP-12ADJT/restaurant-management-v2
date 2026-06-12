package com.fiap.restaurant_management_v2.infrastructure.config;

import com.fiap.restaurant_management_v2.application.gateways.PasswordEncoderGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SharedConfiguration {
    @Bean
    public PasswordEncoderGateway passwordEncoderGateway() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder::encode;
    }
}
