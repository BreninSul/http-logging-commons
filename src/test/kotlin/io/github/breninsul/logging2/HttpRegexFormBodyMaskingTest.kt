package io.github.breninsul.logging2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HttpRegexFormBodyMaskingTest {

    /**
     * This test class verifies the correct functionality of the
     * HttpRegexFormBodyMasking class. Specifically, it ensures that the `mask`
     * method masks sensitive fields in a URL-encoded form body, replacing
     * their values with a "<MASKED>" string, and that the `type` method
     * identifies the body type as FormBodyType.
     */

    @Test
    fun `mask should replace sensitive field values`() {
        val fields = listOf("password", "token")
        val masking = HttpRegexFormBodyMasking(fields)
        val input = "username=john&password=secret&token=abcd1234"
        val expected = "username=john&password=<MASKED>&token=<MASKED>"

        val result = masking.mask(input)

        assertEquals(expected, result)
    }

    @Test
    fun `mask should handle a single sensitive field`() {
        val fields = listOf("apiKey")
        val masking = HttpRegexFormBodyMasking(fields)
        val input = "apiKey=my-secret-key"
        val expected = "apiKey=<MASKED>"

        val result = masking.mask(input)

        assertEquals(expected, result)
    }

    @Test
    fun `mask should handle multiple sensitive fields in the body`() {
        val fields = listOf("email", "session")
        val masking = HttpRegexFormBodyMasking(fields)
        val input = "email=test@example.com&session=abc123&status=active"
        val expected = "email=<MASKED>&session=<MASKED>&status=active"

        val result = masking.mask(input)

        assertEquals(expected, result)
    }

    @Test
    fun `mask should ignore non-sensitive fields`() {
        val fields = listOf("token")
        val masking = HttpRegexFormBodyMasking(fields)
        val input = "username=john&status=active"
        val expected = "username=john&status=active"

        val result = masking.mask(input)

        assertEquals(expected, result)
    }

    @Test
    fun `mask should handle an empty message`() {
        val fields = listOf("password")
        val masking = HttpRegexFormBodyMasking(fields)
        val input = ""
        val expected = ""

        val result = masking.mask(input)

        assertEquals(expected, result)
    }

    @Test
    fun `mask should handle null input`() {
        val fields = listOf("password")
        val masking = HttpRegexFormBodyMasking(fields)
        val input: String? = null
        val expected = ""

        val result = masking.mask(input)

        assertEquals(expected, result)
    }

    @Test
    fun `mask should handle fields with no values`() {
        val fields = listOf("token")
        val masking = HttpRegexFormBodyMasking(fields)
        val input = "token=&status=active"
        val expected = "token=<MASKED>&status=active"

        val result = masking.mask(input)

        assertEquals(expected, result)
    }

    @Test
    fun `mask should mask only exact field matches`() {
        val fields = listOf("auth")
        val masking = HttpRegexFormBodyMasking(fields)
        val input = "authKey=value&auth=test"
        val expected = "authKey=value&auth=<MASKED>"

        val result = masking.mask(input)

        assertEquals(expected, result)
    }

    @Test
    fun `mask should preserve the original message format`() {
        val fields = listOf("password")
        val masking = HttpRegexFormBodyMasking(fields)
        val input = "username=john&password=secret&token=abcd1234"
        val expected = "username=john&password=<MASKED>&token=abcd1234"

        val result = masking.mask(input)

        assertEquals(expected, result)
    }

    @Test
    fun `type should return FormBodyType`() {
        val masking = HttpRegexFormBodyMasking(listOf("field"))

        val result = masking.type()

        assertEquals(FormBodyType, result)
    }
}