package com.fiap.restaurant_management_v2.application.usecases.usertype.get_all;

import com.fiap.restaurant_management_v2.application.pagination.PageResult;

public record GetAllUsersTypeResponseModel(PageResult<UserTypeSummary> page) {}
