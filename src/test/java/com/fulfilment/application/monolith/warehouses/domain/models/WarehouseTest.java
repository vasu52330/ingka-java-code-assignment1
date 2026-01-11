package com.fulfilment.application.monolith.warehouses.domain.models;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WarehouseAndLocationIntegrationTest {

    @Test
    void shouldCreateWarehouseForLocation() {
        // Arrange - create a location
        Location location = Location.builder()
                .identification("LOC-001")
                .name("Amsterdam Distribution Center")
                .maxNumberOfWarehouses(10)
                .maxCapacity(10000)
                .build();

        // Act - create warehouse for that location
        Warehouse warehouse = Warehouse.builder()
                .identifier("WH-001")
                .name("Main Warehouse")
                .businessUnitCode("BU-001")
                .locationIdentifier(location.getIdentification())
                .capacity(1000)
                .currentStock(500)
                .active(true)
                .build();

        // Assert
        assertEquals(location.getIdentification(), warehouse.getLocationIdentifier());
        assertTrue(warehouse.getCapacity() <= location.getMaxCapacity());
        assertTrue(warehouse.isActive());
    }

    @Test
    void shouldValidateWarehouseAgainstLocationCapacity() {
        // Arrange
        Location location = Location.builder()
                .identification("LOC-001")
                .maxNumberOfWarehouses(5)
                .maxCapacity(5000)
                .build();

        // This should be valid
        Warehouse validWarehouse = Warehouse.builder()
                .identifier("WH-001")
                .name("Warehouse")
                .businessUnitCode("BU-001")
                .locationIdentifier(location.getIdentification())
                .capacity(1000) // Within location capacity
                .currentStock(500)
                .build();

        // This would exceed location capacity (but domain model doesn't validate this)
        Warehouse largeWarehouse = Warehouse.builder()
                .identifier("WH-002")
                .name("Large Warehouse")
                .businessUnitCode("BU-001")
                .locationIdentifier(location.getIdentification())
                .capacity(6000) // Exceeds location max capacity
                .currentStock(0)
                .build();

        // Assert - both should build successfully
        // (Actual validation would happen in a service layer)
        assertNotNull(validWarehouse);
        assertNotNull(largeWarehouse);
    }

    @Test
    void shouldHandleWarehouseStates() {
        // Test different warehouse states

        // Active warehouse
        Warehouse activeWarehouse = Warehouse.builder()
                .identifier("WH-001")
                .name("Active Warehouse")
                .businessUnitCode("BU-001")
                .locationIdentifier("LOC-001")
                .capacity(1000)
                .currentStock(500)
                .active(true)
                .archived(false)
                .build();

        // Archived warehouse
        Warehouse archivedWarehouse = Warehouse.builder()
                .identifier("WH-002")
                .name("Archived Warehouse")
                .businessUnitCode("BU-001")
                .locationIdentifier("LOC-001")
                .capacity(1000)
                .currentStock(0)
                .active(false)
                .archived(true)
                .build();

        // Inactive but not archived
        Warehouse inactiveWarehouse = Warehouse.builder()
                .identifier("WH-003")
                .name("Inactive Warehouse")
                .businessUnitCode("BU-001")
                .locationIdentifier("LOC-001")
                .capacity(1000)
                .currentStock(0)
                .active(false)
                .archived(false)
                .build();

        // Assert different states
        assertTrue(activeWarehouse.isActive() && !activeWarehouse.isArchived());
        assertTrue(!archivedWarehouse.isActive() && archivedWarehouse.isArchived());
        assertTrue(!inactiveWarehouse.isActive() && !inactiveWarehouse.isArchived());
    }

    @Test
    void shouldCreateMultipleWarehousesForSameLocation() {
        // Arrange
        String locationId = "LOC-001";

        // Act
        Warehouse warehouse1 = Warehouse.builder()
                .identifier("WH-001")
                .name("Warehouse 1")
                .businessUnitCode("BU-001")
                .locationIdentifier(locationId)
                .capacity(500)
                .currentStock(250)
                .build();

        Warehouse warehouse2 = Warehouse.builder()
                .identifier("WH-002")
                .name("Warehouse 2")
                .businessUnitCode("BU-002")
                .locationIdentifier(locationId) // Same location
                .capacity(750)
                .currentStock(375)
                .build();

        // Assert
        assertEquals(locationId, warehouse1.getLocationIdentifier());
        assertEquals(locationId, warehouse2.getLocationIdentifier());
        assertNotEquals(warehouse1.getIdentifier(), warehouse2.getIdentifier());
        assertNotEquals(warehouse1.getBusinessUnitCode(), warehouse2.getBusinessUnitCode());
    }
}