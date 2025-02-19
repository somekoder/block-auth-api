package com.somekoder.block.auth.api.domain.datasource.result

sealed class GetResult<out T> {
    data class Success<T>(val data: T) : GetResult<T>()
    data class Failure(val message: String) : GetResult<Nothing>()
    data object NotFound : GetResult<Nothing>()
}