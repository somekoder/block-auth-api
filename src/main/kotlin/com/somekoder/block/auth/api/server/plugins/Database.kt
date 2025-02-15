package com.somekoder.block.auth.api.server.plugins

import com.somekoder.block.auth.api.data.sql.table.RefreshTokenTable
import com.somekoder.block.auth.api.data.sql.table.UserTable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    Database.connect(
        url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
        user = "root",
        driver = "org.h2.Driver",
        password = "",
    ).also {
        transaction {
            SchemaUtils.create(UserTable, RefreshTokenTable)
        }
    }
}