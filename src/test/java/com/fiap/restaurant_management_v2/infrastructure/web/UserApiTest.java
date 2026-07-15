package com.fiap.restaurant_management_v2.infrastructure.web;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.adapters.controllers.UserController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateUserPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetAllUsersPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetUserByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.UpdateUserPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserWithDetailsViewModel;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.GetAllUsersParams;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class UserApiTest {

    @Mock
    private UserController userController;

    @Mock
    private CreateUserPresenter createUserPresenter;

    @Mock
    private GetAllUsersPresenter getAllUsersPresenter;

    @Mock
    private GetUserByIdPresenter getUserByIdPresenter;

    @Mock
    private UpdateUserPresenter updateUserPresenter;

    private UserApi api;

    @BeforeEach
    void setUp() {
        api = new UserApi(
            userController,
            createUserPresenter,
            getAllUsersPresenter,
            getUserByIdPresenter,
            updateUserPresenter
        );
    }

    @Test
    void getAllDelegatesAndReturnsPresenterViewModel() {
        var viewModel = new PageViewModel<UserWithDetailsViewModel>(1, 10, 0, 0, List.of());
        when(getAllUsersPresenter.getViewModel()).thenReturn(viewModel);

        var response = api.getAll(
            new GetAllUsersParams("Ada", "ada@example.com", "ada", "12345678901", 1, 10)
        );

        verify(userController).getAll("Ada", "ada@example.com", "ada", "12345678901", 1, 10);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(viewModel, response.getBody());
    }

    @Test
    void getByIdDelegatesAndReturnsPresenterViewModel() {
        UUID id = UUID.randomUUID();
        var viewModel = new UserWithDetailsViewModel(
            id.toString(),
            "Ada",
            "ada@example.com",
            "ada",
            "123.456.789-01",
            "DONO",
            List.of()
        );
        when(getUserByIdPresenter.getViewModel()).thenReturn(viewModel);

        var response = api.getById(id.toString());

        verify(userController).getById(id);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(viewModel, response.getBody());
    }
}
