package com.somekoder.block.auth.api.domain.util

import com.somekoder.block.auth.api.domain.model.EmailError

class EmailValidator(
    private val regexPattern: String
) {

    operator fun invoke(email: String) : List<EmailError> {
        val regex = Regex(regexPattern)
        val matches = regex.matches(email)
        return if (matches) emptyList() else listOf(EmailError.RegexViolated(regexPattern))
    }
}