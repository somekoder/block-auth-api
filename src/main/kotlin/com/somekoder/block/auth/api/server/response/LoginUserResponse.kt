package com.somekoder.block.auth.api.server.response

import com.somekoder.block.auth.api.domain.model.RefreshToken
import com.somekoder.block.auth.api.domain.model.Token
import com.somekoder.block.auth.api.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class LoginUserResponse(
    val userId: String,
    val email: String,
    val token: String,
    val tokenExpiresAt: Long,
    val refreshToken: String,
    val refreshTokenExpiresAt: Long
) {
    companion object {
        fun from(user: User, token: Token, refreshToken: RefreshToken): LoginUserResponse {
            return LoginUserResponse(
                email = user.email,
                userId = user.id.toString(),
                token = token.token,
                tokenExpiresAt = token.expiresAt,
                refreshToken = refreshToken.refreshToken,
                refreshTokenExpiresAt = refreshToken.expiresAt
            )
        }
    }
}
