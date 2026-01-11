package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseResponseTest {

    @Test
    void shouldCreateDefaultInstance() {
        // Act
        WarehouseResponse response = new WarehouseResponse();

        // Assert
        assertNotNull(response);
        assertNull(response.getId());
        assertNull(response.getIdentifier());
        assertFalse(response.isActive());
        assertNull(response.getBusinessUnitCode());
        assertNull(response.getLocationIdentifier());
        assertNull(response.getCapacity());
        assertNull(response.getStock());
        assertFalse(response.isArchived());
        assertNull(response.getName());
        assertNull(response.getCreationDate());
    }

    @Test
    void shouldSetAndGetId() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();
        String expectedId = "12345";

        // Act
        response.setId(expectedId);

        // Assert
        assertEquals(expectedId, response.getId());
    }

    @Test
    void shouldSetAndGetIdentifier() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();
        String expectedIdentifier = "WH-001";

        // Act
        response.setIdentifier(expectedIdentifier);

        // Assert
        assertEquals(expectedIdentifier, response.getIdentifier());
    }

    @Test
    void shouldSetAndGetActive() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();

        // Act
        response.setActive(true);

        // Assert
        assertTrue(response.isActive());

        // Act - set to false
        response.setActive(false);

        // Assert
        assertFalse(response.isActive());
    }

    @Test
    void shouldSetAndGetBusinessUnitCode() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();
        String expectedBusinessUnitCode = "BU-001";

        // Act
        response.setBusinessUnitCode(expectedBusinessUnitCode);

        // Assert
        assertEquals(expectedBusinessUnitCode, response.getBusinessUnitCode());
    }

    @Test
    void shouldSetAndGetLocationIdentifier() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();
        String expectedLocationIdentifier = "AMSTERDAM-001";

        // Act
        response.setLocationIdentifier(expectedLocationIdentifier);

        // Assert
        assertEquals(expectedLocationIdentifier, response.getLocationIdentifier());
    }

    @Test
    void shouldSetAndGetCapacity() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();
        Integer expectedCapacity = 1000;

        // Act
        response.setCapacity(expectedCapacity);

        // Assert
        assertEquals(expectedCapacity, response.getCapacity());
    }

    @Test
    void shouldSetAndGetStock() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();
        Integer expectedStock = 500;

        // Act
        response.setStock(expectedStock);

        // Assert
        assertEquals(expectedStock, response.getStock());
    }

    @Test
    void shouldSetAndGetArchived() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();

        // Act
        response.setArchived(true);

        // Assert
        assertTrue(response.isArchived());

        // Act - set to false
        response.setArchived(false);

        // Assert
        assertFalse(response.isArchived());
    }

    @Test
    void shouldSetAndGetName() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();
        String expectedName = "Main Warehouse";

        // Act
        response.setName(expectedName);

        // Assert
        assertEquals(expectedName, response.getName());
    }

    @Test
    void shouldSetAndGetCreationDate() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();
        LocalDateTime expectedDate = LocalDateTime.now();

        // Act
        response.setCreationDate(expectedDate);

        // Assert
        assertEquals(expectedDate, response.getCreationDate());
    }

    @Test
    void shouldHandleNullValues() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();

        // Act - set all fields to null
        response.setId(null);
        response.setIdentifier(null);
        response.setBusinessUnitCode(null);
        response.setLocationIdentifier(null);
        response.setCapacity(null);
        response.setStock(null);
        response.setName(null);
        response.setCreationDate(null);

        // Assert
        assertNull(response.getId());
        assertNull(response.getIdentifier());
        assertNull(response.getBusinessUnitCode());
        assertNull(response.getLocationIdentifier());
        assertNull(response.getCapacity());
        assertNull(response.getStock());
        assertNull(response.getName());
        assertNull(response.getCreationDate());
    }

    @Test
    void shouldHandleEmptyStrings() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();

        // Act
        response.setId("");
        response.setIdentifier("");
        response.setBusinessUnitCode("");
        response.setLocationIdentifier("");
        response.setName("");

        // Assert
        assertEquals("", response.getId());
        assertEquals("", response.getIdentifier());
        assertEquals("", response.getBusinessUnitCode());
        assertEquals("", response.getLocationIdentifier());
        assertEquals("", response.getName());
    }

    @Test
    void shouldHandleWhitespaceStrings() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();

        // Act
        response.setId("  ");
        response.setIdentifier("\t\n");
        response.setBusinessUnitCode("  BU-001  ");
        response.setLocationIdentifier("  AMSTERDAM-001  ");
        response.setName("  Warehouse Name  ");

        // Assert
        assertEquals("  ", response.getId());
        assertEquals("\t\n", response.getIdentifier());
        assertEquals("  BU-001  ", response.getBusinessUnitCode());
        assertEquals("  AMSTERDAM-001  ", response.getLocationIdentifier());
        assertEquals("  Warehouse Name  ", response.getName());
    }

    @Test
    void shouldHandleNegativeValues() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();

        // Act
        response.setCapacity(-100);
        response.setStock(-50);

        // Assert
        assertEquals(-100, response.getCapacity());
        assertEquals(-50, response.getStock());
    }

    @Test
    void shouldHandleZeroValues() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();

        // Act
        response.setCapacity(0);
        response.setStock(0);

        // Assert
        assertEquals(0, response.getCapacity());
        assertEquals(0, response.getStock());
    }

    @Test
    void shouldHandleLargeValues() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();

        // Act
        response.setCapacity(Integer.MAX_VALUE);
        response.setStock(Integer.MAX_VALUE);

        // Assert
        assertEquals(Integer.MAX_VALUE, response.getCapacity());
        assertEquals(Integer.MAX_VALUE, response.getStock());
    }

    @Test
    void shouldHandleSpecialCharactersInStrings() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();

        // Act
        response.setId("123@#$%");
        response.setIdentifier("WH-001@Special#");
        response.setBusinessUnitCode("BU-001_@Special");
        response.setLocationIdentifier("AMSTERDAM-001#Area");
        response.setName("Main Warehouse & Storage");

        // Assert
        assertEquals("123@#$%", response.getId());
        assertEquals("WH-001@Special#", response.getIdentifier());
        assertEquals("BU-001_@Special", response.getBusinessUnitCode());
        assertEquals("AMSTERDAM-001#Area", response.getLocationIdentifier());
        assertEquals("Main Warehouse & Storage", response.getName());
    }

    @Test
    void shouldSetAllFieldsAndRetrieveThem() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();
        LocalDateTime now = LocalDateTime.now();

        // Act - set all fields
        response.setId("123");
        response.setIdentifier("WH-001");
        response.setActive(true);
        response.setBusinessUnitCode("BU-001");
        response.setLocationIdentifier("AMSTERDAM-001");
        response.setCapacity(1000);
        response.setStock(500);
        response.setArchived(false);
        response.setName("Main Warehouse");
        response.setCreationDate(now);

        // Assert - verify all fields
        assertEquals("123", response.getId());
        assertEquals("WH-001", response.getIdentifier());
        assertTrue(response.isActive());
        assertEquals("BU-001", response.getBusinessUnitCode());
        assertEquals("AMSTERDAM-001", response.getLocationIdentifier());
        assertEquals(1000, response.getCapacity());
        assertEquals(500, response.getStock());
        assertFalse(response.isArchived());
        assertEquals("Main Warehouse", response.getName());
        assertEquals(now, response.getCreationDate());
    }

    @Test
    void shouldToggleBooleanFields() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();

        // Test active field toggle
        response.setActive(true);
        assertTrue(response.isActive());

        response.setActive(false);
        assertFalse(response.isActive());

        response.setActive(true);
        assertTrue(response.isActive());

        // Test archived field toggle
        response.setArchived(true);
        assertTrue(response.isArchived());

        response.setArchived(false);
        assertFalse(response.isArchived());

        response.setArchived(true);
        assertTrue(response.isArchived());
    }

    @Test
    void shouldHandleCreationDateEdgeCases() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();

        // Test with far past date
        LocalDateTime pastDate = LocalDateTime.now().minusYears(10);
        response.setCreationDate(pastDate);
        assertEquals(pastDate, response.getCreationDate());

        // Test with far future date
        LocalDateTime futureDate = LocalDateTime.now().plusYears(10);
        response.setCreationDate(futureDate);
        assertEquals(futureDate, response.getCreationDate());

        // Test with same date (should work)
        LocalDateTime sameDate = LocalDateTime.of(2024, 1, 1, 0, 0);
        response.setCreationDate(sameDate);
        assertEquals(sameDate, response.getCreationDate());
    }

    @Test
    void shouldHaveDefaultToString() {
        // Arrange
        WarehouseResponse response = new WarehouseResponse();
        response.setIdentifier("WH-001");

        // Act
        String toStringResult = response.toString();

        // Assert
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("WarehouseResponse"));
    }

    @Test
    void shouldHaveDefaultEqualsAndHashCode() {
        // Since equals() and hashCode() are not overridden, test default behavior
        WarehouseResponse response1 = new WarehouseResponse();
        response1.setId("123");

        WarehouseResponse response2 = new WarehouseResponse();
        response2.setId("123");

        // Default equals() compares object identity, not field values
        assertNotEquals(response1, response2);
        assertNotEquals(response1.hashCode(), response2.hashCode());
    }

    @Test
    void shouldAllowFieldDirectAccessForCreationDate() {
        // Note: creationDate is a public field, not a private field with getter/setter
        // But the class has getCreationDate() and setCreationDate() methods
        // Let's test both

        WarehouseResponse response = new WarehouseResponse();
        LocalDateTime now = LocalDateTime.now();

        // Test via setter
        response.setCreationDate(now);
        assertEquals(now, response.getCreationDate());

        // Test direct field access (if field is public)
        LocalDateTime newDate = LocalDateTime.now().plusDays(1);
        response.creationDate = newDate;
        assertEquals(newDate, response.creationDate);
        assertEquals(newDate, response.getCreationDate());
    }

    @Test
    void shouldHandleBusinessLogicScenarios() {
        // Test scenarios that might represent business logic states

        // Scenario 1: Active, not archived warehouse
        WarehouseResponse activeWarehouse = new WarehouseResponse();
        activeWarehouse.setActive(true);
        activeWarehouse.setArchived(false);
        activeWarehouse.setStock(100);
        activeWarehouse.setCapacity(1000);

        assertTrue(activeWarehouse.isActive());
        assertFalse(activeWarehouse.isArchived());
        assertTrue(activeWarehouse.getStock() < activeWarehouse.getCapacity());

        // Scenario 2: Archived warehouse
        WarehouseResponse archivedWarehouse = new WarehouseResponse();
        archivedWarehouse.setActive(false);
        archivedWarehouse.setArchived(true);
        archivedWarehouse.setStock(0);

        assertFalse(archivedWarehouse.isActive());
        assertTrue(archivedWarehouse.isArchived());
        assertEquals(0, archivedWarehouse.getStock());

        // Scenario 3: Full warehouse
        WarehouseResponse fullWarehouse = new WarehouseResponse();
        fullWarehouse.setStock(1000);
        fullWarehouse.setCapacity(1000);

        assertEquals(fullWarehouse.getStock(), fullWarehouse.getCapacity());
    }

    @Test
    void shouldHandleNullVsEmptyStringsDifferently() {
        WarehouseResponse response = new WarehouseResponse();

        // Set to empty string
        response.setName("");
        assertEquals("", response.getName());
        assertFalse(response.getName() == null);

        // Set to null
        response.setName(null);
        assertNull(response.getName());

        // Set back to empty
        response.setName("");
        assertEquals("", response.getName());
    }

    @Test
    void shouldBeSerializable() {
        // Check if class can be serialized (important for REST responses)
        // The class doesn't explicitly implement Serializable, but test this fact
        boolean isSerializable = java.io.Serializable.class.isAssignableFrom(WarehouseResponse.class);

        // Note: For REST responses, JSON serialization doesn't require Serializable
        // But it's good to document this fact
        assertFalse(isSerializable, "WarehouseResponse doesn't implement Serializable (may not be needed for JSON)");
    }

    @Test
    void shouldHaveConsistentFieldNamesWithDomainModel() {
        // This test documents that field names match domain model
        WarehouseResponse response = new WarehouseResponse();

        // Field names from domain model:
        // identifier, active, businessUnitCode, locationIdentifier, capacity, stock, archived, name

        // All these fields are present with getters/setters
        response.setIdentifier("test");
        response.setActive(true);
        response.setBusinessUnitCode("test");
        response.setLocationIdentifier("test");
        response.setCapacity(1);
        response.setStock(1);
        response.setArchived(false);
        response.setName("test");

        // If we can set and get all, field names are consistent
        assertEquals("test", response.getIdentifier());
        assertTrue(response.isActive());
        assertEquals("test", response.getBusinessUnitCode());
        assertEquals("test", response.getLocationIdentifier());
        assertEquals(1, response.getCapacity());
        assertEquals(1, response.getStock());
        assertFalse(response.isArchived());
        assertEquals("test", response.getName());
    }
}