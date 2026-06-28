package com.fiap.restaurant_management_v2.application.gateways;

@FunctionalInterface
public interface TransactionalExecutor {
    void execute(Runnable action);
}
