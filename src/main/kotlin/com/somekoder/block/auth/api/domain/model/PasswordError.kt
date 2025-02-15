package com.somekoder.block.auth.api.domain.model

sealed class PasswordError {
    data class TooShort(val min: Int) : PasswordError()
    data class TooLong(val max: Int) : PasswordError()
    data object MissingNumber : PasswordError()
    data object MissingSpecialChar : PasswordError()
    data object MissingUppercase : PasswordError()
    data object MissingLowercase : PasswordError()
    data class IllegalChar(val illegalChars: List<Char>) : PasswordError()
}