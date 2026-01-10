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
class CreateWarehouseUseCaseTest {

    @Mock private WarehouseStore warehouseStore;
    @Mock private LocationResolver locationResolver;
    @Mock private BusinessUnitValidator businessUnitValidator;
    @Mock private WarehouseCapacityValidator capacityValidator;

    private CreateWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateWarehouseUseCase(warehouseStore, locationResolver,
                businessUnitValidator, capacityValidator);
    }

    private Warehouse validWarehouse() {
        return Warehouse.builder()
                .name("Main Warehouse")
                .businessUnitCode("BU001")
                .locationIdentifier("LOC001")
                .capacity(1000)
                .currentStock(0)
                .build();
    }

    private Location validLocation() {
        Location location = new Location();
        location.setMaxCapacity(2000);
        return location;
    }

    // Helper to mock all successful validations except one
    private void mockAllValidationsExcept(Runnable exceptionSetup) {
        when(businessUnitValidator.isBusinessUnitCodeUnique("BU001")).thenReturn(true);
        when(locationResolver.resolveByIdentifier("LOC001")).thenReturn(validLocation());
        when(warehouseStore.countWarehousesAtLocation("LOC001")).thenReturn(2);
        when(capacityValidator.canCreateWarehouseAtLocation("LOC001", 2)).thenReturn(true);
        when(capacityValidator.hasSufficientCapacity("LOC001", 1000)).thenReturn(true);

        if (exceptionSetup != null) {
            exceptionSetup.run();
        }
    }

    @Test
    void create_Success_WhenAllValidationsPass() {
        // Given
        Warehouse warehouse = validWarehouse();
        mockAllValidationsExcept(null);

        // When
        useCase.create(warehouse);

        // Then
        assertTrue(warehouse.isActive());
        assertFalse(warehouse.isArchived());
        verify(warehouseStore).create(warehouse);
    }

    @Test
    void create_ThrowsException_WhenBusinessUnitNotUnique() {
        // Given
        Warehouse warehouse = validWarehouse();

        when(businessUnitValidator.isBusinessUnitCodeUnique("BU001")).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.create(warehouse));

        assertTrue(exception.getMessage().contains("Business unit code already exists"));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void create_ThrowsException_WhenLocationInvalid() {
        // Given
        Warehouse warehouse = validWarehouse();

        when(businessUnitValidator.isBusinessUnitCodeUnique("BU001")).thenReturn(true);
        when(locationResolver.resolveByIdentifier("LOC001")).thenReturn(null);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.create(warehouse));

        assertTrue(exception.getMessage().contains("Invalid location identifier"));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void create_ThrowsException_WhenMaxWarehousesReached() {
        // Given
        Warehouse warehouse = validWarehouse();

        when(businessUnitValidator.isBusinessUnitCodeUnique("BU001")).thenReturn(true);
        when(locationResolver.resolveByIdentifier("LOC001")).thenReturn(validLocation());
        when(warehouseStore.countWarehousesAtLocation("LOC001")).thenReturn(5);
        when(capacityValidator.canCreateWarehouseAtLocation("LOC001", 5)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.create(warehouse));

        assertTrue(exception.getMessage().contains("Maximum number of warehouses reached"));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void create_ThrowsException_WhenCapacityExceedsLocationMax() {
        // Given
        Warehouse warehouse = validWarehouse();
        warehouse.setCapacity(3000); // Exceeds location max capacity
        Location location = validLocation(); // Max capacity is 2000

        when(businessUnitValidator.isBusinessUnitCodeUnique("BU001")).thenReturn(true);
        when(locationResolver.resolveByIdentifier("LOC001")).thenReturn(location);
        when(warehouseStore.countWarehousesAtLocation("LOC001")).thenReturn(2);
        when(capacityValidator.canCreateWarehouseAtLocation("LOC001", 2)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.create(warehouse));

        assertTrue(exception.getMessage().contains("Warehouse capacity exceeds maximum capacity"));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void create_ThrowsException_WhenStockExceedsCapacity() {
        // Given
        Warehouse warehouse = validWarehouse();
        warehouse.setCurrentStock(1500); // Exceeds capacity of 1000

        when(businessUnitValidator.isBusinessUnitCodeUnique("BU001")).thenReturn(true);
        when(locationResolver.resolveByIdentifier("LOC001")).thenReturn(validLocation());
        when(warehouseStore.countWarehousesAtLocation("LOC001")).thenReturn(2);
        when(capacityValidator.canCreateWarehouseAtLocation("LOC001", 2)).thenReturn(true);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.create(warehouse));

        assertTrue(exception.getMessage().contains("Current stock exceeds warehouse capacity"));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void create_ThrowsException_WhenInsufficientLocationCapacity() {
        // Given
        Warehouse warehouse = validWarehouse();

        when(businessUnitValidator.isBusinessUnitCodeUnique("BU001")).thenReturn(true);
        when(locationResolver.resolveByIdentifier("LOC001")).thenReturn(validLocation());
        when(warehouseStore.countWarehousesAtLocation("LOC001")).thenReturn(2);
        when(capacityValidator.canCreateWarehouseAtLocation("LOC001", 2)).thenReturn(true);
        when(capacityValidator.hasSufficientCapacity("LOC001", 1000)).thenReturn(false);

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> useCase.create(warehouse));

        assertTrue(exception.getMessage().contains("Insufficient capacity at location"));
        verify(warehouseStore, never()).create(any());
    }

    @Test
    void create_Success_WhenWarehouseHasStockWithinCapacity() {
        // Given
        Warehouse warehouse = validWarehouse();
        warehouse.setCurrentStock(500);

        mockAllValidationsExcept(null);

        // When
        useCase.create(warehouse);

        // Then
        verify(warehouseStore).create(warehouse);
    }

    @Test
    void create_SetsActiveAndNotArchived() {
        // Given
        Warehouse warehouse = validWarehouse();
        mockAllValidationsExcept(null);

        // When
        useCase.create(warehouse);

        // Then
        assertTrue(warehouse.isActive());
        assertFalse(warehouse.isArchived());
        verify(warehouseStore).create(warehouse);
    }

    @Test
    void create_Success_WithMaximumAllowedCapacity() {
        // Given
        Warehouse warehouse = validWarehouse();
        warehouse.setCapacity(2000); // Exactly matches location max capacity
        Location location = validLocation(); // Max capacity is 2000

        when(businessUnitValidator.isBusinessUnitCodeUnique("BU001")).thenReturn(true);
        when(locationResolver.resolveByIdentifier("LOC001")).thenReturn(location);
        when(warehouseStore.countWarehousesAtLocation("LOC001")).thenReturn(1);
        when(capacityValidator.canCreateWarehouseAtLocation("LOC001", 1)).thenReturn(true);
        when(capacityValidator.hasSufficientCapacity("LOC001", 2000)).thenReturn(true);

        // When
        useCase.create(warehouse);

        // Then
        verify(warehouseStore).create(warehouse);
    }
}