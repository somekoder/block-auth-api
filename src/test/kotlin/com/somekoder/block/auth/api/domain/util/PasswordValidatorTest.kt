package com.somekoder.block.auth.api.domain.util

import com.somekoder.block.auth.api.domain.model.PasswordError
import kotlin.test.*

class PasswordValidatorTest {

    private val defaultValidator = PasswordValidator(
        minLength = 1,
        maxLength = 32,
        requireNumber = false,
        requireSpecial = false,
        requireUppercase = false,
        requireLowercase = false,
        allowedChars = ('a' .. 'z').toSet(),
        allowedSpecialChars = setOf('#'),
    )

    @Test
    fun testMinLength() {
        val validator = defaultValidator.copy(minLength = 1)
        val badResult = validator("")
        val goodResult = validator("1")
        assertNotNull(badResult.find { it is PasswordError.TooShort })
        assertNull(goodResult.find { it is PasswordError.TooShort })
    }

    @Test
    fun testMaxLength() {
        val validator = defaultValidator.copy(maxLength = 1)
        val badResult = validator("12")
        val goodResult = validator("1")
        assertNotNull(badResult.find { it is PasswordError.TooLong })
        assertNull(goodResult.find { it is PasswordError.TooLong })
    }

    @Test
    fun testRequireNumberTrue() {
        val validator = defaultValidator.copy(requireNumber = true)
        val goodResult = validator("1")
        val badResult = validator("a")
        assertNull(goodResult.find { it is PasswordError.MissingNumber })
        assertNotNull(badResult.find { it is PasswordError.MissingNumber })
    }

    @Test
    fun testRequireNumberFalse() {
        val validator = defaultValidator.copy(requireNumber = false)
        val result1 = validator("a")
        val result2 = validator("1")
        assertNull(result1.find { it is PasswordError.MissingNumber })
        assertNull(result2.find { it is PasswordError.MissingNumber })
    }

    @Test
    fun testRequireSpecialCharTrue() {
        val validator = defaultValidator.copy(requireSpecial = true)
        val goodResult = validator("#")
        val badResult = validator("1")
        assertNull(goodResult.find { it is PasswordError.MissingSpecialChar })
        assertNotNull(badResult.find { it is PasswordError.MissingSpecialChar })
    }

    @Test
    fun testRequireSpecialCharFalse() {
        val validator = defaultValidator.copy(requireSpecial = false)
        val result = validator("1")
        assertNull(result.find { it is PasswordError.MissingSpecialChar })
    }

    @Test
    fun testRequireUppercaseTrue() {
        val validator = defaultValidator.copy(requireUppercase = true)
        val goodResult = validator("A")
        val badResult = validator("a")
        assertNull(goodResult.find { it is PasswordError.MissingUppercase })
        assertNotNull(badResult.find { it is PasswordError.MissingUppercase })
    }

    @Test
    fun testRequireUppercaseFalse() {
        val validator = defaultValidator.copy(requireUppercase = false)
        val result = validator("a")
        assertNull(result.find { it is PasswordError.MissingUppercase })
    }

    @Test
    fun testRequireLowercaseTrue() {
        val validator = defaultValidator.copy(requireLowercase = true)
        val goodResult = validator("a")
        val badResult = validator("A")
        assertNull(goodResult.find { it is PasswordError.MissingLowercase })
        assertNotNull(badResult.find { it is PasswordError.MissingLowercase })
    }

    @Test
    fun testRequireLowercaseFalse() {
        val validator = defaultValidator.copy(requireLowercase = false)
        val result = validator("A")
        assertNull(result.find { it is PasswordError.MissingLowercase })
    }

    @Test
    fun testAllowedChars() {
        val validator = defaultValidator.copy(allowedChars = ('a'..'z').toSet())
        val goodResult = validator("az")
        val badResult = validator("AZ")
        assertNull(goodResult.find { it is PasswordError.IllegalChar })
        assertEquals(listOf(PasswordError.IllegalChar(listOf('A', 'Z'))), badResult)
    }
}