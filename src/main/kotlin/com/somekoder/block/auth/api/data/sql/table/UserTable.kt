package com.somekoder.block.auth.api.data.sql.table

import org.jetbrains.exposed.dao.id.UUIDTable

object UserTable : UUIDTable("Users", "id") {
    val email = varchar("email", length = 320).uniqueIndex()
    val passwordHash = varchar("passwordHash", length = 500)
    val createdAt = long("createdAt")
}
