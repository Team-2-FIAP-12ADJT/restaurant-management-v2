package com.fiap.restaurant_management_v2.application.gateways;

public interface LoggerGateway {

    void info(String message, Object... args);

    void warn(String message, Object... args);
}
