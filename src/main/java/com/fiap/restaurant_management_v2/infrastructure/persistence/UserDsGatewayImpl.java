package com.fiap.restaurant_management_v2.infrastructure.persistence;

import com.fiap.restaurant_management_v2.application.gateways.UserDsGateway;
import com.fiap.restaurant_management_v2.application.gateways.UserDsRequestModel;
import com.fiap.restaurant_management_v2.application.gateways.UserDsResponseModel;

public class UserDsGatewayImpl implements UserDsGateway {
    private final UserJpaRepository jpaRepository;

    public UserDsGatewayImpl(UserJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public UserDsResponseModel save(UserDsRequestModel user) {
        UserEntity saved = jpaRepository.save(UserEntityMapper.toEntity(user));
        return UserEntityMapper.toDsResponse(saved);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByLogin(String login) {
        return jpaRepository.existsByLogin(login);
    }
}
