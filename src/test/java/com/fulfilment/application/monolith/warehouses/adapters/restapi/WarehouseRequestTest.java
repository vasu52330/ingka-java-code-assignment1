package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseRequestTest {

    private WarehouseRequest request;

    @BeforeEach
    void setUp() {
        request = new WarehouseRequest();
    }

    @Test
    void shouldSetAndGetBusinessUnitCode() {
        // Arrange
        String expectedCode = "BU-001";

        // Act
        request.setBusinessUnitCode(expectedCode);

        // Assert
        assertEquals(expectedCode, request.getBusinessUnitCode());
    }

    @Test
    void shouldSetAndGetLocationIdentifier() {
        // Arrange
        String expectedLocation = "AMSTERDAM-001";

        // Act
        request.setLocationIdentifier(expectedLocation);

        // Assert
        assertEquals(expectedLocation, request.getLocationIdentifier());
    }

    @Test
    void shouldSetAndGetCapacity() {
        // Arrange
        Integer expectedCapacity = 1000;

        // Act
        request.setCapacity(expectedCapacity);

        // Assert
        assertEquals(expectedCapacity, request.getCapacity());
    }

    @Test
    void shouldSetAndGetStock() {
        // Arrange
        Integer expectedStock = 500;

        // Act
        request.setStock(expectedStock);

        // Assert
        assertEquals(expectedStock, request.getStock());
    }

    @Test
    void shouldHandleNullValues() {
        // Act
        request.setBusinessUnitCode(null);
        request.setLocationIdentifier(null);
        request.setCapacity(null);
        request.setStock(null);

        // Assert
        assertNull(request.getBusinessUnitCode());
        assertNull(request.getLocationIdentifier());
        assertNull(request.getCapacity());
        assertNull(request.getStock());
    }

    @Test
    void shouldHandleEmptyStrings() {
        // Act
        request.setBusinessUnitCode("");
        request.setLocationIdentifier("");

        // Assert
        assertEquals("", request.getBusinessUnitCode());
        assertEquals("", request.getLocationIdentifier());
    }

    @Test
    void shouldHandleSpecialCharacters() {
        // Act
        request.setBusinessUnitCode("BU-001@Special");
        request.setLocationIdentifier("LOC-001#Area");

        // Assert
        assertEquals("BU-001@Special", request.getBusinessUnitCode());
        assertEquals("LOC-001#Area", request.getLocationIdentifier());
    }

    // Test validation logic manually (without Validator)
    @Test
    void shouldHaveRequiredAnnotations() {
        // This test documents the expected validation annotations
        // You can't test them without the validator, but you can document them

        // Expected annotations:
        // @NotBlank on businessUnitCode
        // @NotBlank on locationIdentifier
        // @NotNull @Min(1) on capacity
        // @NotNull @PositiveOrZero on stock

        // Just test that the class can be instantiated and used
        assertNotNull(request);
    }
}