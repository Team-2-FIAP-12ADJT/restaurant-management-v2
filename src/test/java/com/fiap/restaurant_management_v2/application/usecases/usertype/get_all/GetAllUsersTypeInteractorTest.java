package com.fiap.restaurant_management_v2.application.usecases.usertype.get_all;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetAllUsersTypeInteractorTest {

    @Mock
    private UserTypeDsGateway userTypeDsGateway;

    private CapturingPresenter presenter;
    private GetAllUsersTypeInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new GetAllUsersTypeInteractor(
            presenter,
            userTypeDsGateway
        );
    }

    @Test
    @DisplayName("Retorna lista paginada de tipos de usuário")
    void returnsPagedUserTypes() {
        var request = new GetAllUsersTypeRequestModel(1, 10);
        var id = UUID.randomUUID();
        var dsResponse = new UserTypeDsResponseModel(id, "admin");

        when(
            userTypeDsGateway.findAll(any(SearchQuery.class), eq(1), eq(10))
        ).thenReturn(new PageResult<>(List.of(dsResponse), 1, 1, 10));

        interactor.execute(request);

        verify(userTypeDsGateway).findAll(
            any(SearchQuery.class),
            eq(1),
            eq(10)
        );
        assertEquals(1, presenter.response.page().content().size());
        assertEquals(
            "admin",
            presenter.response.page().content().get(0).userType()
        );
    }

    private static final class CapturingPresenter
        implements GetAllUsersTypeOutputBoundary
    {

        private GetAllUsersTypeResponseModel response;

        @Override
        public void present(GetAllUsersTypeResponseModel response) {
            this.response = response;
        }
    }
}
