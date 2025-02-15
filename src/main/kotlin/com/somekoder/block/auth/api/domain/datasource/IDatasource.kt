package com.somekoder.block.auth.api.domain.datasource

import com.somekoder.block.auth.api.domain.datasource.result.CreateResult
import com.somekoder.block.auth.api.domain.datasource.result.GetResult
import com.somekoder.block.auth.api.domain.datasource.result.LoginResult
import com.somekoder.block.auth.api.domain.datasource.result.RefreshResult
import com.somekoder.block.auth.api.domain.model.User
import java.util.UUID

interface IDatasource {
    suspend fun getUserByEmail(email: String): GetResult<User>
    suspend fun getUserById(id: UUID): GetResult<User>
    suspend fun createUser(email: String, password: String): CreateResult<User>
    suspend fun login(email: String, password: String): LoginResult
    suspend fun refreshToken(userId: UUID, refreshToken: String): RefreshResult
}