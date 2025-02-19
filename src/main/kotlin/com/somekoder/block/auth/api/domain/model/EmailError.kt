package com.somekoder.block.auth.api.domain.model

sealed class EmailError {
    data class RegexViolated(val regex: String) : EmailError()
}