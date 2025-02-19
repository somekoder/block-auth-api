package com.somekoder.block.auth.api.server.response.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class PasswordErrorResponse {
    @Serializable
    @SerialName(TOO_SHORT)
    data class TooShort(val min: Int) : PasswordErrorResponse()

    @Serializable
    @SerialName(TOO_LONG)
    data class TooLong(val max: Int) : PasswordErrorResponse()

    @Serializable
    @SerialName(MISSING_NUMBER)
    data object MissingNumber : PasswordErrorResponse()

    @Serializable
    @SerialName(MISSING_SPECIAL_CHAR)
    data object MissingSpecialChar : PasswordErrorResponse()

    @Serializable
    @SerialName(MISSING_UPPERCASE)
    data object MissingUppercase : PasswordErrorResponse()

    @Serializable
    @SerialName(MISSING_LOWERCASE)
    data object MissingLowercase : PasswordErrorResponse()

    @Serializable
    @SerialName(ILLEGAL_CHAR)
    data class IllegalChar(val illegalChars: List<Char>) : PasswordErrorResponse()
}

private const val TOO_SHORT = "TOO_SHORT"
private const val TOO_LONG = "TOO_LONG"
private const val MISSING_NUMBER = "MISSING_NUMBER"
private const val MISSING_SPECIAL_CHAR = "MISSING_SPECIAL_CHAR"
private const val MISSING_UPPERCASE = "MISSING_UPPERCASE"
private const val MISSING_LOWERCASE = "MISSING_LOWERCASE"
private const val ILLEGAL_CHAR = "ILLEGAL_CHAR"