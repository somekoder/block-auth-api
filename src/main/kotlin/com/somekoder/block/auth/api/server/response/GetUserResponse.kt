package com.somekoder.block.auth.api.server.response

import kotlinx.serialization.Serializable

@Serializable
data class GetUserResponse(
    val userId: String,
    val email: String,
)