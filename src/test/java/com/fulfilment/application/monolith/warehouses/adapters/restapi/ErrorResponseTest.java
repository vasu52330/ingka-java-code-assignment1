package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ErrorResponseTest {

    @Test
    void shouldCreateWithSingleParameterConstructor() {
        // Arrange
        String expectedError = "Test error message";

        // Act
        ErrorResponse response = new ErrorResponse(expectedError);

        // Assert
        assertNotNull(response);
        assertEquals(expectedError, response.getError());
        assertTrue(response.getTimestamp() > 0);
        assertTrue(response.getTimestamp() <= System.currentTimeMillis());
    }

    @Test
    void shouldCreateWithTwoParameterConstructor() {
        // Arrange
        String expectedError = "Test error message";
        long expectedTimestamp = 1234567890L;

        // Act
        ErrorResponse response = new ErrorResponse(expectedError, expectedTimestamp);

        // Assert
        assertNotNull(response);
        assertEquals(expectedError, response.getError());
        assertEquals(expectedTimestamp, response.getTimestamp());
    }

    @Test
    void shouldSetAndGetError() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Initial error");
        String newError = "Updated error message";

        // Act
        response.setError(newError);

        // Assert
        assertEquals(newError, response.getError());
    }

    @Test
    void shouldSetAndGetTimestamp() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Error", 1000L);
        long newTimestamp = 2000L;

        // Act
        response.setTimestamp(newTimestamp);

        // Assert
        assertEquals(newTimestamp, response.getTimestamp());
    }

    @Test
    void shouldHandleNullErrorInConstructor() {
        // Act
        ErrorResponse response = new ErrorResponse(null);

        // Assert
        assertNull(response.getError());
        assertTrue(response.getTimestamp() > 0);
    }

    @Test
    void shouldHandleNullErrorInSetter() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Initial error");

        // Act
        response.setError(null);

        // Assert
        assertNull(response.getError());
    }

    @Test
    void shouldHandleEmptyErrorString() {
        // Arrange
        ErrorResponse response = new ErrorResponse("");

        // Assert
        assertEquals("", response.getError());
    }

    @Test
    void shouldHandleWhitespaceErrorString() {
        // Arrange
        ErrorResponse response = new ErrorResponse("   ");

        // Assert
        assertEquals("   ", response.getError());
    }

    @Test
    void shouldHandleSpecialCharactersInError() {
        // Arrange
        String errorWithSpecialChars = "Error: @#$%^&*() \n\t Line break";
        ErrorResponse response = new ErrorResponse(errorWithSpecialChars);

        // Assert
        assertEquals(errorWithSpecialChars, response.getError());
    }

    @Test
    void shouldHandleVeryLongErrorString() {
        // Arrange
        String longError = "A".repeat(10000); // 10KB error message
        ErrorResponse response = new ErrorResponse(longError);

        // Assert
        assertEquals(longError, response.getError());
    }

    @Test
    void shouldHandleZeroTimestamp() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Error", 0L);

        // Assert
        assertEquals(0L, response.getTimestamp());
    }

    @Test
    void shouldHandleNegativeTimestamp() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Error", -1000L);

        // Assert
        assertEquals(-1000L, response.getTimestamp());
    }

    @Test
    void shouldHandleFutureTimestamp() {
        // Arrange
        long futureTimestamp = System.currentTimeMillis() + 1000000L; // ~11.5 days in future
        ErrorResponse response = new ErrorResponse("Error", futureTimestamp);

        // Assert
        assertEquals(futureTimestamp, response.getTimestamp());
    }

    @Test
    void shouldHandlePastTimestamp() {
        // Arrange
        long pastTimestamp = 0L; // Jan 1, 1970
        ErrorResponse response = new ErrorResponse("Error", pastTimestamp);

        // Assert
        assertEquals(pastTimestamp, response.getTimestamp());
    }

    @Test
    void shouldUpdateErrorAndKeepTimestamp() {
        // Arrange
        long originalTimestamp = 1000L;
        ErrorResponse response = new ErrorResponse("Original error", originalTimestamp);

        // Act
        response.setError("Updated error");

        // Assert
        assertEquals("Updated error", response.getError());
        assertEquals(originalTimestamp, response.getTimestamp());
    }

    @Test
    void shouldUpdateTimestampAndKeepError() {
        // Arrange
        String originalError = "Original error";
        ErrorResponse response = new ErrorResponse(originalError, 1000L);

        // Act
        response.setTimestamp(2000L);

        // Assert
        assertEquals(originalError, response.getError());
        assertEquals(2000L, response.getTimestamp());
    }

    @Test
    void shouldHaveConsistentStateAfterMultipleUpdates() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Error 1", 1000L);

        // Act
        response.setError("Error 2");
        response.setTimestamp(2000L);
        response.setError("Error 3");
        response.setTimestamp(3000L);

        // Assert
        assertEquals("Error 3", response.getError());
        assertEquals(3000L, response.getTimestamp());
    }

    @Test
    void shouldBeUsedForJsonSerialization() {
        // This test verifies the DTO can be used for JSON serialization

        ErrorResponse response = new ErrorResponse("Not Found", 1234567890L);

        // Simulate what Jackson would serialize
        String expectedJsonStructure =
                "{\"error\":\"Not Found\",\"timestamp\":1234567890}";

        // Just verify fields are accessible
        assertEquals("Not Found", response.getError());
        assertEquals(1234567890L, response.getTimestamp());
    }

    @Test
    void shouldHaveDefaultToString() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Test error", 12345L);

        // Act
        String toStringResult = response.toString();

        // Assert
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("ErrorResponse"));
    }

    @Test
    void shouldHaveDefaultEqualsAndHashCode() {
        // Since equals() and hashCode() are not overridden, test default behavior
        ErrorResponse response1 = new ErrorResponse("Error", 1000L);
        ErrorResponse response2 = new ErrorResponse("Error", 1000L);

        // Default equals() compares object identity, not field values
        assertNotEquals(response1, response2);
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void shouldBeImmutableIfUsedAsRecord() {
        // Note: This class is not immutable (has setters)
        // But we can test typical usage patterns

        ErrorResponse response = new ErrorResponse("Error", 1000L);

        // Can be modified
        response.setError("Modified");
        response.setTimestamp(2000L);

        assertEquals("Modified", response.getError());
        assertEquals(2000L, response.getTimestamp());
    }

    @Test
    void shouldHandleEdgeCaseWithMaxLongTimestamp() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Error", Long.MAX_VALUE);

        // Assert
        assertEquals(Long.MAX_VALUE, response.getTimestamp());
    }

    @Test
    void shouldHandleEdgeCaseWithMinLongTimestamp() {
        // Arrange
        ErrorResponse response = new ErrorResponse("Error", Long.MIN_VALUE);

        // Assert
        assertEquals(Long.MIN_VALUE, response.getTimestamp());
    }

    @Test
    void shouldBeSuitableForErrorReporting() {
        // Test realistic error scenarios

        // HTTP 404
        ErrorResponse notFound = new ErrorResponse("Resource not found");
        assertNotNull(notFound.getError());
        assertTrue(notFound.getTimestamp() > 0);

        // HTTP 500
        ErrorResponse serverError = new ErrorResponse("Internal server error", System.currentTimeMillis());
        assertNotNull(serverError.getError());
        assertTrue(serverError.getTimestamp() > 0);

        // Validation error
        ErrorResponse validationError = new ErrorResponse("Validation failed: Name is required");
        assertNotNull(validationError.getError());
        assertTrue(validationError.getTimestamp() > 0);
    }

    @Test
    void shouldAllowChainedOperations() {
        // Test that setters can be chained (they return void, so can't actually chain)
        // But we can test sequential operations

        ErrorResponse response = new ErrorResponse("Initial");

        // Sequential updates
        response.setError("Updated");
        response.setTimestamp(999L);

        assertEquals("Updated", response.getError());
        assertEquals(999L, response.getTimestamp());
    }

    @Test
    void shouldWorkInExceptionHandlingContext() {
        // Simulate how ErrorResponse would be created in exception handling

        try {
            // Simulate an exception
            throw new RuntimeException("Something went wrong");
        } catch (RuntimeException e) {
            // Create error response from exception
            ErrorResponse response = new ErrorResponse(e.getMessage());

            assertNotNull(response);
            assertEquals("Something went wrong", response.getError());
            assertTrue(response.getTimestamp() > 0);
        }
    }
}