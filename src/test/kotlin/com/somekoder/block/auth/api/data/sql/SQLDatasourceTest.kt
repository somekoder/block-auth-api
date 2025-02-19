package com.somekoder.block.auth.api.data.sql

import com.auth0.jwt.algorithms.Algorithm
import com.somekoder.block.auth.api.data.sql.table.RefreshTokenTable
import com.somekoder.block.auth.api.data.sql.table.UserTable
import com.somekoder.block.auth.api.domain.datasource.result.CreateResult
import com.somekoder.block.auth.api.domain.datasource.result.GetResult
import com.somekoder.block.auth.api.domain.datasource.result.LoginResult
import com.somekoder.block.auth.api.domain.datasource.result.RefreshResult
import com.somekoder.block.auth.api.domain.model.User
import com.somekoder.block.auth.api.infra.RefreshTokenUtils
import com.somekoder.block.auth.api.infra.TokenUtils
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.After
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test
import java.lang.Thread.sleep
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class SQLDatasourceTest {

    companion object {
        private const val REFRESH_EXPIRES_IN = 2000L
    }

    val database = Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    )

    private val tokenUtils = TokenUtils(
        algorithm = Algorithm.HMAC256("secret"),
        issuer = "test",
        audience = "test",
        expiresInMillis = 1000
    )
    private val refreshTokenUtils = RefreshTokenUtils(
        expiresInMillis = REFRESH_EXPIRES_IN
    )
    private val datasource = SQLDatasource(tokenUtils, refreshTokenUtils)

    @Before
    fun setup() {
        transaction {
            SchemaUtils.create(UserTable, RefreshTokenTable)
        }
    }

    @After
    fun tearDown() {
        transaction {
            SchemaUtils.drop(UserTable, RefreshTokenTable)
        }
    }

    @Test
    fun testCreateUser() = runTest {
        val email = "test@test.com"
        val password = "test"
        val createResult = datasource.createUser(
            email = email,
            password = password,
        ) as CreateResult.Success<User>
        assertEquals(email, createResult.data.email)
        assertNotEquals(password, createResult.data.passwordHash)
    }

    @Test
    fun testCreateUser_AlreadyExisting() = runTest {
        val email = "test@test.com"
        val password = "test"
        val createResult = datasource.createUser(
            email = email,
            password = password,
        ) as CreateResult.Success<User>
        assertEquals(email, createResult.data.email)
        assertNotEquals(password, createResult.data.passwordHash)

        val secondResult = datasource.createUser(
            email = email,
            password = password,
        )
        assertTrue(secondResult is CreateResult.UniqueViolation)
    }

    @Test
    fun testGetUserById() = runTest {
        val email = "test@test.com"
        val password = "test"
        val createResult = datasource.createUser(
            email = email,
            password = password,
        ) as CreateResult.Success<User>
        val getResult = datasource.getUserById(createResult.data.id) as GetResult.Success<User>
        assertEquals(email, getResult.data.email)
        assertNotEquals(password, getResult.data.passwordHash)
    }

    @Test
    fun testGetUserById_NonExistentUser() = runTest {
        val getResult = datasource.getUserById(UUID.randomUUID())
        assertTrue { getResult is GetResult.NotFound }
    }

    @Test
    fun testGetUserByEmail() = runTest {
        val email = "test@test.com"
        val password = "test"
        val createResult = datasource.createUser(
            email = email,
            password = password,
        ) as CreateResult.Success<User>
        val getResult = datasource.getUserByEmail(createResult.data.email) as GetResult.Success<User>
        assertEquals(email, getResult.data.email)
        assertNotEquals(password, getResult.data.passwordHash)
    }

    @Test
    fun testGetUserByEmail_NonExistentUser() = runTest {
        val getResult = datasource.getUserByEmail("test@test.com")
        assertTrue { getResult is GetResult.NotFound }
    }

    @Test
    fun testLoginUser() = runTest {
        val email = "test@test.com"
        val password = "test"
        datasource.createUser(
            email = email,
            password = password,
        ) as CreateResult.Success<User>
        val loginResult = datasource.login(
            email = email,
            password = password,
        )
        assertTrue { loginResult is LoginResult.Success }
    }

    @Test
    fun testLoginUser_NonExistentUser() = runTest {
        val loginResult = datasource.login(
            email = "test2@test.com",
            password = "test",
        )
        assertTrue { loginResult is LoginResult.NotFound }
    }

    @Test
    fun testLoginUser_BadPassword() = runTest {
        val email = "test@test.com"
        val password = "test"
        datasource.createUser(
            email = email,
            password = password,
        ) as CreateResult.Success<User>
        val loginResult = datasource.login(
            email = email,
            password = "notpassword",
        )
        assertTrue { loginResult is LoginResult.InvalidPassword }
    }

    @Test
    fun testRefreshToken_NonExistentUser() = runTest {
       val result = datasource.refreshToken(UUID.randomUUID(), "")
        assertEquals(RefreshResult.Invalid, result)
    }

    @Test
    fun testRefreshToken_BadToken() = runTest {
        datasource.createUser("email", "password") as CreateResult.Success<User>
        val loginResult = datasource.login("email", "password") as LoginResult.Success
        val result = datasource.refreshToken(loginResult.user.id, "")
        assertEquals(RefreshResult.Invalid, result)
    }

    @Test
    fun testRefreshToken_ExpiredToken() = runTest {
        datasource.createUser("email", "password") as CreateResult.Success<User>
        val loginResult = datasource.login("email", "password") as LoginResult.Success
        sleep(REFRESH_EXPIRES_IN)
        val result = datasource.refreshToken(loginResult.user.id, loginResult.refreshToken.refreshToken)
        assertEquals(RefreshResult.Invalid, result)
    }

    @Test
    fun testRefreshToken_GoodToken() = runTest {
        datasource.createUser("email", "password") as CreateResult.Success<User>
        val loginResult = datasource.login("email", "password") as LoginResult.Success
        val result = datasource.refreshToken(loginResult.user.id, loginResult.refreshToken.refreshToken)
        assertTrue(result is RefreshResult.Success)
    }

    @Test
    fun testRefreshToken_TokenRecycled() = runTest {
        datasource.createUser("email", "password") as CreateResult.Success<User>
        val loginResult = datasource.login("email", "password") as LoginResult.Success
        val result = datasource.refreshToken(loginResult.user.id, loginResult.refreshToken.refreshToken)
        assertTrue(result is RefreshResult.Success)
        val secondResult = datasource.refreshToken(loginResult.user.id, loginResult.refreshToken.refreshToken)
        assertEquals(RefreshResult.Invalid, secondResult)
    }

}