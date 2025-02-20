package com.somekoder.block.auth.api.domain.usecase

import com.somekoder.block.auth.api.domain.datasource.IDatasource
import com.somekoder.block.auth.api.domain.datasource.result.GetResult
import com.somekoder.block.auth.api.domain.model.User
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetUserByEmailUseCaseTest {

    private val datasource: IDatasource = mockk()
    private val useCase = GetUserByEmailUseCase(datasource)

    @BeforeTest
    fun setUp() {

    }

    @Test
    fun testGetUserByEmail_Success() = runTest {
        val email = "email@example.com"
        val success = GetResult.Success(User(email = email, id = UUID.randomUUID(), createdAt = 0, passwordHash = "password"))
        coEvery { datasource.getUserByEmail(email) } returns success
        val actual = useCase.invoke(email)
        assertEquals(success, actual)
    }

    @Test
    fun testGetUserByEmail_Failure() = runTest {
        val email = "email@example.com"
        val failure = GetResult.Failure("An error occurred")
        coEvery { datasource.getUserByEmail(email) } returns failure
        val actual = useCase.invoke(email)
        assertEquals(failure, actual)
    }

    @Test
    fun testGetUserByEmail_NotFound() = runTest {
        val email = "email@example.com"
        val failure = GetResult.NotFound
        coEvery { datasource.getUserByEmail(email) } returns failure
        val actual = useCase.invoke(email)
        assertEquals(failure, actual)
    }
}