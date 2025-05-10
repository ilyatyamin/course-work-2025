package org.ilyatyamin.yacontesthelper.client.dto

data class GetUserInfoRequest(
    val username: String,
)

data class GetUserInfoResponse(
    val username: String,
    val firstName: String?,
    val lastName: String?,
)