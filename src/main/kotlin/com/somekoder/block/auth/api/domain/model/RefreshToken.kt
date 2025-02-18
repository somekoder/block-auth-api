package com.somekoder.block.auth.api.domain.model

data class RefreshToken(
    val refreshToken: String,
    val refreshTokenHash: String,
    val expiresAt: Long,
    val createdAt: Long
)
