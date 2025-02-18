package com.somekoder.block.auth.api.server.di

import com.auth0.jwt.algorithms.Algorithm
import com.somekoder.block.auth.api.infra.RefreshTokenUtils
import com.somekoder.block.auth.api.infra.TokenUtils
import com.somekoder.block.auth.api.data.sql.SQLDatasource
import com.somekoder.block.auth.api.data.sql.table.RefreshTokenTable
import com.somekoder.block.auth.api.data.sql.table.UserTable
import com.somekoder.block.auth.api.domain.datasource.IDatasource
import com.somekoder.block.auth.api.domain.usecase.*
import com.somekoder.block.auth.api.domain.util.EmailValidator
import com.somekoder.block.auth.api.domain.util.PasswordValidator
import com.somekoder.block.auth.api.server.util.DefaultConfig
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.koin.dsl.module

fun Application.applicationModule() = module {

    single { provideDatabase(get()) }

    single { provideTokenUtils(get()) }
    single { provideRefreshTokenUtils(get()) }
    single { provideDatasource(get(), get()) }
    single { provideEmailValidator(get()) }
    single { providePasswordValidator(get()) }

    single { DefaultConfig(environment.config) }

    // Use cases
    factory { CreateUserUseCase(get(), get(), get()) }
    factory { GetUserByEmailUseCase(get()) }
    factory { GetUserByIdUseCase(get()) }
    factory { LoginUseCase(get()) }
    factory { RefreshUseCase(get()) }
}

private fun provideTokenUtils(
    config: DefaultConfig,
) : TokenUtils {
    return TokenUtils(
        algorithm = Algorithm.HMAC256(config.jwtSecret),
        issuer = config.jwtIssuer,
        audience = config.jwtAudience,
        expiresInMillis = config.jwtExpiresIn
    )
}

private fun provideRefreshTokenUtils(
    config: DefaultConfig,
) : RefreshTokenUtils {
    return RefreshTokenUtils(expiresInMillis = config.refreshExpiresIn)
}

private fun provideEmailValidator(
    config: DefaultConfig,
) : EmailValidator {
    return EmailValidator(regexPattern = config.emailRegexPattern)
}

private fun providePasswordValidator(
    config: DefaultConfig,
) : PasswordValidator {
    return PasswordValidator(
        minLength = config.passwordMinLength,
        maxLength = config.passwordMaxLength,
        requireSpecial = config.passwordRequireSpecial,
        requireUppercase = config.passwordRequireUppercase,
        requireLowercase = config.passwordRequireLowercase,
        requireNumber = config.passwordRequireNumber,
        allowedChars = config.passwordAllowedChars,
        allowedSpecialChars = config.passwordAllowedSpecialChars
    )
}

private fun provideDatasource(
    tokenUtils: TokenUtils,
    refreshTokenUtils: RefreshTokenUtils
) : IDatasource {
    return SQLDatasource(
        tokenUtils = tokenUtils,
        refreshTokenUtils = refreshTokenUtils
    )
}

private fun provideDatabase(
    config: DefaultConfig,
) : Database {

    return Database.connect(
        url = "jdbc:postgresql://${config.databaseUrl}/${config.databaseName}",
        user = config.databaseUsername,
        driver = "org.postgresql.Driver",
        password = config.databasePassword,
    ).also {
        transaction {
            SchemaUtils.create(UserTable, RefreshTokenTable)
        }
    }
}