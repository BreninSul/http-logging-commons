package io.github.breninsul.logging2

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

open class HttpRegexJsonBodyMaskingTest {
    @Test
    fun repeat1000() {
        repeat(1000) {
            `test mask with field present`()
        }
    }

    /**
     * Tests for `HttpRegexJsonBodyMasking` class.
     *
     * `HttpRegexJsonBodyMasking` is a class that masks sensitive fields in
     * JSON strings using regular expressions. It takes a collection of fields
     * whose values should be replaced with a `<MASKED>` placeholder in the
     * given message.
     *
     * The `mask` method transforms the input JSON string by masking specified
     * fields.
     */
    @Test
    fun `test no mask with field present`() {
        val fields = listOf("password", "token", "123213123", "asdsad2123")
        val masking = getMasker(fields)
        val json = """{"username":"john_doe","password1":"secret123","token1":"abc12345"}""".repeat(1000000)
        println("Length ${json.length / (1024 * 1024)} mb")
        val time = System.currentTimeMillis()
        val result = masking.mask(json)
        assertEquals(json, result)
        println("Time took: ${System.currentTimeMillis() - time} ms")
    }

    @Test
    fun `test mask once  with field present`() {
        val fields = listOf("password", "token")
        val masking = getMasker(fields)

        val repeated = """{"username":"john_doe","password1":"secret123","token1":"abc12345"}""".repeat(1000000)
        val json =
            repeated + """{"username":"john_doe","password":"secret123","token":"abc12345"}""" + """{"password": ["saddsa","asdsd[edfasd\"esf]"],"it2": ["other"]}""" + """{"password": {"1":["saddsa","as[]{}dsd[edfasd\"esf]"],"it2": ["other"]},"2":{}}"""
        val expected = repeated + """{"username":"john_doe","password":"<MASKED>","token":"<MASKED>"}""" + """{"password": [<MASKED>],"it2": ["other"]}""" + """{"password": {<MASKED>},"2":{}}"""
        println("Length ${json.length / (1024 * 1024)} mb")
        val time = System.currentTimeMillis()
        val result = masking.mask(json)
        assertEquals(expected, result)
        println("Time took: ${System.currentTimeMillis() - time} ms")
    }

    @Test
    fun `test mask with field present`() {
        val time = System.currentTimeMillis()
        val fields = listOf("password", "token", "length", "secret", "length1", "object", "email")
        val masking = getMasker(fields)

        val json = """{"object" :{"email":"secret@gmail.com"},"username":"john_doe","length1" : 123123,"secret":"sec","password":"secret123","token":"abc12345","length" : 123123 }"""
        val expected = """{"object" :{<MASKED>},"username":"john_doe","length1" : <MASKED>,"secret":"<MASKED>","password":"<MASKED>","token":"<MASKED>","length" : <MASKED> }"""

        val result = masking.mask(json)
        assertEquals(expected, result)
        println("Time took: ${System.currentTimeMillis() - time} ms")
    }

    @Test
    fun `test mask with field absent`() {
        val fields = listOf("password", "token")
        val masking = getMasker(fields)

        val json = """{"username":"john_doe","email":"john@example.com"}"""
        val expected = json

        val result = masking.mask(json)
        assertEquals(expected, result)
    }

    @Test
    fun `test mask with null message`() {
        val fields = listOf("password", "token")
        val masking = getMasker(fields)

        val result = masking.mask(null)
        assertEquals("", result)
    }

    @Test
    fun `test mask with empty fields`() {
        val fields = emptyList<String>()
        val masking = getMasker(fields)

        val json = """{"username":"john_doe","password":"secret123"}"""
        val expected = json

        val result = masking.mask(json)
        assertEquals(expected, result)
    }

    @Test
    fun `test mask with nested fields`() {
        val fields = listOf("password", "token")
        val masking = getMasker(fields)

        val json = """{"user":{"username":"john_doe","password":"secret123"},"token":"abc12345"}"""
        val expected = """{"user":{"username":"john_doe","password":"<MASKED>"},"token":"<MASKED>"}"""

        val result = masking.mask(json)
        assertEquals(expected, result)
    }

    @Test
    fun `test mask with special characters in field values`() {
        val fields = listOf("password", "token")
        val masking = getMasker(fields)

        val json = """{"password":"s#ec^re*t!","token":"to@ke%123"}"""
        val expected = """{"password":"<MASKED>","token":"<MASKED>"}"""

        val result = masking.mask(json)
        assertEquals(expected, result)
    }

    @Test
    fun `test mask with partially matching fields`() {
        val fields = listOf("pass", "tok")
        val masking = getMasker(fields)

        val json = """{"password":"secret123","token":"abc12345"}"""
        val expected = json

        val result = masking.mask(json)
        assertEquals(expected, result)
    }

    @Test
    fun `test mask with multiple identical fields`() {
        val fields = listOf("password")
        val masking = getMasker(fields)

        val json = """{"password":"secret123","password":"myPass456"}"""
        val expected = """{"password":"<MASKED>","password":"<MASKED>"}"""

        val result = masking.mask(json)
        assertEquals(expected, result)
    }

    @Test
    fun `test equality of HttpRegexJsonBodyMasking objects`() {
        val fields1 = listOf("password", "token")
        val fields2 = listOf("password", "token")
        val fields3 = listOf("token")

        val masking1 = getMasker(fields1)
        val masking2 = getMasker(fields2)
        val masking3 = getMasker(fields3)

        assertEquals(masking1, masking2)
        assertEquals(masking1.hashCode(), masking2.hashCode())
        assertEquals(false, masking1 == masking3)
    }

    @Test
    fun `test type method returns correct type`() {
        val fields = listOf("password", "token")
        val masking = getMasker(fields)

        val result = masking.type()
        assertEquals(JsonBodyType, result)
    }

    protected open fun getMasker(fields: List<String>) = HttpRegexJsonBodyMasking(fields)
}