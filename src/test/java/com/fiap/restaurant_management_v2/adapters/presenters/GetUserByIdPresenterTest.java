package com.fiap.restaurant_management_v2.adapters.presenters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserWithDetailsViewModel;
import com.fiap.restaurant_management_v2.application.usecases.user.get_user_by_id.GetUserByIdResponseModel;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetUserByIdPresenterTest {

    @Test
    @DisplayName("Formata response model em view model")
    void buildsViewModel() {
        var presenter = new GetUserByIdPresenter();
        var id = UUID.randomUUID();

        presenter.present(
            new GetUserByIdResponseModel(
                id,
                "Foo",
                "foo@example.com",
                "foo",
                "12345678901",
                "DONO",
                List.of()
            )
        );

        UserWithDetailsViewModel vm = presenter.getViewModel();
        assertEquals(id.toString(), vm.id());
        assertEquals("Foo", vm.name());
        assertEquals("foo@example.com", vm.email());
        assertEquals("foo", vm.login());
        assertEquals("123.456.789-01", vm.taxIdentifier());
        assertEquals("DONO", vm.userType());
        assertEquals(List.of(), vm.restaurants());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new GetUserByIdPresenter().getViewModel());
    }
}
