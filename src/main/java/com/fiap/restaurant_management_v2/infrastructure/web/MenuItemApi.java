package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.adapters.controllers.MenuItemController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateMenuItemPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetAllMenuItemsPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetMenuItemByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetMenuItemsByRestaurantPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.UpdateMenuItemPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.MenuItemViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.CreateMenuItemRequest;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.GetAllMenuItemsParams;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.GetMenuItemsByRestaurantParams;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.UpdateMenuItemRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.MENU_ITEMS)
public class MenuItemApi {

    private final MenuItemController menuItemController;
    private final CreateMenuItemPresenter createPresenter;
    private final GetAllMenuItemsPresenter getAllPresenter;
    private final GetMenuItemByIdPresenter getByIdPresenter;
    private final GetMenuItemsByRestaurantPresenter getByRestaurantPresenter;
    private final UpdateMenuItemPresenter updatePresenter;

    public MenuItemApi(
        MenuItemController menuItemController,
        CreateMenuItemPresenter createPresenter,
        GetAllMenuItemsPresenter getAllPresenter,
        GetMenuItemByIdPresenter getByIdPresenter,
        GetMenuItemsByRestaurantPresenter getByRestaurantPresenter,
        UpdateMenuItemPresenter updatePresenter
    ) {
        this.menuItemController = menuItemController;
        this.createPresenter = createPresenter;
        this.getAllPresenter = getAllPresenter;
        this.getByIdPresenter = getByIdPresenter;
        this.getByRestaurantPresenter = getByRestaurantPresenter;
        this.updatePresenter = updatePresenter;
    }

    @PostMapping
    public ResponseEntity<MenuItemViewModel> create(
        @Valid @RequestBody CreateMenuItemRequest request
    ) {
        menuItemController.create(
            request.name(),
            request.description(),
            request.price(),
            request.onlyLocal(),
            request.photoPath(),
            request.restaurantId()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(createPresenter.getViewModel());
    }

    @GetMapping
    public ResponseEntity<PageViewModel<MenuItemViewModel>> getAll(
        @ParameterObject GetAllMenuItemsParams params
    ) {
        menuItemController.getAll(
            params.name(),
            params.page(),
            params.size()
        );
        return ResponseEntity.ok(getAllPresenter.getViewModel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MenuItemViewModel> getById(@PathVariable UUID id) {
        menuItemController.getById(id);
        return ResponseEntity.ok(getByIdPresenter.getViewModel());
    }

    @GetMapping("/restaurant/{restaurantId}")
    public ResponseEntity<PageViewModel<MenuItemViewModel>> getByRestaurant(
        @PathVariable UUID restaurantId,
        @ParameterObject GetMenuItemsByRestaurantParams params
    ) {
        menuItemController.getByRestaurant(
            restaurantId,
            params.page(),
            params.size()
        );
        return ResponseEntity.ok(getByRestaurantPresenter.getViewModel());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<MenuItemViewModel> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateMenuItemRequest request
    ) {
        menuItemController.update(
            id,
            request.name(),
            request.description(),
            request.price(),
            request.onlyLocal(),
            request.photoPath(),
            request.restaurantId()
        );
        return ResponseEntity.ok(updatePresenter.getViewModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        menuItemController.delete(id);
        return ResponseEntity.noContent().build();
    }
}
