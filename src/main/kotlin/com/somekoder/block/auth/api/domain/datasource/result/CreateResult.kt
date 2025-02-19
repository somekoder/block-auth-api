package com.somekoder.block.auth.api.domain.datasource.result

sealed class CreateResult<out T> {
    data class Success<T>(val data: T) : CreateResult<T>()
    data class Failure(val message: String) : CreateResult<Nothing>()
    data object UniqueViolation : CreateResult<Nothing>()
}