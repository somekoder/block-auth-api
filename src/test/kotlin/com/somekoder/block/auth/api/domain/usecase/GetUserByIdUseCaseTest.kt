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

class GetUserByIdUseCaseTest {

    private val datasource: IDatasource = mockk()
    private val useCase = GetUserByIdUseCase(datasource)

    @BeforeTest
    fun setUp() {

    }

    @Test
    fun testGetUserById_Success() = runTest {
        val id = UUID.randomUUID()
        val success = GetResult.Success(User(email = "email", id = id, createdAt = 0, passwordHash = "password"))
        coEvery { datasource.getUserById(id) } returns success
        val actual = useCase.invoke(id)
        assertEquals(success, actual)
    }

    @Test
    fun testGetUserById_Failure() = runTest {
        val id = UUID.randomUUID()
        val failure = GetResult.Failure("An error occurred")
        coEvery { datasource.getUserById(id) } returns failure
        val actual = useCase.invoke(id)
        assertEquals(failure, actual)
    }

    @Test
    fun testGetUserById_NotFound() = runTest {
        val id = UUID.randomUUID()
        val failure = GetResult.NotFound
        coEvery { datasource.getUserById(id) } returns failure
        val actual = useCase.invoke(id)
        assertEquals(failure, actual)
    }
}