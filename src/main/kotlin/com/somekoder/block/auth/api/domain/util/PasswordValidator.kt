package com.somekoder.block.auth.api.domain.util

import com.somekoder.block.auth.api.domain.model.PasswordError

data class PasswordValidator(
    private val minLength: Int,
    private val maxLength: Int,
    private val requireNumber: Boolean,
    private val requireSpecial: Boolean,
    private val requireLowercase: Boolean,
    private val requireUppercase: Boolean,
    private val allowedChars: Set<Char>,
    private val allowedSpecialChars: Set<Char>,
) {

    operator fun invoke(password: String) : List<PasswordError> {
        val errors = mutableListOf<PasswordError>()

        if (password.length < minLength) {
            errors.add(PasswordError.TooShort(minLength))
        }
        if (password.length > maxLength) {
            errors.add(PasswordError.TooLong(maxLength))
        }
        if (requireNumber && password.none { it in '0'..'9' }) {
            errors.add(PasswordError.MissingNumber)
        }
        if (requireSpecial && password.none { it in allowedSpecialChars }) {
            errors.add(PasswordError.MissingSpecialChar)
        }
        if (requireUppercase && password.none { it in 'A'..'Z' }) {
            errors.add(PasswordError.MissingUppercase)
        }
        if (requireLowercase && password.none { it in 'a'..'z' }) {
            errors.add(PasswordError.MissingLowercase)
        }
        val illegalChars = password.toCharArray().filter { it !in allowedChars }
        if (illegalChars.isNotEmpty()) {
            errors.add(PasswordError.IllegalChar(illegalChars))
        }
        return errors
    }
}