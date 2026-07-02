package com.fiap.restaurant_management_v2.adapters.presenters.viewmodel;

/** View-ready user data (id already formatted as string). Never exposes the password. */
public record UserViewModel(
    String id,
    String name,
    String email,
    String login,
    String taxIdentifier
) {}
