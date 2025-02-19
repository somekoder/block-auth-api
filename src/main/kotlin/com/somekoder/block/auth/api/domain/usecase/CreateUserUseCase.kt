package com.somekoder.block.auth.api.domain.usecase

import com.somekoder.block.auth.api.domain.datasource.IDatasource
import com.somekoder.block.auth.api.domain.datasource.result.CreateResult
import com.somekoder.block.auth.api.domain.model.EmailError
import com.somekoder.block.auth.api.domain.model.PasswordError
import com.somekoder.block.auth.api.domain.model.User
import com.somekoder.block.auth.api.domain.util.EmailValidator
import com.somekoder.block.auth.api.domain.util.PasswordValidator

class CreateUserUseCase(
    private val datasource: IDatasource,
    private val passwordValidator: PasswordValidator,
    private val emailValidator: EmailValidator
) {

    suspend operator fun invoke(email: String, password: String): Result {
        val passwordErrors = passwordValidator.invoke(password)
        val emailErrors = emailValidator.invoke(email)

        if (passwordErrors.isNotEmpty() || emailErrors.isNotEmpty()) {
            return Result.ValidationError(emailErrors, passwordErrors)
        }

        return when (val result = datasource.createUser(email, password)) {
            is CreateResult.Failure -> Result.Failure(result.message)
            is CreateResult.Success -> Result.Success(result.data)
            is CreateResult.UniqueViolation -> Result.UniqueViolation
        }
    }

    sealed class Result {
        data class Success(val user: User) : Result()
        data class Failure(val message: String) : Result()
        data object UniqueViolation : Result()
        data class ValidationError(
            val emailErrors: List<EmailError>,
            val passwordErrors: List<PasswordError>
        ) : Result()
    }
}