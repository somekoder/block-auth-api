package com.somekoder.block.auth.api.server.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequest(
    val email: String,
    val password: String
)