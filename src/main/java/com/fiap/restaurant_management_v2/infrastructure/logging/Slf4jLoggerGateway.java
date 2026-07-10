package com.fiap.restaurant_management_v2.infrastructure.logging;

import com.fiap.restaurant_management_v2.application.gateways.LoggerGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Slf4jLoggerGateway implements LoggerGateway {

    private static final Logger LOGGER = LoggerFactory.getLogger("business");

    @Override
    public void info(String message, Object... args) {
        LOGGER.info(message, args);
    }

    @Override
    public void warn(String message, Object... args) {
        LOGGER.warn(message, args);
    }
}
