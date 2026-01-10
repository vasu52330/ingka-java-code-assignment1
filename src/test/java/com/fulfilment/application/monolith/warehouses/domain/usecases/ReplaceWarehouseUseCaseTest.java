package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReplaceWarehouseUseCaseTest {

    @Mock
    private WarehouseStore warehouseStore;

    @Mock
    private LocationResolver locationResolver;

    @Mock
    private BusinessUnitValidator businessUnitValidator;

    @Mock
    private WarehouseCapacityValidator capacityValidator;

    private ReplaceWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ReplaceWarehouseUseCase(
                warehouseStore, locationResolver, businessUnitValidator, capacityValidator);
    }

    // Helper method to create a valid warehouse
    private Warehouse createWarehouse(Long id, String identifier, String name,
                                      String businessUnitCode, String locationId,
                                      int capacity, int currentStock, boolean active) {
        return Warehouse.builder()
                .id(id)
                .identifier(identifier)
                .name(name)
                .businessUnitCode(businessUnitCode)
                .locationIdentifier(locationId)
                .capacity(capacity)
                .currentStock(currentStock)
                .active(active)
                .archived(false)
                .build();
    }

    // Helper method to create a location
    private Location createLocation(int maxCapacity) {
        Location location = new Location();
        location.setMaxCapacity(maxCapacity);
        return location;
    }

    @Test
    void replace_ShouldSucceed_WhenAllValidationsPass() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, true);

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-001", 1500, 500, false);

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        assertEquals(1L, newWarehouse.getId());
        assertTrue(newWarehouse.isActive());
        assertFalse(newWarehouse.isArchived());
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void replace_ShouldThrowException_WhenWarehouseNotFound() {
        // Given
        String identifier = "NON-EXISTENT";
        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-001", 1000, 0, false);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.replace(newWarehouse));

        assertEquals("Warehouse not found: " + identifier, exception.getMessage());
        verify(warehouseStore, never()).update(any(Warehouse.class));
    }

    @Test
    void replace_ShouldThrowException_WhenIdentifierIsNull() {
        // Given
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setIdentifier(null);
        newWarehouse.setName("Test Warehouse");
        newWarehouse.setBusinessUnitCode("BU-001");
        newWarehouse.setLocationIdentifier("LOC-001");
        newWarehouse.setCapacity(1000);
        newWarehouse.setCurrentStock(0);

        when(warehouseStore.findByIdentifier(isNull())).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.replace(newWarehouse));

        assertEquals("Warehouse not found: null", exception.getMessage());
        verify(warehouseStore, never()).update(any(Warehouse.class));
    }

    @Test
    void replace_ShouldThrowException_WhenIdentifierIsEmpty() {
        // Given
        String identifier = "";
        Warehouse newWarehouse = new Warehouse();
        newWarehouse.setIdentifier(identifier);
        newWarehouse.setName("Test Warehouse");
        newWarehouse.setBusinessUnitCode("BU-001");
        newWarehouse.setLocationIdentifier("LOC-001");
        newWarehouse.setCapacity(1000);
        newWarehouse.setCurrentStock(0);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.replace(newWarehouse));

        assertEquals("Warehouse not found: ", exception.getMessage());
        verify(warehouseStore, never()).update(any(Warehouse.class));
    }

    @Test
    void replace_ShouldThrowException_WhenBusinessUnitChangedAndNotUnique() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, true);

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-002", "LOC-001", 1500, 500, false);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(businessUnitValidator.isBusinessUnitCodeUnique("BU-002")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.replace(newWarehouse));

        assertTrue(exception.getMessage().contains("Business unit code already exists: BU-002"));
        verify(warehouseStore, never()).update(any(Warehouse.class));
    }

    @Test
    void replace_ShouldSucceed_WhenBusinessUnitChangedAndIsUnique() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, true);

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-002", "LOC-001", 1500, 500, false);

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(businessUnitValidator.isBusinessUnitCodeUnique("BU-002")).thenReturn(true);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void replace_ShouldNotCheckBusinessUnit_WhenBusinessUnitNotChanged() {
        // Given
        String identifier = "WH-001";
        String businessUnitCode = "BU-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                businessUnitCode, "LOC-001", 1000, 500, true);

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                businessUnitCode, "LOC-001", 1500, 500, false);

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        verify(businessUnitValidator, never()).isBusinessUnitCodeUnique(anyString());
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void replace_ShouldThrowException_WhenLocationInvalid() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, true);

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-001", 1500, 500, false);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.replace(newWarehouse));

        assertTrue(exception.getMessage().contains("Invalid location identifier: LOC-001"));
        verify(warehouseStore, never()).update(any(Warehouse.class));
    }

    @Test
    void replace_ShouldThrowException_WhenCapacityExceedsLocationMax() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, true);

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-001", 3000, 500, false);

        Location location = createLocation(2000); // Max capacity is 2000

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.replace(newWarehouse));

        assertTrue(exception.getMessage().contains("Warehouse capacity exceeds maximum capacity"));
        verify(warehouseStore, never()).update(any(Warehouse.class));
    }

    @Test
    void replace_ShouldSucceed_WhenDifferentValidLocation() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, true);

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-002", 1500, 500, false); // Different location

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-002")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void replace_ShouldSetActiveAndNotArchived() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, true);

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-001", 1500, 500, false);

        // Set new warehouse to inactive initially
        newWarehouse.setActive(false);
        newWarehouse.setArchived(true);

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        assertTrue(newWarehouse.isActive());
        assertFalse(newWarehouse.isArchived());
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void replace_ShouldPreserveExistingId() {
        // Given
        String identifier = "WH-001";
        Long existingId = 42L;
        Warehouse existing = createWarehouse(existingId, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, true);

        Warehouse newWarehouse = createWarehouse(999L, identifier, "New Warehouse", // Different ID initially
                "BU-001", "LOC-001", 1500, 500, false);

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        assertEquals(existingId, newWarehouse.getId());
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void replace_ShouldSucceed_WithMaximumAllowedCapacity() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, true);

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-001", 2000, 500, false); // Exactly matches max capacity

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void replace_ShouldSucceed_WhenExistingWarehouseIsInactive() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, false); // Inactive

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-001", 1500, 500, false);

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        assertTrue(newWarehouse.isActive()); // Should become active
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void replace_ShouldSucceed_WhenExistingWarehouseIsArchived() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, true);
        existing.setArchived(true); // Archived warehouse

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-001", 1500, 500, false);

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        assertFalse(newWarehouse.isArchived()); // Should become not archived
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void replace_ShouldSucceed_WithZeroCurrentStock() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 0, true); // Zero stock

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-001", 1500, 0, false); // Zero stock

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void replace_ShouldSucceed_WithMinimumCapacity() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 100, true);

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-001", 100, 100, false); // Minimum capacity equals stock

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        verify(warehouseStore).update(newWarehouse);
    }

    @Test
    void replace_ShouldMaintainOtherFields() {
        // Given
        String identifier = "WH-001";
        Warehouse existing = createWarehouse(1L, identifier, "Old Warehouse",
                "BU-001", "LOC-001", 1000, 500, true);

        Warehouse newWarehouse = createWarehouse(null, identifier, "New Warehouse",
                "BU-001", "LOC-001", 1500, 500, false);

        // Save original values
        String originalName = newWarehouse.getName();
        String originalBusinessUnit = newWarehouse.getBusinessUnitCode();
        String originalLocation = newWarehouse.getLocationIdentifier();
        int originalCapacity = newWarehouse.getCapacity();
        int originalStock = newWarehouse.getCurrentStock();

        Location location = createLocation(2000);

        when(warehouseStore.findByIdentifier(identifier)).thenReturn(existing);
        when(locationResolver.resolveByIdentifier("LOC-001")).thenReturn(location);

        // When
        useCase.replace(newWarehouse);

        // Then
        assertEquals(originalName, newWarehouse.getName());
        assertEquals(originalBusinessUnit, newWarehouse.getBusinessUnitCode());
        assertEquals(originalLocation, newWarehouse.getLocationIdentifier());
        assertEquals(originalCapacity, newWarehouse.getCapacity());
        assertEquals(originalStock, newWarehouse.getCurrentStock());
        verify(warehouseStore).update(newWarehouse);
    }
}