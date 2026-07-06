package com.fiap.restaurant_management_v2.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

import com.fiap.restaurant_management_v2.adapters.controllers.UserTypeController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateUserTypePresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetAllUsersTypePresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetUserTypeByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.UpdateUserTypePresenter;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.BindUserTypeRequest;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class TypeUserApiTest {

    @Mock
    private UserTypeController userTypeController;

    @Mock
    private CreateUserTypePresenter createUserTypePresenter;

    @Mock
    private GetAllUsersTypePresenter getAllUsersTypePresenter;

    @Mock
    private GetUserTypeByIdPresenter getUserTypeByIdPresenter;

    @Mock
    private UpdateUserTypePresenter updateUserTypePresenter;

    private TypeUserApi api;

    @BeforeEach
    void setUp() {
        api = new TypeUserApi(
            userTypeController,
            createUserTypePresenter,
            getAllUsersTypePresenter,
            getUserTypeByIdPresenter,
            updateUserTypePresenter
        );
    }

    @Test
    void deleteByIdDelegatesAndReturnsNoContent() {
        UUID id = UUID.randomUUID();

        var response = api.deleteById(id.toString());

        verify(userTypeController).delete(id);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

    @Test
    void bindDelegatesAndReturnsNoContent() {
        UUID userId = UUID.randomUUID();
        UUID typeId = UUID.randomUUID();

        var response = api.bind(new BindUserTypeRequest(userId, typeId));

        verify(userTypeController).bind(userId, typeId);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
