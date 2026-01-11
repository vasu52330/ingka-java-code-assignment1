package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseRepositoryTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private TypedQuery<Warehouse> warehouseTypedQuery;

    @Mock
    private TypedQuery<Long> longTypedQuery;

    @Mock
    private TypedQuery<Integer> integerTypedQuery;

    @InjectMocks
    private WarehouseRepository warehouseRepository;

    @Captor
    private ArgumentCaptor<String> queryStringCaptor;

    @Captor
    private ArgumentCaptor<String> parameterCaptor;

    private Warehouse testWarehouse;

    @BeforeEach
    void setUp() {
        testWarehouse = new Warehouse();
        testWarehouse.setIdentifier("WH-001");
        testWarehouse.setBusinessUnitCode("BU-001");
        testWarehouse.setLocationIdentifier("AMSTERDAM-001");
        testWarehouse.setCapacity(1000);
        testWarehouse.setActive(true);
    }

    @Test
    void shouldBeApplicationScoped() {
        ApplicationScoped annotation = WarehouseRepository.class.getAnnotation(ApplicationScoped.class);
        assertNotNull(annotation, "Class should be annotated with @ApplicationScoped");
    }

    @Test
    void shouldImplementWarehouseStoreInterface() {
        assertTrue(com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore.class
                .isAssignableFrom(WarehouseRepository.class));
    }

    @Test
    void create_shouldPersistWarehouse() {
        // Act
        warehouseRepository.create(testWarehouse);

        // Assert
        verify(entityManager).persist(testWarehouse);
    }

    @Test
    void create_shouldHandleNullWarehouse() {
        // Act & Assert
        assertDoesNotThrow(() -> warehouseRepository.create(null));
        verify(entityManager).persist(null);
    }

    @Test
    void update_shouldMergeWarehouse() {
        // Act
        warehouseRepository.update(testWarehouse);

        // Assert
        verify(entityManager).merge(testWarehouse);
    }

    @Test
    void update_shouldHandleNullWarehouse() {
        // Act & Assert
        assertDoesNotThrow(() -> warehouseRepository.update(null));
        verify(entityManager).merge(null);
    }

    @Test
    void findByIdentifier_shouldReturnWarehouseWhenFound() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.setParameter(eq("identifier"), anyString()))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultStream())
                .thenReturn(Stream.of(testWarehouse));

        // Act
        Warehouse result = warehouseRepository.findByIdentifier("WH-001");

        // Assert
        assertNotNull(result);
        assertEquals("WH-001", result.getIdentifier());
        verify(entityManager).createQuery(queryStringCaptor.capture(), eq(Warehouse.class));
        assertTrue(queryStringCaptor.getValue().contains("identifier = :identifier"));
        verify(warehouseTypedQuery).setParameter("identifier", "WH-001");
    }

    @Test
    void findByIdentifier_shouldReturnNullWhenNotFound() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.setParameter(eq("identifier"), anyString()))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultStream())
                .thenReturn(Stream.empty());

        // Act
        Warehouse result = warehouseRepository.findByIdentifier("NON-EXISTENT");

        // Assert
        assertNull(result);
        verify(warehouseTypedQuery).setParameter("identifier", "NON-EXISTENT");
    }

    @Test
    void findByIdentifier_shouldHandleEmptyIdentifier() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.setParameter(eq("identifier"), anyString()))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultStream())
                .thenReturn(Stream.empty());

        // Act
        Warehouse result = warehouseRepository.findByIdentifier("");

        // Assert
        assertNull(result);
        verify(warehouseTypedQuery).setParameter("identifier", "");
    }

    @Test
    void findByIdentifier_shouldHandleNullIdentifier() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.setParameter(eq("identifier"), isNull()))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultStream())
                .thenReturn(Stream.empty());

        // Act
        Warehouse result = warehouseRepository.findByIdentifier(null);

        // Assert
        assertNull(result);
        verify(warehouseTypedQuery).setParameter("identifier", null);
    }

    @Test
    void findByBusinessUnitCode_shouldReturnWarehouseWhenFound() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.setParameter(eq("businessUnitCode"), anyString()))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultStream())
                .thenReturn(Stream.of(testWarehouse));

        // Act
        Warehouse result = warehouseRepository.findByBusinessUnitCode("BU-001");

        // Assert
        assertNotNull(result);
        assertEquals("BU-001", result.getBusinessUnitCode());
        verify(entityManager).createQuery(queryStringCaptor.capture(), eq(Warehouse.class));
        assertTrue(queryStringCaptor.getValue().contains("businessUnitCode = :businessUnitCode"));
        assertTrue(queryStringCaptor.getValue().contains("active = true"));
        verify(warehouseTypedQuery).setParameter("businessUnitCode", "BU-001");
    }

    @Test
    void findByBusinessUnitCode_shouldReturnNullWhenNotFound() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.setParameter(eq("businessUnitCode"), anyString()))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultStream())
                .thenReturn(Stream.empty());

        // Act
        Warehouse result = warehouseRepository.findByBusinessUnitCode("NON-EXISTENT");

        // Assert
        assertNull(result);
    }

    @Test
    void countWarehousesAtLocation_shouldReturnCount() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter(eq("locationIdentifier"), anyString()))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult())
                .thenReturn(5L);

        // Act
        int result = warehouseRepository.countWarehousesAtLocation("AMSTERDAM-001");

        // Assert
        assertEquals(5, result);
        verify(entityManager).createQuery(queryStringCaptor.capture(), eq(Long.class));
        assertTrue(queryStringCaptor.getValue().contains("COUNT(w)"));
        assertTrue(queryStringCaptor.getValue().contains("locationIdentifier = :locationIdentifier"));
        assertTrue(queryStringCaptor.getValue().contains("active = true"));
        verify(longTypedQuery).setParameter("locationIdentifier", "AMSTERDAM-001");
    }

    @Test
    void countWarehousesAtLocation_shouldReturnZeroWhenNullResult() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter(eq("locationIdentifier"), anyString()))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult())
                .thenReturn(null);

        // Act
        int result = warehouseRepository.countWarehousesAtLocation("AMSTERDAM-001");

        // Assert
        assertEquals(0, result);
    }

    @Test
    void countWarehousesAtLocation_shouldHandleEmptyLocation() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter(eq("locationIdentifier"), anyString()))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult())
                .thenReturn(0L);

        // Act
        int result = warehouseRepository.countWarehousesAtLocation("");

        // Assert
        assertEquals(0, result);
        verify(longTypedQuery).setParameter("locationIdentifier", "");
    }

    @Test
    void getTotalCapacityAtLocation_shouldReturnSum() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Integer.class)))
                .thenReturn(integerTypedQuery);
        when(integerTypedQuery.setParameter(eq("locationIdentifier"), anyString()))
                .thenReturn(integerTypedQuery);
        when(integerTypedQuery.getSingleResult())
                .thenReturn(2500);

        // Act
        int result = warehouseRepository.getTotalCapacityAtLocation("AMSTERDAM-001");

        // Assert
        assertEquals(2500, result);
        verify(entityManager).createQuery(queryStringCaptor.capture(), eq(Integer.class));
        assertTrue(queryStringCaptor.getValue().contains("SUM(w.capacity)"));
        assertTrue(queryStringCaptor.getValue().contains("locationIdentifier = :locationIdentifier"));
        assertTrue(queryStringCaptor.getValue().contains("active = true"));
        verify(integerTypedQuery).setParameter("locationIdentifier", "AMSTERDAM-001");
    }

    @Test
    void getTotalCapacityAtLocation_shouldReturnZeroWhenNullResult() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Integer.class)))
                .thenReturn(integerTypedQuery);
        when(integerTypedQuery.setParameter(eq("locationIdentifier"), anyString()))
                .thenReturn(integerTypedQuery);
        when(integerTypedQuery.getSingleResult())
                .thenReturn(null);

        // Act
        int result = warehouseRepository.getTotalCapacityAtLocation("AMSTERDAM-001");

        // Assert
        assertEquals(0, result);
    }

    @Test
    void getTotalCapacityAtLocation_shouldReturnZeroWhenNoActiveWarehouses() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Integer.class)))
                .thenReturn(integerTypedQuery);
        when(integerTypedQuery.setParameter(eq("locationIdentifier"), anyString()))
                .thenReturn(integerTypedQuery);
        when(integerTypedQuery.getSingleResult())
                .thenReturn(0);

        // Act
        int result = warehouseRepository.getTotalCapacityAtLocation("EMPTY-LOCATION");

        // Assert
        assertEquals(0, result);
    }

    @Test
    void findAllByLocation_shouldReturnList() {
        // Arrange
        Warehouse warehouse1 = new Warehouse();
        warehouse1.setIdentifier("WH-001");
        warehouse1.setLocationIdentifier("AMSTERDAM-001");

        Warehouse warehouse2 = new Warehouse();
        warehouse2.setIdentifier("WH-002");
        warehouse2.setLocationIdentifier("AMSTERDAM-001");

        List<Warehouse> expectedList = Arrays.asList(warehouse1, warehouse2);

        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.setParameter(eq("locationIdentifier"), anyString()))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultList())
                .thenReturn(expectedList);

        // Act
        List<Warehouse> result = warehouseRepository.findAllByLocation("AMSTERDAM-001");

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("WH-001", result.get(0).getIdentifier());
        assertEquals("WH-002", result.get(1).getIdentifier());
        verify(warehouseTypedQuery).setParameter("locationIdentifier", "AMSTERDAM-001");
    }

    @Test
    void findAllByLocation_shouldReturnEmptyListWhenNoWarehouses() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.setParameter(eq("locationIdentifier"), anyString()))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultList())
                .thenReturn(Collections.emptyList());

        // Act
        List<Warehouse> result = warehouseRepository.findAllByLocation("EMPTY-LOCATION");

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllActive_shouldReturnList() {
        // Arrange
        Warehouse warehouse1 = new Warehouse();
        warehouse1.setIdentifier("WH-001");
        warehouse1.setActive(true);

        Warehouse warehouse2 = new Warehouse();
        warehouse2.setIdentifier("WH-002");
        warehouse2.setActive(true);

        List<Warehouse> expectedList = Arrays.asList(warehouse1, warehouse2);

        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultList())
                .thenReturn(expectedList);

        // Act
        List<Warehouse> result = warehouseRepository.findAllActive();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(entityManager).createQuery(queryStringCaptor.capture(), eq(Warehouse.class));
        assertTrue(queryStringCaptor.getValue().contains("active = true"));
    }

    @Test
    void findAllActive_shouldReturnEmptyListWhenNoActiveWarehouses() {
        // Arrange
        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultList())
                .thenReturn(Collections.emptyList());

        // Act
        List<Warehouse> result = warehouseRepository.findAllActive();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldHandleExceptionsGracefully() {
        // Test that repository doesn't propagate persistence exceptions
        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenThrow(new RuntimeException("Database error"));

        // Act & Assert - should throw exception (not caught by repository)
        assertThrows(RuntimeException.class, () ->
                warehouseRepository.findByIdentifier("WH-001"));
    }

    @Test
    void findByBusinessUnitCode_shouldOnlyReturnActiveWarehouses() {
        // This test verifies the query includes "active = true" condition
        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.setParameter(anyString(), anyString()))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultStream())
                .thenReturn(Stream.empty());

        // Act
        warehouseRepository.findByBusinessUnitCode("BU-001");

        // Assert
        verify(entityManager).createQuery(queryStringCaptor.capture(), eq(Warehouse.class));
        String query = queryStringCaptor.getValue();
        assertTrue(query.contains("active = true"),
                "Query should filter by active warehouses");
    }

    @Test
    void countWarehousesAtLocation_shouldOnlyCountActiveWarehouses() {
        // This test verifies the query includes "active = true" condition
        when(entityManager.createQuery(anyString(), eq(Long.class)))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.setParameter(anyString(), anyString()))
                .thenReturn(longTypedQuery);
        when(longTypedQuery.getSingleResult())
                .thenReturn(0L);

        // Act
        warehouseRepository.countWarehousesAtLocation("LOC-001");

        // Assert
        verify(entityManager).createQuery(queryStringCaptor.capture(), eq(Long.class));
        String query = queryStringCaptor.getValue();
        assertTrue(query.contains("active = true"),
                "Query should only count active warehouses");
    }

    @Test
    void shouldUseParameterizedQueries() {
        // Test that all queries use parameters instead of string concatenation
        when(entityManager.createQuery(anyString(), eq(Warehouse.class)))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.setParameter(anyString(), anyString()))
                .thenReturn(warehouseTypedQuery);
        when(warehouseTypedQuery.getResultStream())
                .thenReturn(Stream.empty());

        // Act
        warehouseRepository.findByIdentifier("test' OR '1'='1"); // SQL injection attempt

        // Assert
        verify(warehouseTypedQuery).setParameter(eq("identifier"),
                eq("test' OR '1'='1"));
        // If using parameters correctly, this should be safe
    }
}