package com.somekoder.block.auth.api.server.response.error

import com.somekoder.block.auth.api.domain.model.EmailError
import com.somekoder.block.auth.api.domain.model.PasswordError
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserValidationError(
    val passwordErrors: List<PasswordErrorResponse>,
    val emailErrors: List<EmailErrorResponse>
) {

    companion object {
        fun create(passwordErrors: List<PasswordError>, emailErrors: List<EmailError>) : CreateUserValidationError {
            val passwordErrorResponses = passwordErrors.map {
                when (it) {
                    is PasswordError.IllegalChar -> PasswordErrorResponse.IllegalChar(it.illegalChars)
                    is PasswordError.MissingLowercase -> PasswordErrorResponse.MissingLowercase
                    is PasswordError.MissingNumber -> PasswordErrorResponse.MissingNumber
                    is PasswordError.MissingSpecialChar -> PasswordErrorResponse.MissingSpecialChar
                    is PasswordError.MissingUppercase -> PasswordErrorResponse.MissingUppercase
                    is PasswordError.TooLong -> PasswordErrorResponse.TooLong(it.max)
                    is PasswordError.TooShort -> PasswordErrorResponse.TooShort(it.min)
                }
            }
            val emailErrorsResponse = emailErrors.map {
                when (it) {
                    is EmailError.RegexViolated -> EmailErrorResponse.RegexVerificationFailed(it.regex)
                }
            }
            return CreateUserValidationError(passwordErrorResponses, emailErrorsResponse)
        }
    }
}


