package com.somekoder.block.auth.api.domain.usecase

import com.somekoder.block.auth.api.domain.datasource.IDatasource
import com.somekoder.block.auth.api.domain.datasource.result.LoginResult
import com.somekoder.block.auth.api.domain.model.RefreshToken
import com.somekoder.block.auth.api.domain.model.Token
import com.somekoder.block.auth.api.domain.model.User
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.util.*
import kotlin.test.assertEquals

class LoginUseCaseTest {

    private val datasource: IDatasource = mockk()
    private val useCase = LoginUseCase(datasource)
    private val user = User(UUID.randomUUID(), "userName", "password", createdAt = 0)
    private val token = Token(token = "token", expiresAt = 10)
    private val refreshToken = RefreshToken(refreshToken = "token", expiresAt = 10, refreshTokenHash = "refreshTokenHash", createdAt = 0)

    @Test
    fun testLogin_success() = runTest {
        val expectedResult = LoginResult.Success(
            user = user,
            token = token,
            refreshToken = refreshToken
        )
        coEvery { datasource.login(any(), any()) } returns expectedResult
        val result = useCase.invoke("email", "password")
        assertEquals(expectedResult, result)
    }

    @Test
    fun testLogin_failure() = runTest {
        val expectedResult = LoginResult.Failure("An error occurred")
        coEvery { datasource.login(any(), any()) } returns expectedResult
        val result = useCase.invoke("email", "password")
        assertEquals(expectedResult, result)
    }

    @Test
    fun testLogin_notFound() = runTest {
        val expectedResult = LoginResult.NotFound
        coEvery { datasource.login(any(), any()) } returns expectedResult
        val result = useCase.invoke("email", "password")
        assertEquals(expectedResult, result)
    }

    @Test
    fun testLogin_invalidPassword() = runTest {
        val expectedResult = LoginResult.InvalidPassword
        coEvery { datasource.login(any(), any()) } returns expectedResult
        val result = useCase.invoke("email", "password")
        assertEquals(expectedResult, result)
    }
}