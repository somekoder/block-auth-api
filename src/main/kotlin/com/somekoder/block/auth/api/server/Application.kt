package com.somekoder.block.auth.api.server

import com.somekoder.block.auth.api.server.di.configureDI
import com.somekoder.block.auth.api.domain.datasource.result.GetResult
import com.somekoder.block.auth.api.domain.datasource.result.LoginResult
import com.somekoder.block.auth.api.domain.datasource.result.RefreshResult
import com.somekoder.block.auth.api.domain.model.User
import com.somekoder.block.auth.api.domain.usecase.CreateUserUseCase
import com.somekoder.block.auth.api.domain.usecase.GetUserByIdUseCase
import com.somekoder.block.auth.api.domain.usecase.LoginUseCase
import com.somekoder.block.auth.api.domain.usecase.RefreshUseCase
import com.somekoder.block.auth.api.infra.logging.Logger
import com.somekoder.block.auth.api.server.request.CreateUserRequest
import com.somekoder.block.auth.api.server.request.LoginUserRequest
import com.somekoder.block.auth.api.server.request.RefreshRequest
import com.somekoder.block.auth.api.server.response.error.CreateUserValidationError
import com.somekoder.block.auth.api.server.plugins.*
import com.somekoder.block.auth.api.server.response.CreateUserResponse
import com.somekoder.block.auth.api.server.response.GetUserResponse
import com.somekoder.block.auth.api.server.response.LoginUserResponse
import com.somekoder.block.auth.api.server.response.RefreshResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.netty.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database
import org.koin.ktor.ext.inject
import java.util.*

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() {
    configureDI()
    configureDefaultHeaders()
    configureContentNegotiation()
    configureAuthentication()
    configureRouting()

    // Init database
    val database by inject<Database>()
    val logger by inject<Logger>()
    logger.info("Connecting to database: ${database.url}")
}

private fun Application.configureRouting() {

    val loginUseCase by inject<LoginUseCase>()
    val createUserUseCase by inject<CreateUserUseCase>()
    val getUserByIdUseCase by inject<GetUserByIdUseCase>()
    val refreshUseCase by inject<RefreshUseCase>()

    routing {
        post("/login") {
            val request = call.receive<LoginUserRequest>()
            when (val result = loginUseCase.invoke(request.email, request.password)) {
                is LoginResult.Failure -> call.respond(HttpStatusCode.InternalServerError)
                is LoginResult.InvalidPassword -> call.respond(HttpStatusCode.Unauthorized)
                is LoginResult.NotFound -> call.respond(HttpStatusCode.Unauthorized)
                is LoginResult.Success -> {
                    val response = LoginUserResponse.from(result.user, result.token, result.refreshToken)
                    call.respond(HttpStatusCode.OK, response)
                }
            }
        }
        post("/create") {
            val request = call.receive<CreateUserRequest>()
            when (val result = createUserUseCase.invoke(request.email, request.password)) {
                is CreateUserUseCase.Result.Failure -> call.respond(HttpStatusCode.InternalServerError)
                is CreateUserUseCase.Result.UniqueViolation -> call.respond(HttpStatusCode.Conflict)
                is CreateUserUseCase.Result.ValidationError -> {
                    val json = CreateUserValidationError.create(result.passwordErrors, result.emailErrors)
                    call.respond(HttpStatusCode.BadRequest, json)
                }
                is CreateUserUseCase.Result.Success -> {
                    val response = CreateUserResponse(email = result.user.email, userId = result.user.id.toString())
                    call.respond(HttpStatusCode.Created, response)
                }
            }
        }
        authenticate("default") {
            get("/user/{id}") {
                val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                when (val result = getUserByIdUseCase.invoke(UUID.fromString(id))) {
                    is GetResult.Failure -> call.respond(HttpStatusCode.InternalServerError)
                    is GetResult.NotFound -> call.respond(HttpStatusCode.NotFound)
                    is GetResult.Success<User> -> {
                        val response = GetUserResponse(email = result.data.email, userId = result.data.id.toString())
                        call.respond(HttpStatusCode.OK, response)
                    }
                }
            }
        }
        authenticate("allow-expired") {
            post("/refresh") {
                val request = call.receive<RefreshRequest>()
                val user = call.principal<UserPrincipal>() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                when (val result = refreshUseCase.invoke(user.id, request.refreshToken)) {
                    is RefreshResult.Failure -> call.respond(HttpStatusCode.BadRequest)
                    is RefreshResult.Invalid -> call.respond(HttpStatusCode.Unauthorized)
                    is RefreshResult.Success -> {
                        val response = RefreshResponse(
                            token = result.token.token,
                            refreshToken = result.refreshToken.refreshToken,
                            tokenExpiresAt = result.token.expiresAt,
                            refreshTokenExpiresAt = result.refreshToken.expiresAt,
                        )
                        call.respond(HttpStatusCode.OK, response)
                    }
                }
            }
        }
    }
}
