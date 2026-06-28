package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserTypeDsResponseModel;

final class UserTypeEntityMapper {

    private UserTypeEntityMapper() {}

    static UserTypeEntity toEntity(UserTypeDsRequestModel userType ) {
        return UserTypeEntity.builder()
                .id(userType.id())
                .userType(userType.userType())
                .build();
    }


    static UserTypeDsResponseModel toDsResponse(UserTypeEntity entity) {
        return new UserTypeDsResponseModel(
            entity.getId(),
            entity.getUserType()
        );
    }
}
