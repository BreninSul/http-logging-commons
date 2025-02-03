package io.github.breninsul.logging2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HttpRegexUriMaskingTest {

    @Test
    fun `test mask with single field present in URI`() {
        val masking = HttpRegexUriMasking(listOf("password"))
        val uri = "http://example.com?username=user&password=1234&token=abcd"
        val result = masking.mask(uri)
        assertEquals("http://example.com?username=user&password=<MASKED>&token=abcd", result)
    }

    @Test
    fun `test mask with multiple fields present in URI`() {
        val masking = HttpRegexUriMasking(listOf("password", "token"))
        val uri = "http://example.com?username=user&password=1234&token=abcd"
        val result = masking.mask(uri)
        assertEquals("http://example.com?username=user&password=<MASKED>&token=<MASKED>", result)
    }

    @Test
    fun `test mask with no fields present in URI`() {
        val masking = HttpRegexUriMasking(listOf("creditcard"))
        val uri = "http://example.com?username=user&password=1234&token=abcd"
        val result = masking.mask(uri)
        assertEquals("http://example.com?username=user&password=1234&token=abcd", result)
    }

    @Test
    fun `test mask with URI having no query parameters`() {
        val masking = HttpRegexUriMasking(listOf("password"))
        val uri = "http://example.com"
        val result = masking.mask(uri)
        assertEquals("http://example.com", result)
    }

    @Test
    fun `test mask with URI being null`() {
        val masking = HttpRegexUriMasking(listOf("password"))
        val uri: String? = null
        val result = masking.mask(uri)
        assertEquals("", result)
    }

    @Test
    fun `test mask with overlapping regex ranges`() {
        val masking = HttpRegexUriMasking(listOf("key"))
        val uri = "http://example.com?key=value123&key=value456"
        val result = masking.mask(uri)
        assertEquals("http://example.com?key=<MASKED>&key=<MASKED>", result)
    }

    @Test
    fun `test mask with regex match at the end of URI`() {
        val masking = HttpRegexUriMasking(listOf("sessionId"))
        val uri = "http://example.com?user=john&sessionId=abc123"
        val result = masking.mask(uri)
        assertEquals("http://example.com?user=john&sessionId=<MASKED>", result)
    }

    @Test
    fun `test mask with multiple fields where one matches`() {
        val masking = HttpRegexUriMasking(listOf("password", "token"))
        val uri = "http://example.com?username=user&password=1234"
        val result = masking.mask(uri)
        assertEquals("http://example.com?username=user&password=<MASKED>", result)
    }

    @Test
    fun `test mask with empty URI`() {
        val masking = HttpRegexUriMasking(listOf("password"))
        val uri = ""
        val result = masking.mask(uri)
        assertEquals("", result)
    }

    @Test
    fun `test mask with case-sensitive field mismatch`() {
        val masking = HttpRegexUriMasking(listOf("Password"))
        val uri = "http://example.com?username=user&password=1234"
        val result = masking.mask(uri)
        assertEquals("http://example.com?username=user&password=1234", result)
    }

    @Test
    fun `test mask with partially matched field`() {
        val masking = HttpRegexUriMasking(listOf("pass"))
        val uri = "http://example.com?password=1234"
        val result = masking.mask(uri)
        assertEquals("http://example.com?password=1234", result)
    }
}