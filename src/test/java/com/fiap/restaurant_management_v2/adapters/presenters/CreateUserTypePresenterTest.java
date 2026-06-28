package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserTypeViewModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.create.CreateUserTypeResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CreateUserTypePresenterTest {

    @Test
    @DisplayName("Formata o response model em view model com id como string")
    void buildsViewModelFromResponse() {
        var presenter = new CreateUserTypePresenter();
        var id = UUID.randomUUID();

        presenter.present(new CreateUserTypeResponseModel(id, "admin"));

        UserTypeViewModel viewModel = presenter.getViewModel();
        assertEquals(id.toString(), viewModel.id());
        assertEquals("admin", viewModel.nameType());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new CreateUserTypePresenter().getViewModel());
    }
}
