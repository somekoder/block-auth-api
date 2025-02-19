package com.somekoder.block.auth.api.domain.model

import java.util.UUID

data class User(
    val id: UUID,
    val email: String,
    val passwordHash: String,
    val createdAt: Long
)
