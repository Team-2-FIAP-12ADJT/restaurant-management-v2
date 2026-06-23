package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.MenuItemViewModel;
import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.create.CreateMenuItemResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.delete.DeleteMenuItemResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.GetAllMenuItemsResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.MenuItemSummary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id.GetMenuItemByIdResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant.GetMenuItemsByRestaurantResponseModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.update.UpdateMenuItemResponseModel;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MenuItemPresentersTest {

    private static final UUID ID = UUID.randomUUID();
    private static final UUID RESTAURANT_ID = UUID.randomUUID();
    private static final BigDecimal PRICE = new BigDecimal("39.90");

    @Test
    @DisplayName("Create presenter converte response em view model")
    void createPresenterBuildsViewModel() {
        CreateMenuItemPresenter presenter = new CreateMenuItemPresenter();
        assertNull(presenter.getViewModel());

        presenter.present(
            new CreateMenuItemResponseModel(
                ID,
                "Risoto",
                "Risoto de cogumelos",
                PRICE,
                true,
                "/images/risoto.jpg",
                RESTAURANT_ID
            )
        );

        assertViewModel(presenter.getViewModel());
    }

    @Test
    @DisplayName("Get by id presenter converte response em view model")
    void getByIdPresenterBuildsViewModel() {
        GetMenuItemByIdPresenter presenter = new GetMenuItemByIdPresenter();
        assertNull(presenter.getViewModel());

        presenter.present(
            new GetMenuItemByIdResponseModel(
                ID,
                "Risoto",
                "Risoto de cogumelos",
                PRICE,
                true,
                "/images/risoto.jpg",
                RESTAURANT_ID
            )
        );

        assertViewModel(presenter.getViewModel());
    }

    @Test
    @DisplayName("Update presenter converte response em view model")
    void updatePresenterBuildsViewModel() {
        UpdateMenuItemPresenter presenter = new UpdateMenuItemPresenter();
        assertNull(presenter.getViewModel());

        presenter.present(
            new UpdateMenuItemResponseModel(
                ID,
                "Risoto",
                "Risoto de cogumelos",
                PRICE,
                true,
                "/images/risoto.jpg",
                RESTAURANT_ID
            )
        );

        assertViewModel(presenter.getViewModel());
    }

    @Test
    @DisplayName("Get all presenter converte página e conteúdo")
    void getAllPresenterBuildsPage() {
        GetAllMenuItemsPresenter presenter = new GetAllMenuItemsPresenter();
        assertNull(presenter.getViewModel());

        presenter.present(
            new GetAllMenuItemsResponseModel(page())
        );

        assertEquals(1, presenter.getViewModel().page());
        assertEquals(10, presenter.getViewModel().size());
        assertEquals(1, presenter.getViewModel().totalElements());
        assertEquals(1, presenter.getViewModel().totalPages());
        assertViewModel(presenter.getViewModel().content().getFirst());
    }

    @Test
    @DisplayName("Presenter por restaurante converte página e conteúdo")
    void getByRestaurantPresenterBuildsPage() {
        GetMenuItemsByRestaurantPresenter presenter =
            new GetMenuItemsByRestaurantPresenter();
        assertNull(presenter.getViewModel());

        presenter.present(
            new GetMenuItemsByRestaurantResponseModel(page())
        );

        assertEquals(1, presenter.getViewModel().content().size());
        assertViewModel(presenter.getViewModel().content().getFirst());
    }

    @Test
    @DisplayName("Delete presenter marca item como excluído")
    void deletePresenterMarksItemAsDeleted() {
        DeleteMenuItemPresenter presenter = new DeleteMenuItemPresenter();
        assertFalse(presenter.isDeleted());

        presenter.present(new DeleteMenuItemResponseModel(ID));

        assertTrue(presenter.isDeleted());
    }

    private static PageResult<MenuItemSummary> page() {
        return new PageResult<>(
            List.of(
                new MenuItemSummary(
                    ID,
                    "Risoto",
                    "Risoto de cogumelos",
                    PRICE,
                    true,
                    "/images/risoto.jpg",
                    RESTAURANT_ID
                )
            ),
            1,
            1,
            10
        );
    }

    private static void assertViewModel(MenuItemViewModel viewModel) {
        assertEquals(ID.toString(), viewModel.id());
        assertEquals("Risoto", viewModel.name());
        assertEquals("Risoto de cogumelos", viewModel.description());
        assertEquals(PRICE, viewModel.price());
        assertTrue(viewModel.onlyLocal());
        assertEquals("/images/risoto.jpg", viewModel.photoPath());
        assertEquals(RESTAURANT_ID.toString(), viewModel.restaurantId());
    }
}
