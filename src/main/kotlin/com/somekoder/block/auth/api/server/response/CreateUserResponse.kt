package com.somekoder.block.auth.api.server.response

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserResponse(
    val userId: String,
    val email: String,
)