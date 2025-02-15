package com.somekoder.block.auth.api.domain.model

data class PasswordRules(
    val minLength: Int,
    val maxLength: Int,
    val requireNumber: Boolean,
    val requireSpecialChar: Boolean,
    val requireUppercase: Boolean,
    val requireLowercase: Boolean,
    val allowedChars: List<Char>,
    val allowedSpecialChars: List<Char>
)