package com.somekoder.block.auth.api.domain.model

data class Token(
    val token: String,
    val expiresAt: Long
)
