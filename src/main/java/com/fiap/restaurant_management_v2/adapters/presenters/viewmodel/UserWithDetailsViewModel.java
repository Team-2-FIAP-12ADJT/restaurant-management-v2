package com.fiap.restaurant_management_v2.adapters.presenters.viewmodel;

import java.util.List;

/** View-ready user data com userType + restaurantes nested. Usado só por
 * GetById/GetAll — create/update seguem com {@link UserViewModel} flat. */
public record UserWithDetailsViewModel(
    String id,
    String name,
    String email,
    String login,
    String taxIdentifier,
    String userType,
    List<RestaurantViewModel> restaurants
) {}
