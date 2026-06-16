package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.UserDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;
import java.time.Instant;

final class UserEntityMapper {
    private UserEntityMapper() {}

    static UserEntity toEntity(UserDsRequestModel user) {
        Instant now = Instant.now();
        return new UserEntity(
            user.id(),
            user.name(),
            user.email(),
            user.login(),
            user.password(),
            now,
            now
        );
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
