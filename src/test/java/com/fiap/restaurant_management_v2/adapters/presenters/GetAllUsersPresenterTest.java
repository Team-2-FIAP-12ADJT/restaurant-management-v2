package com.fiap.restaurant_management_v2.adapters.presenters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserWithDetailsViewModel;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.GetAllUsersResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.user.get_all.UserSummary;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GetAllUsersPresenterTest {

    @Test
    @DisplayName("Formata página de summaries em page view model")
    void buildsPageViewModel() {
        var presenter = new GetAllUsersPresenter();
        var id = UUID.randomUUID();
        var summary = new UserSummary(
            id,
            "Foo",
            "foo@example.com",
            "foo",
            "12345678901",
            "DONO",
            List.of()
        );
        var page = new PageResult<>(List.of(summary), 1L, 1, 10);

        presenter.present(new GetAllUsersResponseModel(page));

        PageViewModel<UserWithDetailsViewModel> vm = presenter.getViewModel();
        assertEquals(1, vm.page());
        assertEquals(10, vm.size());
        assertEquals(1L, vm.totalElements());
        assertEquals(1, vm.totalPages());
        assertEquals(1, vm.content().size());
        var first = vm.content().getFirst();
        assertEquals(id.toString(), first.id());
        assertEquals("Foo", first.name());
        assertEquals("foo@example.com", first.email());
        assertEquals("foo", first.login());
        assertEquals("123.456.789-01", first.taxIdentifier());
        assertEquals("DONO", first.userType());
        assertEquals(List.of(), first.restaurants());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new GetAllUsersPresenter().getViewModel());
    }
}
