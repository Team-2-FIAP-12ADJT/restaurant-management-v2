package com.fiap.restaurant_management_v2.application.usecases.user.get_all;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fiap.restaurant_management_v2.application.pagination.PageResult;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class GetAllModelsTest {

    @Test
    void requestModel_fieldsAreAccessible() {
        var req = new GetAllUsersRequestModel("n", "e", "l", "t", 2, 20);
        assertEquals("n", req.name());
        assertEquals("e", req.email());
        assertEquals("l", req.login());
        assertEquals("t", req.taxIdentifier());
        assertEquals(2, req.page());
        assertEquals(20, req.size());
    }

    @Test
    void userSummary_and_responseModel_work() {
        var id = UUID.randomUUID();
        var summary = new UserSummary(id, "Name", "a@b", "login", "123", "DONO", List.of());

        var page = new PageResult<>(List.of(summary), 1L, 1, 10);
        var resp = new GetAllUsersResponseModel(page);

        assertEquals(1, resp.page().content().size());
        assertEquals("Name", resp.page().content().getFirst().name());
        assertEquals(1, resp.page().totalPages());
    }
}

