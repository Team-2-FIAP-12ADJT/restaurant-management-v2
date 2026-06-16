package com.fiap.restaurant_management_v2.application.usecases.user.get_all;

import com.fiap.restaurant_management_v2.application.pagination.PageResult;

public record GetAllUsersResponseModel(PageResult<UserSummary> page) {}
