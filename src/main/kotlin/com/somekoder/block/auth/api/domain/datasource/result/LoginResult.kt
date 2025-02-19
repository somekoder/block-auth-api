package com.somekoder.block.auth.api.domain.datasource.result

import com.somekoder.block.auth.api.domain.model.RefreshToken
import com.somekoder.block.auth.api.domain.model.Token
import com.somekoder.block.auth.api.domain.model.User

sealed class LoginResult {
    data object NotFound : LoginResult()
    data object InvalidPassword : LoginResult()
    data class Success(
        val user: User,
        val token: Token,
        val refreshToken: RefreshToken
    ) : LoginResult()
    data class Failure(val error: String) : LoginResult()
}