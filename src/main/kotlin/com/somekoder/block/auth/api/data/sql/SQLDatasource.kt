package com.somekoder.block.auth.api.data.sql

import com.somekoder.block.auth.api.data.sql.table.UserTable
import com.somekoder.block.auth.api.infra.Hashing
import com.somekoder.block.auth.api.infra.RefreshTokenUtils
import com.somekoder.block.auth.api.infra.TokenUtils
import com.somekoder.block.auth.api.data.sql.table.RefreshTokenTable
import com.somekoder.block.auth.api.data.sql.util.transaction
import com.somekoder.block.auth.api.domain.datasource.IDatasource
import com.somekoder.block.auth.api.domain.datasource.result.CreateResult
import com.somekoder.block.auth.api.domain.datasource.result.GetResult
import com.somekoder.block.auth.api.domain.datasource.result.LoginResult
import com.somekoder.block.auth.api.domain.datasource.result.RefreshResult
import com.somekoder.block.auth.api.domain.model.User
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import java.util.*

class SQLDatasource(
    private val tokenUtils: TokenUtils,
    private val refreshTokenUtils: RefreshTokenUtils,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
) : IDatasource {

    override suspend fun getUserByEmail(email: String): GetResult<User> = transaction(
        dispatcher = dispatcher,
        onUnhandledException = { GetResult.Failure(it.message ?: it.toString()) },
        body = {
            UserTable
                .selectAll()
                .where(UserTable.email eq email)
                .singleOrNull()?.let {
                    val user = User(
                        id = it[UserTable.id].value,
                        email = it[UserTable.email],
                        passwordHash = it[UserTable.passwordHash]
                    )
                    GetResult.Success(user)
                } ?: GetResult.NotFound
        }
    )

    override suspend fun getUserById(id: UUID): GetResult<User> = transaction(
        dispatcher = dispatcher,
        onUnhandledException = { GetResult.Failure(it.message ?: it.toString()) },
        body = {
         UserTable
            .selectAll()
            .where(UserTable.id eq id)
            .singleOrNull()?.let {
                val user = User(
                    id = it[UserTable.id].value,
                    email = it[UserTable.email],
                    passwordHash = it[UserTable.passwordHash]
                )
                GetResult.Success(user)
            } ?: GetResult.NotFound
        }
    )

    override suspend fun createUser(email: String, password: String): CreateResult<User> = transaction(
        dispatcher = dispatcher,
        onUnhandledException = { CreateResult.Failure(it.toString()) },
        body = {
            // Check if user email exists
            UserTable.selectAll().where { UserTable.email eq email }.singleOrNull()?.let {
                return@transaction CreateResult.UniqueViolation
            }

            // Hash password
            val passwordHash = Hashing.hash(password)

            // Insert user
            val row = UserTable.insert {
                it[UserTable.email] = email
                it[UserTable.passwordHash] = passwordHash
            }.resultedValues?.firstOrNull()

            if (row == null) {
                return@transaction CreateResult.Failure("Inserted row was null")
            }

            val user = User(
                id = row[UserTable.id].value,
                email = row[UserTable.email],
                passwordHash = row[UserTable.passwordHash]
            )
            CreateResult.Success(user)
        }
    )

    override suspend fun login(email: String, password: String): LoginResult = transaction(
        dispatcher = dispatcher,
        onUnhandledException = { LoginResult.Failure(it.toString()) },
        body = {
            // Find user
            UserTable.selectAll().where { UserTable.email eq email }.singleOrNull()?.let { userRow ->
                val userId = userRow[UserTable.id].value
                val passwordHash = userRow[UserTable.passwordHash]

                // Verify password
                val valid = Hashing.verify(password, passwordHash)
                if (!valid) {
                    return@transaction LoginResult.InvalidPassword
                }

                val user = User(email = email, id = userId, passwordHash = passwordHash)

                // Generate tokens
                val token = tokenUtils.generateToken(userId, email)
                val refreshToken = refreshTokenUtils.generateToken()

                // Insert refresh token
                RefreshTokenTable.insert { refreshTokenInsert ->
                    refreshTokenInsert[RefreshTokenTable.userId] = user.id
                    refreshTokenInsert[expiresAt] = refreshToken.expiresAt
                    refreshTokenInsert[tokenHash] = refreshToken.refreshTokenHash
                }

                return@transaction LoginResult.Success(user, token, refreshToken)
            } ?: LoginResult.NotFound
        }
    )

    override suspend fun refreshToken(userId: UUID, refreshToken: String): RefreshResult = transaction(
        dispatcher = dispatcher,
        onUnhandledException = { RefreshResult.Failure(it.toString()) },
        body = {
            val user = UserTable
                .selectAll()
                .where { UserTable.id eq userId }
                .singleOrNull()

            if (user == null) {
                return@transaction RefreshResult.Invalid
            }

            val userTokens = RefreshTokenTable
                .selectAll()
                .where(RefreshTokenTable.userId eq userId)
                .toList()

            val foundToken = userTokens.find {
                refreshTokenUtils.verifyTokenWithExpiration(
                    refreshToken,
                    it[RefreshTokenTable.tokenHash],
                    it[RefreshTokenTable.expiresAt]
                )
            }
            if (foundToken == null) {
                return@transaction RefreshResult.Invalid
            }

            // Generate new tokens
            val email = user[UserTable.email]
            val token = tokenUtils.generateToken(userId, email)
            val newRefreshToken = refreshTokenUtils.generateToken()

            // Insert refresh token
            RefreshTokenTable.insert { refreshTokenInsert ->
                refreshTokenInsert[RefreshTokenTable.userId] = userId
                refreshTokenInsert[expiresAt] = newRefreshToken.expiresAt
                refreshTokenInsert[tokenHash] = newRefreshToken.refreshTokenHash
            }

            // Recycle old refresh token
            RefreshTokenTable.deleteWhere { id eq foundToken[id] }

            RefreshResult.Success(
                token = token,
                refreshToken = newRefreshToken,
            )
        }
    )
}