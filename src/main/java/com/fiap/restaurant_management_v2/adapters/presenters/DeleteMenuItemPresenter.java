package com.fiap.restaurant_management_v2.adapters.presenters;

import com.fiap.restaurant_management_v2.application.usecases.menuitem.delete.DeleteMenuItemOutputBoundary;
import com.fiap.restaurant_management_v2.application.usecases.menuitem.delete.DeleteMenuItemResponseModel;

public class DeleteMenuItemPresenter
    implements DeleteMenuItemOutputBoundary {

    private boolean deleted;

    @Override
    public void present(DeleteMenuItemResponseModel response) {
        this.deleted = true;
    }

    public boolean isDeleted() {
        return deleted;
    }
}
