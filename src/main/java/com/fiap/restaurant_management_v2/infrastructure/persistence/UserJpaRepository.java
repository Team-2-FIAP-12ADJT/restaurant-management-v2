package com.fiap.restaurant_management_v2.infrastructure.persistence;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {
    boolean existsByEmail(String email);

    boolean existsByLogin(String login);
}
