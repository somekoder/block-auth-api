package com.somekoder.block.auth.api.server.response.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class EmailErrorResponse {
    @Serializable
    @SerialName(REGEX_VERIFICATION_FAILED)
    data class RegexVerificationFailed(val regex: String) : EmailErrorResponse()
}

private const val REGEX_VERIFICATION_FAILED = "REGEX_VERIFICATION_FAILED"