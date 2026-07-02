package com.fiap.restaurant_management_v2.adapters.presenters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserViewModel;
import com.fiap.restaurant_management_v2.application.usecases.user.update.UpdateUserResponseModel;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UpdateUserPresenterTest {

    @Test
    @DisplayName("Formata response model em view model (id UUID → String)")
    void buildsViewModel() {
        var presenter = new UpdateUserPresenter();
        var id = UUID.randomUUID();

        presenter.present(
            new UpdateUserResponseModel(
                id,
                "Foo",
                "foo@example.com",
                "foo",
                "123456789"
            )
        );

        UserViewModel vm = presenter.getViewModel();
        assertEquals(id.toString(), vm.id());
        assertEquals("Foo", vm.name());
        assertEquals("foo@example.com", vm.email());
        assertEquals("foo", vm.login());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new UpdateUserPresenter().getViewModel());
    }
}
