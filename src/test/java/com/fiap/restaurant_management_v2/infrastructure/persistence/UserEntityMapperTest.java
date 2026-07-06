package com.fiap.restaurant_management_v2.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserEntityMapperTest {

    @Test
    void toBindDsResponseMapsEntityFields() {
        UUID id = UUID.randomUUID();
        var entity = UserEntity.builder()
            .id(id)
            .name("Ada")
            .email("ada@example.com")
            .login("ada")
            .password("hash")
            .build();

        var response = UserEntityMapper.toBindDsResponse(entity);

        assertEquals(id, response.id());
        assertEquals("Ada", response.name());
        assertEquals("ada@example.com", response.email());
        assertEquals("ada", response.login());
        assertEquals("hash", response.passWord());
    }
}
