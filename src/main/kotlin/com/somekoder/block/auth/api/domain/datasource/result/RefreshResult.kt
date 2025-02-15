package com.somekoder.block.auth.api.domain.datasource.result

import com.somekoder.block.auth.api.domain.model.RefreshToken
import com.somekoder.block.auth.api.domain.model.Token

sealed class RefreshResult {
    data object Invalid : RefreshResult()
    data class Success(
        val token: Token,
        val refreshToken: RefreshToken,
    ) : RefreshResult()
    data class Failure(val error: String) : RefreshResult()
}