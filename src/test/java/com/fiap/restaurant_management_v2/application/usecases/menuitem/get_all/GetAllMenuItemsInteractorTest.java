package com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all;

import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.MenuItemDsResponseModel;
import com.fiap.restaurant_management_v2.application.gateways.search.FilterOperator;
import com.fiap.restaurant_management_v2.application.gateways.search.SearchQuery;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GetAllMenuItemsInteractorTest {

    @Mock
    private MenuItemDsGateway menuItemDsGateway;

    private CapturingPresenter presenter;
    private GetAllMenuItemsInteractor interactor;

    @BeforeEach
    void setUp() {
        presenter = new CapturingPresenter();
        interactor = new GetAllMenuItemsInteractor(
            presenter,
            menuItemDsGateway
        );
    }

    @Test
    @DisplayName("Lista itens do cardápio paginados")
    void listsMenuItems() {
        MenuItemDsResponseModel item = menuItem("Risoto");
        PageResult<MenuItemDsResponseModel> page = new PageResult<>(
            List.of(item),
            1L,
            1,
            10
        );
        when(menuItemDsGateway.findAll(any(), anyInt(), anyInt()))
            .thenReturn(page);

        interactor.execute(new GetAllMenuItemsRequestModel(null, 1, 10));

        assertNotNull(presenter.response);
        assertEquals(1, presenter.response.page().content().size());
        assertEquals(
            "Risoto",
            presenter.response.page().content().getFirst().name()
        );
        assertEquals(1, presenter.response.page().totalElements());
    }

    @Test
    @DisplayName("Retorna página vazia quando não existem itens")
    void returnsEmptyPage() {
        PageResult<MenuItemDsResponseModel> page = new PageResult<>(
            List.of(),
            0L,
            1,
            10
        );
        when(menuItemDsGateway.findAll(any(), anyInt(), anyInt()))
            .thenReturn(page);

        interactor.execute(new GetAllMenuItemsRequestModel(null, 1, 10));

        assertNotNull(presenter.response);
        assertEquals(0, presenter.response.page().content().size());
    }

    @Test
    @DisplayName("Adiciona filtro por nome quando informado")
    void filtersByName() {
        PageResult<MenuItemDsResponseModel> page = new PageResult<>(
            List.of(),
            0L,
            1,
            10
        );
        when(menuItemDsGateway.findAll(any(), anyInt(), anyInt()))
            .thenReturn(page);

        interactor.execute(new GetAllMenuItemsRequestModel("Risoto", 1, 10));

        ArgumentCaptor<SearchQuery> captor =
            ArgumentCaptor.forClass(SearchQuery.class);
        verify(menuItemDsGateway).findAll(
            captor.capture(),
            anyInt(),
            anyInt()
        );

        assertEquals(1, captor.getValue().criteria().size());
        assertEquals("name", captor.getValue().criteria().getFirst().field());
        assertEquals(
            FilterOperator.LIKE,
            captor.getValue().criteria().getFirst().operator()
        );
        assertEquals(
            "Risoto",
            captor.getValue().criteria().getFirst().value()
        );
    }

    @Test
    @DisplayName("Ignora filtro por nome quando contém apenas espaços")
    void ignoresBlankNameFilter() {
        PageResult<MenuItemDsResponseModel> page = new PageResult<>(
            List.of(),
            0L,
            1,
            10
        );
        when(menuItemDsGateway.findAll(any(), anyInt(), anyInt()))
            .thenReturn(page);

        interactor.execute(new GetAllMenuItemsRequestModel("   ", 1, 10));

        ArgumentCaptor<SearchQuery> captor =
            ArgumentCaptor.forClass(SearchQuery.class);
        verify(menuItemDsGateway).findAll(
            captor.capture(),
            anyInt(),
            anyInt()
        );

        assertEquals(0, captor.getValue().criteria().size());
    }

    private static MenuItemDsResponseModel menuItem(String name) {
        return new MenuItemDsResponseModel(
            UUID.randomUUID(),
            name,
            "Descrição",
            new BigDecimal("39.90"),
            true,
            "/images/risoto.jpg",
            UUID.randomUUID()
        );
    }

    private static final class CapturingPresenter
        implements GetAllMenuItemsOutputBoundary {

        private GetAllMenuItemsResponseModel response;

        @Override
        public void present(GetAllMenuItemsResponseModel response) {
            this.response = response;
        }
    }
}
