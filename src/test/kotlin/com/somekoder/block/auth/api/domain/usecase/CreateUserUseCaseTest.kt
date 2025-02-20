package com.somekoder.block.auth.api.domain.usecase

import com.somekoder.block.auth.api.domain.datasource.IDatasource
import com.somekoder.block.auth.api.domain.datasource.result.CreateResult
import com.somekoder.block.auth.api.domain.model.EmailError
import com.somekoder.block.auth.api.domain.model.PasswordError
import com.somekoder.block.auth.api.domain.model.User
import com.somekoder.block.auth.api.domain.util.EmailValidator
import com.somekoder.block.auth.api.domain.util.PasswordValidator
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import java.util.*
import kotlin.test.BeforeTest
import kotlin.test.Test

class CreateUserUseCaseTest {

    private val datasource: IDatasource = mockk()
    private val passwordValidator: PasswordValidator = mockk()
    private val emailValidator: EmailValidator = mockk()
    private val useCase = CreateUserUseCase(datasource, passwordValidator, emailValidator)

    companion object {

    }

    @BeforeTest
    fun setUp() {
        every { passwordValidator.invoke(any()) } returns emptyList()
        every { emailValidator.invoke(any()) } returns emptyList()
    }

    @Test
    fun testCreateUser_badPassword() = runTest {
        val password = "password"
        val errors = listOf(PasswordError.MissingNumber, PasswordError.TooShort(8))
        every { passwordValidator.invoke(password) } returns errors
        val result = useCase.invoke("email@example.com", password)
        val expected = CreateUserUseCase.Result.ValidationError(emailErrors = emptyList(), passwordErrors = errors)
        assertEquals(expected, result)
    }

    @Test
    fun testCreateUser_badEmail() = runTest {
        val email = "email@example.com"
        val errors = listOf(EmailError.RegexViolated("actual_regex"))
        every { emailValidator.invoke(email) } returns errors
        val result = useCase.invoke(email, "password")
        val expected = CreateUserUseCase.Result.ValidationError(emailErrors = errors, passwordErrors = emptyList())
        assertEquals(expected, result)
    }

    @Test
    fun testCreateUser_badEmailAndBadPassword() = runTest {
        val email = "email@example.com"
        val password = "password"
        val emailErrors = listOf(EmailError.RegexViolated("actual_regex"))
        val passwordErrors = listOf(PasswordError.MissingLowercase, PasswordError.MissingSpecialChar)
        every { emailValidator.invoke(email) } returns emailErrors
        every { passwordValidator.invoke(password) } returns passwordErrors
        val result = useCase.invoke(email, password)
        val expected = CreateUserUseCase.Result.ValidationError(emailErrors = emailErrors, passwordErrors = passwordErrors)
        assertEquals(expected, result)
    }

    @Test
    fun testCreateUser_datasourceFailure() = runTest {
        val email = "email@example.com"
        val password = "password"
        val error = CreateResult.Failure("Something went wrong")
        coEvery { datasource.createUser(email, password) } returns error

        val result = useCase.invoke(email, password)
        val expected = CreateUserUseCase.Result.Failure(error.message)
        assertEquals(expected, result)
    }

    @Test
    fun testCreateUser_uniqueViolation() = runTest {
        val email = "email@example.com"
        val password = "password"
        val error = CreateResult.UniqueViolation
        coEvery { datasource.createUser(email, password) } returns error

        val result = useCase.invoke(email, password)
        val expected = CreateUserUseCase.Result.UniqueViolation
        assertEquals(expected, result)
    }

    @Test
    fun testCreateUser_success() = runTest {
        val email = "email@example.com"
        val password = "password"
        val success = CreateResult.Success(User(id = UUID.randomUUID(), email = email, passwordHash = password, createdAt = 0))
        coEvery { datasource.createUser(email, password) } returns success

        val result = useCase.invoke(email, password)
        val expected = CreateUserUseCase.Result.Success(success.data)
        assertEquals(expected, result)
    }
}