package com.fiap.restaurant_management_v2.application.gateways;

/** Gateway for password hashing; keeps the concrete encoder out of the framework-free core. */
public interface PasswordEncoderGateway {
    String encode(String rawPassword);

    boolean matches(String rawPassword, String encodedPassword);
}
