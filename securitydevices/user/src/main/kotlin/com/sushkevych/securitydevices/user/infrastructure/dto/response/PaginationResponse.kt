package com.sushkevych.securitydevices.user.infrastructure.dto.response

data class OffsetPaginateResponse(
    val data: List<UserResponse>,
    val totalCount: Long
)

data class CursorPaginateResponse(
    val data: List<UserResponse>,
    val nextCursor: String?,
    val totalCount: Long
)
