package com.somekoder.block.auth.api.domain.usecase

import com.somekoder.block.auth.api.domain.datasource.IDatasource
import com.somekoder.block.auth.api.domain.datasource.result.RefreshResult
import java.util.UUID

class RefreshUseCase(
    private val datasource: IDatasource,
) {

    suspend operator fun invoke(userId: UUID, refreshToken: String): RefreshResult {
        return datasource.refreshToken(userId = userId, refreshToken = refreshToken)
    }
}