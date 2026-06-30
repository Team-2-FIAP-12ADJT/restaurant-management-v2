package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.UserTypeViewModel;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_all.GetAllUsersTypeResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.usertype.get_all.UserTypeSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class GetAllUsersTypePresenterTest {

    @Test
    @DisplayName("Formata o response model paginado em view model")
    void buildsPageViewModelFromResponse() {
        var presenter = new GetAllUsersTypePresenter();
        var id1 = UUID.randomUUID();
        var id2 = UUID.randomUUID();

        var summaries = List.of(
                new UserTypeSummary(id1, "admin"),
                new UserTypeSummary(id2, "waiter")
        );
        var page = new PageResult<>(summaries, 2, 1, 10);
        var response = new GetAllUsersTypeResponseModel(page);

        presenter.present(response);

        PageViewModel<UserTypeViewModel> viewModel = presenter.getViewModel();
        assertEquals(1, viewModel.page());
        assertEquals(10, viewModel.size());
        assertEquals(2, viewModel.totalElements());
        assertEquals(1, viewModel.totalPages());
        assertEquals(2, viewModel.content().size());
        assertEquals("admin", viewModel.content().get(0).nameType());
        assertEquals("waiter", viewModel.content().get(1).nameType());
    }

    @Test
    @DisplayName("View model é nulo antes de present()")
    void viewModelIsNullBeforePresent() {
        assertNull(new GetAllUsersTypePresenter().getViewModel());
    }
}
