package com.fulfilment.application.monolith.validation;

import com.fulfilment.application.monolith.Validation.BusinessUnitValidatorImpl;
import com.fulfilment.application.monolith.warehouses.domain.ports.BusinessUnitValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class BusinessUnitValidatorImplTest {

    @InjectMocks
    private BusinessUnitValidatorImpl validator;

    @Test
    void shouldBeApplicationScoped() {
        ApplicationScoped annotation = BusinessUnitValidatorImpl.class
                .getAnnotation(ApplicationScoped.class);
        assertNotNull(annotation, "Class should be annotated with @ApplicationScoped");
    }

    @Test
    void shouldImplementBusinessUnitValidatorInterface() {
        assertTrue(BusinessUnitValidator.class.isAssignableFrom(BusinessUnitValidatorImpl.class),
                "Should implement BusinessUnitValidator interface");
    }

    @Test
    void isBusinessUnitCodeUnique_shouldReturnFalseForRegisteredCodes() {
        // Arrange - setup done in @BeforeEach
        assertFalse(validator.isBusinessUnitCodeUnique("BU-001"));
        assertFalse(validator.isBusinessUnitCodeUnique("BU-002"));
        assertFalse(validator.isBusinessUnitCodeUnique("BU-003"));
        assertFalse(validator.isBusinessUnitCodeUnique("BU-004"));
        assertFalse(validator.isBusinessUnitCodeUnique("BU-005"));
    }

    @Test
    void isBusinessUnitCodeUnique_shouldReturnTrueForNonRegisteredCodes() {
        // Act & Assert
        assertTrue(validator.isBusinessUnitCodeUnique("BU-006"));
        assertTrue(validator.isBusinessUnitCodeUnique("BU-007"));
        assertTrue(validator.isBusinessUnitCodeUnique("BU-100"));
        assertTrue(validator.isBusinessUnitCodeUnique("BU-999"));
        assertTrue(validator.isBusinessUnitCodeUnique("NEW-BU"));
    }

    @Test
    void isBusinessUnitCodeUnique_shouldReturnFalseForNullCode() {
        // Act
        boolean result = validator.isBusinessUnitCodeUnique(null);

        // Assert
        assertFalse(result, "Should return false for null business unit code");
    }

    @Test
    void isBusinessUnitCodeUnique_shouldReturnFalseForEmptyCode() {
        // Act
        boolean result = validator.isBusinessUnitCodeUnique("");

        // Assert
        assertFalse(result, "Should return false for empty business unit code");
    }

    @Test
    void isBusinessUnitCodeUnique_shouldReturnFalseForBlankCode() {
        // Act & Assert
        assertFalse(validator.isBusinessUnitCodeUnique("   "),
                "Should return false for blank business unit code");
        assertFalse(validator.isBusinessUnitCodeUnique("\t\n"),
                "Should return false for whitespace-only business unit code");
    }

    @Test
    void isBusinessUnitCodeUnique_shouldTrimInputBeforeChecking() {
        // Act & Assert
        assertFalse(validator.isBusinessUnitCodeUnique(" BU-001 "),
                "Should trim spaces and recognize registered code");
        assertFalse(validator.isBusinessUnitCodeUnique("BU-001 "),
                "Should trim trailing space and recognize registered code");
        assertFalse(validator.isBusinessUnitCodeUnique(" BU-001"),
                "Should trim leading space and recognize registered code");
    }

    @Test
    void isBusinessUnitCodeUnique_shouldBeCaseSensitive() {
        // Act & Assert
        assertTrue(validator.isBusinessUnitCodeUnique("bu-001"),
                "Lowercase version of registered code should be considered unique");
        assertTrue(validator.isBusinessUnitCodeUnique("Bu-001"),
                "Mixed case version should be considered unique");
        assertTrue(validator.isBusinessUnitCodeUnique("BU-001".toLowerCase()),
                "Fully lowercase should be considered unique");
    }

    @Test
    void isBusinessUnitCodeUnique_shouldReturnConsistentResults() {
        // Act
        boolean result1 = validator.isBusinessUnitCodeUnique("BU-001");
        boolean result2 = validator.isBusinessUnitCodeUnique("BU-001");

        // Assert
        assertEquals(result1, result2,
                "Multiple calls with same input should return consistent results");
    }

    @Test
    void isBusinessUnitCodeUnique_shouldHandleSpecialCharacters() {
        // Act & Assert
        assertTrue(validator.isBusinessUnitCodeUnique("BU-001@special"));
        assertTrue(validator.isBusinessUnitCodeUnique("BU-001#"));
        assertTrue(validator.isBusinessUnitCodeUnique("BU-001$%^&"));
    }

    @Test
    void isBusinessUnitCodeUnique_shouldHandleLongCodes() {
        // Arrange
        String longCode = "BU-" + "A".repeat(1000);

        // Act & Assert
        assertTrue(validator.isBusinessUnitCodeUnique(longCode),
                "Should handle very long business unit codes");
    }

    @Test
    void isBusinessUnitCodeUnique_shouldHandleUnicodeCharacters() {
        // Act & Assert
        assertTrue(validator.isBusinessUnitCodeUnique("BU-中文"));
        assertTrue(validator.isBusinessUnitCodeUnique("BU-😀"));
        assertTrue(validator.isBusinessUnitCodeUnique("BU-αβγ"));
    }

    @Test
    void isBusinessUnitCodeUnique_shouldReturnTrueForDifferentFormats() {
        // Act & Assert
        assertTrue(validator.isBusinessUnitCodeUnique("BU_001"));  // Underscore instead of dash
        assertTrue(validator.isBusinessUnitCodeUnique("BU.001"));  // Dot instead of dash
        assertTrue(validator.isBusinessUnitCodeUnique("BU001"));   // No separator
    }

    @Test
    void isBusinessUnitCodeUnique_shouldHandleEdgeCases() {
        // Act & Assert
        assertFalse(validator.isBusinessUnitCodeUnique("BU-001"));
        assertTrue(validator.isBusinessUnitCodeUnique("BU- 001")); // Space in middle
    }

    @Test
    void isBusinessUnitCodeUnique_shouldBeThreadSafe() throws InterruptedException {
        // Arrange
        final int threadCount = 10;
        final Thread[] threads = new Thread[threadCount];
        final boolean[] results = new boolean[threadCount];

        // Act
        for (int i = 0; i < threadCount; i++) {
            final int index = i;
            threads[i] = new Thread(() -> {
                results[index] = validator.isBusinessUnitCodeUnique("BU-001");
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        // Assert - all threads should get the same result
        boolean expectedResult = validator.isBusinessUnitCodeUnique("BU-001");
        for (int i = 0; i < threadCount; i++) {
            assertEquals(expectedResult, results[i],
                    "Thread " + i + " should get consistent result");
        }
    }

    @Test
    void staticInitializer_shouldInitializeRegisteredBusinessUnits() throws Exception {
        // Arrange - use reflection to access private static field
        Field field = BusinessUnitValidatorImpl.class.getDeclaredField("REGISTERED_BUSINESS_UNITS");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        Set<String> registeredUnits = (Set<String>) field.get(null);

        // Assert
        assertNotNull(registeredUnits, "REGISTERED_BUSINESS_UNITS should be initialized");
        assertFalse(registeredUnits.isEmpty(), "REGISTERED_BUSINESS_UNITS should not be empty");
        assertEquals(5, registeredUnits.size(), "Should have 5 pre-registered business units");

        // Verify all expected codes are present
        assertTrue(registeredUnits.contains("BU-001"));
        assertTrue(registeredUnits.contains("BU-002"));
        assertTrue(registeredUnits.contains("BU-003"));
        assertTrue(registeredUnits.contains("BU-004"));
        assertTrue(registeredUnits.contains("BU-005"));
    }

    @Test
    void isBusinessUnitCodeUnique_shouldUseTrimMethod() {
        // This is a behavioral test - we're testing that trim() is called
        // Note: We can't easily mock String.trim() as it's final

        // Test with spaces to verify trim() behavior
        String codeWithSpaces = "  BU-001  ";
        assertFalse(validator.isBusinessUnitCodeUnique(codeWithSpaces));

        // Test that the method handles trimmed version correctly
        String trimmedCode = codeWithSpaces.trim();
        assertFalse(validator.isBusinessUnitCodeUnique(trimmedCode));
    }

    @Test
    void isBusinessUnitCodeUnique_shouldHandleNullAndEmptyEarlyReturn() {
        // Test that null/empty/blank codes return false without checking the set

        // We can't easily verify the internal behavior without reflection,
        // but we can verify the expected results
        assertFalse(validator.isBusinessUnitCodeUnique(null));
        assertFalse(validator.isBusinessUnitCodeUnique(""));
        assertFalse(validator.isBusinessUnitCodeUnique("   "));
        assertFalse(validator.isBusinessUnitCodeUnique("\t\n\r"));
    }

    @Test
    void constructor_shouldInitializeProperly() {
        // Act - create a new instance
        BusinessUnitValidatorImpl newInstance = new BusinessUnitValidatorImpl();

        // Assert
        assertNotNull(newInstance, "Should be able to create instance");

        // Verify it works correctly
        assertFalse(newInstance.isBusinessUnitCodeUnique("BU-001"));
        assertTrue(newInstance.isBusinessUnitCodeUnique("BU-999"));
    }
}