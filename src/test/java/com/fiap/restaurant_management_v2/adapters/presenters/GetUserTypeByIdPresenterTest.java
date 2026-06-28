package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserTypeViewModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_type_by_id.GetUserTypeByIdResponseModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class GetUserTypeByIdPresenterTest {

    @Test
    @DisplayName("Formata o response model em view model")
    void buildsViewModelFromResponse() {
        var presenter = new GetUserTypeByIdPresenter();
        var id = UUID.randomUUID();

        presenter.present(new GetUserTypeByIdResponseModel(id, "admin"));

        UserTypeViewModel viewModel = presenter.getViewModel();
        assertEquals(id.toString(), viewModel.id());
        assertEquals("admin", viewModel.nameType());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new GetUserTypeByIdPresenter().getViewModel());
    }
}
