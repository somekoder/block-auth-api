package com.somekoder.block.auth.api.server.response

import kotlinx.serialization.Serializable

@Serializable
data class RefreshResponse(
    val token: String,
    val tokenExpiresAt: Long,
    val refreshToken: String,
    val refreshTokenExpiresAt: Long
)