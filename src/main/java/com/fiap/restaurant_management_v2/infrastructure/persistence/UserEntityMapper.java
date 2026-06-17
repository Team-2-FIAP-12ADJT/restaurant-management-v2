package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.UserDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;

final class UserEntityMapper {

    private UserEntityMapper() {}

    static UserEntity toEntity(UserDsRequestModel user) {
        return UserEntity.builder()
            .id(user.id())
            .name(user.name())
            .email(user.email())
            .login(user.login())
            .password(user.password())
            .build();
    }

    static UserDsResponseModel toDsResponse(UserEntity entity) {
        return new UserDsResponseModel(
            entity.getId(),
            entity.getName(),
            entity.getEmail(),
            entity.getLogin()
        );
    }
}
