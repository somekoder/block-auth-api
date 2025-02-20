package com.somekoder.block.auth.api.domain.util

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class EmailValidatorTest {

    private val validator = EmailValidator(
        regexPattern = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}\$"
    )

    @Test
    fun testMatching() {
        val validEmails = listOf(
            "email@email.com",
            "example1234@test.com",
            "test1234+43gd@example.com",
        )
        validEmails.forEach {
            assertEquals(emptyList(),validator.invoke(it))
        }
    }

    @Test
    fun testNotMatching() {
        val validEmails = listOf(
            "emailemail.com",
            "example1234@test",
            "test1234+43gdexample.com",
        )
        validEmails.forEach {
            assertNotEquals(emptyList(),validator.invoke(it))
        }
    }
}