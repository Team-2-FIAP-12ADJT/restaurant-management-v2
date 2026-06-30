package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.TransactionalExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class SpringTransactionalExecutor implements TransactionalExecutor {

    @Override
    @Transactional
    public void execute(Runnable action) {
        action.run();
    }
}
