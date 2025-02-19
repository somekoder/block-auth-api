package com.somekoder.block.auth.api.domain.usecase

import com.somekoder.block.auth.api.domain.datasource.IDatasource
import com.somekoder.block.auth.api.domain.datasource.result.GetResult
import com.somekoder.block.auth.api.domain.model.User
import java.util.UUID

class GetUserByIdUseCase(
    private val datasource: IDatasource,
) {

    suspend operator fun invoke(id: UUID): GetResult<User> {
        return datasource.getUserById(id)
    }
}