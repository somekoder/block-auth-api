package com.somekoder.block.auth.api.data.sql.table

import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.ReferenceOption

object RefreshTokenTable : UUIDTable("RefreshTokens", "id") {
    val tokenHash = varchar("hashedToken", length = 500)
    val expiresAt = long("expiresAt")
    val createdAt = long("createdAt")
    val revoked = bool("revoked").default(false)
    val userId = optReference(
        name = "userId",
        refColumn = UserTable.id,
        onDelete = ReferenceOption.CASCADE
    )
}