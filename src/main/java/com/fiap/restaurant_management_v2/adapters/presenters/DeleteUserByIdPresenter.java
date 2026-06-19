package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.application.usecases.user.delete.DeleteUserByIdOutputBoundary;

/**
 * No-op presenter: a successful delete returns 204 No Content with no body, so
 * there is no view model to build. The boundary still exists to keep the use
 * case symmetric with the others (the interactor depends on the abstraction).
 */
public class DeleteUserByIdPresenter implements DeleteUserByIdOutputBoundary {

    @Override
    public void present() {
        // nothing to present — 204 No Content
    }
}
