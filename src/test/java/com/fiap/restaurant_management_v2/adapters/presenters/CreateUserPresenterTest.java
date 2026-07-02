package com.fiap.restaurant_management_v2.adapters.presenters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserViewModel;
import com.fiap.restaurant_management_v2.application.usecases.user.create.CreateUserResponseModel;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CreateUserPresenterTest {

    @Test
    @DisplayName("Formata o response model em view model com id como string")
    void buildsViewModelFromResponse() {
        var presenter = new CreateUserPresenter();
        var id = UUID.randomUUID();

        presenter.present(
            new CreateUserResponseModel(
                id,
                "Ada",
                "ada@example.com",
                "ada",
                "123456789"
            )
        );

        UserViewModel viewModel = presenter.getViewModel();
        assertEquals(id.toString(), viewModel.id());
        assertEquals("Ada", viewModel.name());
        assertEquals("ada@example.com", viewModel.email());
        assertEquals("ada", viewModel.login());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new CreateUserPresenter().getViewModel());
    }
}
