package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArchiveWarehouseUseCaseTest {

    @Mock
    private WarehouseStore warehouseStore;

    private ArchiveWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ArchiveWarehouseUseCase(warehouseStore);
    }

    // Helper methods
    private Warehouse paramWarehouse(String identifier) {
        Warehouse warehouse = new Warehouse();
        warehouse.setIdentifier(identifier);
        return warehouse;
    }

    private Warehouse storedWarehouse(String identifier, int currentStock, boolean active, boolean archived) {
        return Warehouse.builder()
                .identifier(identifier)
                .name("Warehouse " + identifier)
                .businessUnitCode("BU-" + identifier)
                .locationIdentifier("LOC-" + identifier)
                .capacity(1000)
                .currentStock(currentStock)
                .active(active)
                .archived(archived)
                .build();
    }

    @Test
    void archive_Success_WhenConditionsMet() {
        // Given
        String id = "WH-001";
        Warehouse param = paramWarehouse(id);
        Warehouse stored = storedWarehouse(id, 0, true, false);

        when(warehouseStore.findByIdentifier(id)).thenReturn(stored);

        // When
        useCase.archive(param);

        // Then
        verify(warehouseStore).findByIdentifier(id);
        verify(warehouseStore).update(argThat(w -> w.isArchived() && !w.isActive()));
    }

    @Test
    void archive_ThrowsException_WhenWarehouseNotFound() {
        // Given
        String id = "NON_EXISTENT";
        Warehouse param = paramWarehouse(id);

        when(warehouseStore.findByIdentifier(id)).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> useCase.archive(param),
                "Warehouse not found: " + id);

        verify(warehouseStore, never()).update(any());
    }

    @Test
    void archive_ThrowsException_WhenWarehouseHasStock() {
        // Given
        String id = "WH-002";
        Warehouse param = paramWarehouse(id);
        Warehouse stored = storedWarehouse(id, 100, true, false);

        when(warehouseStore.findByIdentifier(id)).thenReturn(stored);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> useCase.archive(param));

        assertTrue(exception.getMessage().contains("Cannot archive warehouse with existing stock"));
        assertTrue(exception.getMessage().contains("Current stock: 100"));
        verify(warehouseStore, never()).update(any());
    }

    @Test
    void archive_ThrowsException_WhenWarehouseAlreadyArchived() {
        // Given
        String id = "WH-003";
        Warehouse param = paramWarehouse(id);
        Warehouse stored = storedWarehouse(id, 0, false, true);

        when(warehouseStore.findByIdentifier(id)).thenReturn(stored);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> useCase.archive(param));

        assertEquals("Warehouse is already archived: " + id, exception.getMessage());
        verify(warehouseStore, never()).update(any());
    }

    @Test
    void archive_ThrowsException_WhenInactiveWarehouseHasStock() {
        // Given
        String id = "WH-004";
        Warehouse param = paramWarehouse(id);
        Warehouse stored = storedWarehouse(id, 50, false, false);

        when(warehouseStore.findByIdentifier(id)).thenReturn(stored);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> useCase.archive(param));

        assertTrue(exception.getMessage().contains("Cannot archive warehouse with existing stock"));
        verify(warehouseStore, never()).update(any());
    }

    @Test
    void archive_Success_WhenInactiveWithNoStock() {
        // Given
        String id = "WH-005";
        Warehouse param = paramWarehouse(id);
        Warehouse stored = storedWarehouse(id, 0, false, false);

        when(warehouseStore.findByIdentifier(id)).thenReturn(stored);

        // When
        useCase.archive(param);

        // Then
        verify(warehouseStore).update(argThat(w -> w.isArchived() && !w.isActive()));
    }

    @Test
    void archive_HandlesNullIdentifier() {
        // Given
        Warehouse param = paramWarehouse(null);

        when(warehouseStore.findByIdentifier(isNull())).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> useCase.archive(param),
                "Warehouse not found: null");

        verify(warehouseStore, never()).update(any());
    }

    @Test
    void archive_HandlesEmptyIdentifier() {
        // Given
        String id = "";
        Warehouse param = paramWarehouse(id);

        when(warehouseStore.findByIdentifier(id)).thenReturn(null);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> useCase.archive(param),
                "Warehouse not found: ");

        verify(warehouseStore, never()).update(any());
    }

    @Test
    void archive_OnlyUpdatesFlags_NotOtherProperties() {
        // Given
        String id = "WH-009";
        Warehouse param = paramWarehouse(id);
        Warehouse stored = storedWarehouse(id, 0, true, false);

        when(warehouseStore.findByIdentifier(id)).thenReturn(stored);

        // When
        useCase.archive(param);

        // Then
        assertTrue(stored.isArchived());
        assertFalse(stored.isActive());
        assertEquals("Warehouse " + id, stored.getName());
        assertEquals("BU-" + id, stored.getBusinessUnitCode());
        assertEquals("LOC-" + id, stored.getLocationIdentifier());
        assertEquals(1000, stored.getCapacity());
        assertEquals(0, stored.getCurrentStock());

        verify(warehouseStore).update(stored);
    }

    @Test
    void archive_Success_WithZeroCapacity() {
        // Given
        String id = "WH-010";
        Warehouse param = paramWarehouse(id);
        Warehouse stored = Warehouse.builder()
                .identifier(id)
                .name("Zero Capacity")
                .businessUnitCode("BU10")
                .locationIdentifier("LOC10")
                .capacity(0)
                .currentStock(0)
                .active(true)
                .archived(false)
                .build();

        when(warehouseStore.findByIdentifier(id)).thenReturn(stored);

        // When
        useCase.archive(param);

        // Then
        verify(warehouseStore).update(argThat(w -> w.isArchived() && !w.isActive()));
    }
}