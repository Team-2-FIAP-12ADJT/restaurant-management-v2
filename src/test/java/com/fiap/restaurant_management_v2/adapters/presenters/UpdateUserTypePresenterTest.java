package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserTypeViewModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.update.UpdateUserTypeResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UpdateUserTypePresenterTest {

    @Test
    @DisplayName("Formata o response model em view model")
    void buildsViewModelFromResponse() {
        var presenter = new UpdateUserTypePresenter();
        var id = UUID.randomUUID();

        presenter.present(new UpdateUserTypeResponseModel(id, "manager"));

        UserTypeViewModel viewModel = presenter.getViewModel();
        assertEquals(id.toString(), viewModel.id());
        assertEquals("manager", viewModel.nameType());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new UpdateUserTypePresenter().getViewModel());
    }
}
