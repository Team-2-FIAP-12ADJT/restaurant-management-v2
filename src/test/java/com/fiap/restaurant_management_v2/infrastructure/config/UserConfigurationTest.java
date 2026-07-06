package com.fiap.restaurant_management_v2.infrastructure.config;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import com.fiap.restaurant_management_v2.adapters.presenters.GetAllUsersPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetUserByIdPresenter;
import org.junit.jupiter.api.Test;

class UserConfigurationTest {

    @Test
    void createsRemainingRequestScopedPresenters() {
        var configuration = new UserConfiguration();

        assertInstanceOf(GetAllUsersPresenter.class, configuration.getAllUsersPresenter());
        assertInstanceOf(GetUserByIdPresenter.class, configuration.getUserByIdPresenter());
    }
}
