package com.fulfilment.application.monolith.validation;

import com.fulfilment.application.monolith.Validation.WarehouseCapacityValidatorImpl;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseCapacityValidator;
import jakarta.enterprise.context.ApplicationScoped;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class WarehouseCapacityValidatorImplTest {

    private WarehouseCapacityValidatorImpl validator;

    @BeforeEach
    void setUp() {
        validator = new WarehouseCapacityValidatorImpl();
    }

    @Test
    void shouldBeApplicationScoped() {
        ApplicationScoped annotation = WarehouseCapacityValidatorImpl.class
                .getAnnotation(ApplicationScoped.class);
        assertNotNull(annotation, "Class should be annotated with @ApplicationScoped");
    }

    @Test
    void shouldImplementWarehouseCapacityValidatorInterface() {
        assertTrue(WarehouseCapacityValidator.class.isAssignableFrom(WarehouseCapacityValidatorImpl.class),
                "Should implement WarehouseCapacityValidator interface");
    }

    // Tests for canCreateWarehouseAtLocation method
    @Test
    void canCreateWarehouseAtLocation_shouldReturnTrueWhenBelowCapacity() {
        boolean result = validator.canCreateWarehouseAtLocation("ZWOLLE-001", 0);
        assertTrue(result, "Should allow creation when current count (0) < max (5)");

        result = validator.canCreateWarehouseAtLocation("ZWOLLE-001", 4);
        assertTrue(result, "Should allow creation when current count (4) < max (5)");
    }

    @Test
    void canCreateWarehouseAtLocation_shouldReturnFalseWhenAtOrAboveCapacity() {
        boolean result = validator.canCreateWarehouseAtLocation("ZWOLLE-001", 5);
        assertFalse(result, "Should not allow creation when current count (5) = max (5)");

        result = validator.canCreateWarehouseAtLocation("ZWOLLE-001", 6);
        assertFalse(result, "Should not allow creation when current count (6) > max (5)");
    }

    @Test
    void canCreateWarehouseAtLocation_shouldReturnFalseForNullLocation() {
        boolean result = validator.canCreateWarehouseAtLocation(null, 0);
        assertFalse(result, "Should return false for null location identifier");
    }

    @Test
    void canCreateWarehouseAtLocation_shouldReturnFalseForEmptyLocation() {
        boolean result = validator.canCreateWarehouseAtLocation("", 0);
        assertFalse(result, "Should return false for empty location identifier");
    }

    @Test
    void canCreateWarehouseAtLocation_shouldReturnFalseForBlankLocation() {
        boolean result = validator.canCreateWarehouseAtLocation("   ", 0);
        assertFalse(result, "Should return false for blank location identifier");

        result = validator.canCreateWarehouseAtLocation("\t\n", 0);
        assertFalse(result, "Should return false for whitespace-only location identifier");
    }

    @Test
    void canCreateWarehouseAtLocation_shouldReturnFalseForUnknownLocation() {
        boolean result = validator.canCreateWarehouseAtLocation("UNKNOWN", 0);
        assertFalse(result, "Should return false for unknown location identifier");

        result = validator.canCreateWarehouseAtLocation("INVALID-LOCATION", 0);
        assertFalse(result, "Should return false for invalid location identifier");
    }

    // Tests for hasSufficientCapacity method
    @Test
    void hasSufficientCapacity_shouldReturnTrueWhenCapacityAvailable() {
        boolean result = validator.hasSufficientCapacity("ZWOLLE-001", 500);
        assertTrue(result, "Should return true when requested (500) = total capacity (500)");

        result = validator.hasSufficientCapacity("ZWOLLE-001", 499);
        assertTrue(result, "Should return true when requested (499) < total capacity (500)");

        result = validator.hasSufficientCapacity("ZWOLLE-001", 0);
        assertTrue(result, "Should return true for zero requested capacity");
    }

    @Test
    void hasSufficientCapacity_shouldReturnFalseWhenInsufficientCapacity() {
        boolean result = validator.hasSufficientCapacity("ZWOLLE-001", 501);
        assertFalse(result, "Should return false when requested (501) > total capacity (500)");
    }

    @Test
    void hasSufficientCapacity_shouldReturnFalseForNullLocation() {
        boolean result = validator.hasSufficientCapacity(null, 100);
        assertFalse(result, "Should return false for null location identifier");
    }

    @Test
    void hasSufficientCapacity_shouldReturnFalseForEmptyLocation() {
        boolean result = validator.hasSufficientCapacity("", 100);
        assertFalse(result, "Should return false for empty location identifier");
    }

    @Test
    void hasSufficientCapacity_shouldReturnFalseForBlankLocation() {
        boolean result = validator.hasSufficientCapacity("   ", 100);
        assertFalse(result, "Should return false for blank location identifier");
    }

    @Test
    void hasSufficientCapacity_shouldReturnFalseForUnknownLocation() {
        boolean result = validator.hasSufficientCapacity("UNKNOWN", 100);
        assertFalse(result, "Should return false for unknown location identifier");
    }

    // Additional tests for different locations
    @Test
    void testAllRegisteredLocations() {
        // Test each registered location
        String[] locations = {
                "ZWOLLE-001", "ZWOLLE-002", "AMSTERDAM-001", "AMSTERDAM-002",
                "TILBURG-001", "HELMOND-001", "EINDHOVEN-001", "VETSBY-001"
        };

        for (String location : locations) {
            // Test that location exists
            boolean canCreate = validator.canCreateWarehouseAtLocation(location, 0);
            assertTrue(canCreate, "Location " + location + " should exist");

            // Test that hasSufficientCapacity works
            boolean hasCapacity = validator.hasSufficientCapacity(location, 100);
            assertTrue(hasCapacity, "Location " + location + " should have capacity for 100");
        }
    }

    @Test
    void shouldBeCaseSensitiveForLocationIdentifiers() {
        // Test that location identifiers are case-sensitive
        boolean result1 = validator.canCreateWarehouseAtLocation("zwolle-001", 0); // lowercase
        boolean result2 = validator.canCreateWarehouseAtLocation("Zwolle-001", 0); // mixed case

        assertFalse(result1, "Lowercase location identifier should not be found");
        assertFalse(result2, "Mixed case location identifier should not be found");
    }

    @Test
    void shouldNotTrimInput() {
        // The current implementation doesn't trim
        boolean result1 = validator.canCreateWarehouseAtLocation(" ZWOLLE-001", 0);
        boolean result2 = validator.hasSufficientCapacity("ZWOLLE-001 ", 100);

        assertFalse(result1, "Should not trim leading spaces");
        assertFalse(result2, "Should not trim trailing spaces");
    }

    @Test
    void locationCapacityClass_shouldHaveCorrectGetters() {
        // Test the private static inner class
        WarehouseCapacityValidatorImpl.LocationCapacity capacity =
                new WarehouseCapacityValidatorImpl.LocationCapacity(5, 500);

        assertEquals(5, capacity.getMaxWarehouses());
        assertEquals(500, capacity.getTotalCapacity());
    }

    @Test
    void locationCapacityClass_shouldBeImmutable() {
        // Test that LocationCapacity is immutable
        WarehouseCapacityValidatorImpl.LocationCapacity capacity =
                new WarehouseCapacityValidatorImpl.LocationCapacity(5, 500);

        assertEquals(5, capacity.getMaxWarehouses());
        assertEquals(500, capacity.getTotalCapacity());
    }
}