package com.fiap.restaurant_management_v2.adapters.controllers;

import com.fiap.restaurant_management_v2.application.usecases.menuitem.create.CreateMenuItemInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.create.CreateMenuItemRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.delete.DeleteMenuItemInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.delete.DeleteMenuItemRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.GetAllMenuItemsInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_all.GetAllMenuItemsRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id.GetMenuItemByIdInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_id.GetMenuItemByIdRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant.GetMenuItemsByRestaurantInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.get_by_restaurant.GetMenuItemsByRestaurantRequestModel;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.update.UpdateMenuItemInputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.update.UpdateMenuItemRequestModel;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MenuItemControllerTest {

    @Mock
    private CreateMenuItemInputBoundary create;
    @Mock
    private GetAllMenuItemsInputBoundary getAll;
    @Mock
    private GetMenuItemByIdInputBoundary getById;
    @Mock
    private GetMenuItemsByRestaurantInputBoundary getByRestaurant;
    @Mock
    private UpdateMenuItemInputBoundary update;
    @Mock
    private DeleteMenuItemInputBoundary delete;

    private MenuItemController controller;

    @BeforeEach
    void setUp() {
        controller = new MenuItemController(
            create,
            getAll,
            getById,
            getByRestaurant,
            update,
            delete
        );
    }

    @Test
    void delegatesAllOperations() {
        UUID id = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        BigDecimal price = new BigDecimal("39.90");

        controller.create(
            "Risoto",
            "Descrição",
            price,
            true,
            "/images/risoto.jpg",
            restaurantId
        );
        controller.getAll("Risoto", 1, 10);
        controller.getById(id);
        controller.getByRestaurant(restaurantId, 2, 20);
        controller.update(
            id,
            "Risoto especial",
            "Descrição",
            price,
            false,
            "/images/especial.jpg",
            restaurantId
        );
        controller.delete(id);

        ArgumentCaptor<CreateMenuItemRequestModel> createCaptor =
            ArgumentCaptor.forClass(CreateMenuItemRequestModel.class);
        ArgumentCaptor<UpdateMenuItemRequestModel> updateCaptor =
            ArgumentCaptor.forClass(UpdateMenuItemRequestModel.class);

        verify(create).execute(createCaptor.capture());
        verify(getAll).execute(
            new GetAllMenuItemsRequestModel("Risoto", 1, 10)
        );
        verify(getById).execute(new GetMenuItemByIdRequestModel(id));
        verify(getByRestaurant).execute(
            new GetMenuItemsByRestaurantRequestModel(restaurantId, 2, 20)
        );
        verify(update).execute(updateCaptor.capture());
        verify(delete).execute(new DeleteMenuItemRequestModel(id));

        assertEquals(restaurantId, createCaptor.getValue().restaurantId());
        assertEquals(id, updateCaptor.getValue().id());
        assertEquals("Risoto especial", updateCaptor.getValue().name());
    }
}
