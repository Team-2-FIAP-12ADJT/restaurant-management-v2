package com.fiap.restaurant_management_v2.infrastructure.web;

import com.fiap.restaurant_management_v2.adapters.controllers.RestaurantController;
import com.fiap.restaurant_management_v2.adapters.presenters.CreateRestaurantPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetAllRestaurantsPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.GetRestaurantByIdPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.UpdateRestaurantPresenter;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.PageViewModel;
import com.fiap.restaurant_management_v2.adapters.presenters.viewmodel.RestaurantWithOwnerViewModel;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.CreateRestaurantRequest;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.GetAllRestaurantsParams;
import com.fiap.restaurant_management_v2.infrastructure.web.dto.UpdateRestaurantRequest;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(ApiPaths.RESTAURANTS)
public class RestaurantApi {

    private final RestaurantController restaurantController;
    private final CreateRestaurantPresenter createRestaurantPresenter;
    private final GetAllRestaurantsPresenter getAllRestaurantsPresenter;
    private final GetRestaurantByIdPresenter getRestaurantByIdPresenter;
    private final UpdateRestaurantPresenter updateRestaurantPresenter;

    public RestaurantApi(
        RestaurantController restaurantController,
        CreateRestaurantPresenter createRestaurantPresenter,
        GetAllRestaurantsPresenter getAllRestaurantsPresenter,
        GetRestaurantByIdPresenter getRestaurantByIdPresenter,
        UpdateRestaurantPresenter updateRestaurantPresenter
    ) {
        this.restaurantController = restaurantController;
        this.createRestaurantPresenter = createRestaurantPresenter;
        this.getAllRestaurantsPresenter = getAllRestaurantsPresenter;
        this.getRestaurantByIdPresenter = getRestaurantByIdPresenter;
        this.updateRestaurantPresenter = updateRestaurantPresenter;
    }

    @PostMapping
    public ResponseEntity<RestaurantWithOwnerViewModel> create(
        @Valid @RequestBody CreateRestaurantRequest request
    ) {
        restaurantController.create(
            request.name(),
            request.address(),
            request.taxIdentifier(),
            request.cuisineType(),
            request.openingHours(),
            request.ownerId()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(
            createRestaurantPresenter.getViewModel()
        );
    }

    @GetMapping
    public ResponseEntity<PageViewModel<RestaurantWithOwnerViewModel>> getAll(
        @ParameterObject GetAllRestaurantsParams params
    ) {
        restaurantController.getAll(
            params.name(),
            params.taxIdentifier(),
            params.cuisineType(),
            params.page(),
            params.size()
        );

        return ResponseEntity.ok(getAllRestaurantsPresenter.getViewModel());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantWithOwnerViewModel> getById(
        @PathVariable UUID id
    ) {
        restaurantController.getById(id);
        return ResponseEntity.ok(getRestaurantByIdPresenter.getViewModel());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RestaurantWithOwnerViewModel> update(
        @PathVariable UUID id,
        @Valid @RequestBody UpdateRestaurantRequest request
    ) {
        restaurantController.update(
            id,
            request.name(),
            request.address(),
            request.taxIdentifier(),
            request.cuisineType(),
            request.openingHours(),
            request.ownerId()
        );

        return ResponseEntity.ok(updateRestaurantPresenter.getViewModel());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        restaurantController.delete(id);
        return ResponseEntity.noContent().build();
    }
}
