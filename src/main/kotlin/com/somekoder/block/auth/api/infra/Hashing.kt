package com.somekoder.block.auth.api.infra

import org.mindrot.jbcrypt.BCrypt

object Hashing {

    const val MAX_BYTES = 50

    fun hash(value: String) : String {
        if (value.toByteArray().size > MAX_BYTES) {
            throw IllegalArgumentException("Can't hash larger than $MAX_BYTES bytes")
        }
        return BCrypt.hashpw(value, BCrypt.gensalt())
    }

    fun verify(value: String, hash: String) : Boolean {
        return BCrypt.checkpw(value, hash)
    }
}