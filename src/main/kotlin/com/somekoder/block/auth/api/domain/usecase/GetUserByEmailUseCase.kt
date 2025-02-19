package com.somekoder.block.auth.api.domain.usecase

import com.somekoder.block.auth.api.domain.datasource.IDatasource
import com.somekoder.block.auth.api.domain.datasource.result.GetResult
import com.somekoder.block.auth.api.domain.model.User

class GetUserByEmailUseCase(
    private val datasource: IDatasource,
) {

    suspend operator fun invoke(email: String): GetResult<User> {
        return datasource.getUserByEmail(email)
    }
}