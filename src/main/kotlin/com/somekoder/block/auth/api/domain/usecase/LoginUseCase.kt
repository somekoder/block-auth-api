package com.somekoder.block.auth.api.domain.usecase

import com.somekoder.block.auth.api.domain.datasource.IDatasource
import com.somekoder.block.auth.api.domain.datasource.result.LoginResult

class LoginUseCase(
    private val datasource: IDatasource,
) {

    suspend operator fun invoke(email: String, password: String): LoginResult {
        return datasource.login(email, password)
    }
}