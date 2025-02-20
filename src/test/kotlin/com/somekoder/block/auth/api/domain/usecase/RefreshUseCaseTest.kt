package com.somekoder.block.auth.api.domain.usecase

import com.somekoder.block.auth.api.domain.datasource.IDatasource
import com.somekoder.block.auth.api.domain.datasource.result.RefreshResult
import com.somekoder.block.auth.api.domain.model.RefreshToken
import com.somekoder.block.auth.api.domain.model.Token
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.*

class RefreshUseCaseTest {

    private val datasource: IDatasource = mockk()
    private val useCase = RefreshUseCase(datasource)
    private val token = Token(token = "token", expiresAt = 10)
    private val refreshToken = RefreshToken(refreshToken = "token", expiresAt = 10, refreshTokenHash = "refreshTokenHash", createdAt = 0)

    @Test
    fun testRefresh_failure() = runTest {
        val expectedResult = RefreshResult.Failure("An error occurred")
        coEvery { datasource.refreshToken(any(), any()) } returns expectedResult
        val result = useCase.invoke(UUID.randomUUID(), "refreshToken")
        assertEquals(expectedResult, result)
    }

    @Test
    fun testRefresh_invalid() = runTest {
        val expectedResult = RefreshResult.Invalid
        coEvery { datasource.refreshToken(any(), any()) } returns expectedResult
        val result = useCase.invoke(UUID.randomUUID(), "refreshToken")
        assertEquals(expectedResult, result)
    }

    @Test
    fun testRefresh_success() = runTest {
        val expectedResult = RefreshResult.Success(token = token, refreshToken = refreshToken,)
        coEvery { datasource.refreshToken(any(), any()) } returns expectedResult
        val result = useCase.invoke(UUID.randomUUID(), "refreshToken")
        assertEquals(expectedResult, result)
    }
}