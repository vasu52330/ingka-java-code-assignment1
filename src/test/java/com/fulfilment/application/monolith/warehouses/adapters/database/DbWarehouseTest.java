package com.fulfilment.application.monolith.warehouses.adapters.database;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class DbWarehouseTest {

    @Test
    void shouldCreateDefaultConstructorInstance() {
        // Act
        DbWarehouse warehouse = new DbWarehouse();

        // Assert
        assertNotNull(warehouse);
        assertNull(warehouse.id);
        assertNull(warehouse.businessUnitCode);
        assertNull(warehouse.location);
        assertNull(warehouse.capacity);
        assertNull(warehouse.stock);
        assertNull(warehouse.createdAt);
        assertNull(warehouse.archivedAt);
    }

    @Test
    void shouldSetAndGetAllFields() {
        // Arrange
        DbWarehouse warehouse = new DbWarehouse();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime archivedTime = LocalDateTime.now().minusDays(1);

        // Act
        warehouse.id = 1L;
        warehouse.businessUnitCode = "BU-001";
        warehouse.location = "AMSTERDAM-001";
        warehouse.capacity = 1000;
        warehouse.stock = 500;
        warehouse.createdAt = now;
        warehouse.archivedAt = archivedTime;

        // Assert
        assertEquals(1L, warehouse.id);
        assertEquals("BU-001", warehouse.businessUnitCode);
        assertEquals("AMSTERDAM-001", warehouse.location);
        assertEquals(1000, warehouse.capacity);
        assertEquals(500, warehouse.stock);
        assertEquals(now, warehouse.createdAt);
        assertEquals(archivedTime, warehouse.archivedAt);
    }

    @Test
    void shouldBeEntityAnnotated() {
        // Act
        jakarta.persistence.Entity annotation = DbWarehouse.class.getAnnotation(jakarta.persistence.Entity.class);

        // Assert
        assertNotNull(annotation, "DbWarehouse class should be annotated with @Entity");
    }

    @Test
    void shouldHaveTableAnnotationWithCorrectName() {
        // Act
        jakarta.persistence.Table tableAnnotation = DbWarehouse.class.getAnnotation(jakarta.persistence.Table.class);

        // Assert
        assertNotNull(tableAnnotation, "DbWarehouse class should be annotated with @Table");
        assertEquals("warehouse", tableAnnotation.name(), "Table name should be 'warehouse'");
    }

    @Test
    void shouldBeCacheable() {
        // Act
        jakarta.persistence.Cacheable cacheableAnnotation = DbWarehouse.class.getAnnotation(jakarta.persistence.Cacheable.class);

        // Assert
        assertNotNull(cacheableAnnotation, "DbWarehouse class should be annotated with @Cacheable");
    }

    @Test
    void shouldHaveIdFieldWithGeneratedValue() throws NoSuchFieldException {
        // Arrange
        Field idField = DbWarehouse.class.getDeclaredField("id");

        // Act
        jakarta.persistence.Id idAnnotation = idField.getAnnotation(jakarta.persistence.Id.class);
        jakarta.persistence.GeneratedValue generatedValueAnnotation = idField.getAnnotation(jakarta.persistence.GeneratedValue.class);

        // Assert
        assertNotNull(idAnnotation, "id field should be annotated with @Id");
        assertNotNull(generatedValueAnnotation, "id field should be annotated with @GeneratedValue");
    }

    @Test
    void shouldHavePublicFields() throws NoSuchFieldException {
        // Verify all fields are public (as shown in the code)
        String[] fieldNames = {"id", "businessUnitCode", "location", "capacity", "stock", "createdAt", "archivedAt"};

        for (String fieldName : fieldNames) {
            Field field = DbWarehouse.class.getDeclaredField(fieldName);
            assertTrue(java.lang.reflect.Modifier.isPublic(field.getModifiers()),
                    "Field '" + fieldName + "' should be public");
        }
    }

    @Test
    void shouldHandleNullValuesGracefully() {
        // Arrange
        DbWarehouse warehouse = new DbWarehouse();

        // Act - set some fields to null
        warehouse.id = null;
        warehouse.businessUnitCode = null;
        warehouse.location = null;
        warehouse.capacity = null;
        warehouse.stock = null;
        warehouse.createdAt = null;
        warehouse.archivedAt = null;

        // Assert
        assertNull(warehouse.id);
        assertNull(warehouse.businessUnitCode);
        assertNull(warehouse.location);
        assertNull(warehouse.capacity);
        assertNull(warehouse.stock);
        assertNull(warehouse.createdAt);
        assertNull(warehouse.archivedAt);
    }

    @Test
    void shouldHandleEmptyStringValues() {
        // Arrange
        DbWarehouse warehouse = new DbWarehouse();

        // Act
        warehouse.businessUnitCode = "";
        warehouse.location = "";

        // Assert
        assertEquals("", warehouse.businessUnitCode);
        assertEquals("", warehouse.location);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, -1, -100, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void shouldHandleVariousCapacityValues(int capacity) {
        // Arrange
        DbWarehouse warehouse = new DbWarehouse();

        // Act
        warehouse.capacity = capacity;

        // Assert
        assertEquals(capacity, warehouse.capacity);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, -1, -100, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void shouldHandleVariousStockValues(int stock) {
        // Arrange
        DbWarehouse warehouse = new DbWarehouse();

        // Act
        warehouse.stock = stock;

        // Assert
        assertEquals(stock, warehouse.stock);
    }

    @Test
    void shouldHandleFutureAndPastDates() {
        // Arrange
        DbWarehouse warehouse = new DbWarehouse();
        LocalDateTime past = LocalDateTime.now().minusYears(1);
        LocalDateTime future = LocalDateTime.now().plusYears(1);

        // Act
        warehouse.createdAt = past;
        warehouse.archivedAt = future;

        // Assert
        assertEquals(past, warehouse.createdAt);
        assertEquals(future, warehouse.archivedAt);
    }

    @Test
    void shouldAllowArchivedAtBeforeCreatedAt() {
        // This might be a business logic constraint, but the entity itself should allow it
        DbWarehouse warehouse = new DbWarehouse();
        LocalDateTime createdAt = LocalDateTime.now();
        LocalDateTime archivedAt = createdAt.minusDays(1); // Archived before creation

        warehouse.createdAt = createdAt;
        warehouse.archivedAt = archivedAt;

        assertEquals(createdAt, warehouse.createdAt);
        assertEquals(archivedAt, warehouse.archivedAt);
    }

    @Test
    void shouldCreateMultipleInstancesWithDifferentValues() {
        // Arrange
        DbWarehouse warehouse1 = new DbWarehouse();
        DbWarehouse warehouse2 = new DbWarehouse();

        LocalDateTime time1 = LocalDateTime.now();
        LocalDateTime time2 = LocalDateTime.now().plusHours(1);

        // Act
        warehouse1.id = 1L;
        warehouse1.businessUnitCode = "BU-001";
        warehouse1.location = "LOC-001";
        warehouse1.capacity = 100;
        warehouse1.stock = 50;
        warehouse1.createdAt = time1;
        warehouse1.archivedAt = null;

        warehouse2.id = 2L;
        warehouse2.businessUnitCode = "BU-002";
        warehouse2.location = "LOC-002";
        warehouse2.capacity = 200;
        warehouse2.stock = 100;
        warehouse2.createdAt = time2;
        warehouse2.archivedAt = time2.plusDays(30);

        // Assert
        assertNotEquals(warehouse1.id, warehouse2.id);
        assertNotEquals(warehouse1.businessUnitCode, warehouse2.businessUnitCode);
        assertNotEquals(warehouse1.location, warehouse2.location);
        assertNotEquals(warehouse1.capacity, warehouse2.capacity);
        assertNotEquals(warehouse1.stock, warehouse2.stock);
        assertNotEquals(warehouse1.createdAt, warehouse2.createdAt);
        assertNotEquals(warehouse1.archivedAt, warehouse2.archivedAt);
    }

    @Test
    void shouldHaveDefaultToString() {
        // Since toString() is not overridden, test default behavior
        DbWarehouse warehouse = new DbWarehouse();
        warehouse.id = 1L;
        warehouse.businessUnitCode = "TEST";

        // Act
        String toStringResult = warehouse.toString();

        // Assert
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("DbWarehouse"));
    }

    @Test
    void shouldNotImplementSerializable() {
        // Check if class implements Serializable (common for JPA entities but not required)
        boolean isSerializable = java.io.Serializable.class.isAssignableFrom(DbWarehouse.class);

        // The current DbWarehouse class doesn't implement Serializable
        // This test documents that fact
        assertFalse(isSerializable, "DbWarehouse class doesn't implement Serializable");
    }

    @Test
    void shouldAllowNullIdForNewEntities() {
        // New entities (not yet persisted) should have null id
        DbWarehouse newWarehouse = new DbWarehouse();
        newWarehouse.businessUnitCode = "NEW-BU";
        newWarehouse.location = "NEW-LOC";

        assertNull(newWarehouse.id);
    }

    @Test
    void shouldHandleWhitespaceInStringFields() {
        DbWarehouse warehouse = new DbWarehouse();

        warehouse.businessUnitCode = "  BU-001  ";
        warehouse.location = "\tAMSTERDAM-001\n";

        assertEquals("  BU-001  ", warehouse.businessUnitCode);
        assertEquals("\tAMSTERDAM-001\n", warehouse.location);
    }

    @Test
    void shouldBeUsableInCollections() {
        // Test that DbWarehouse can be used in standard Java collections
        DbWarehouse warehouse1 = new DbWarehouse();
        warehouse1.id = 1L;
        warehouse1.businessUnitCode = "BU-001";

        DbWarehouse warehouse2 = new DbWarehouse();
        warehouse2.id = 2L;
        warehouse2.businessUnitCode = "BU-002";

        java.util.List<DbWarehouse> warehouses = new java.util.ArrayList<>();
        warehouses.add(warehouse1);
        warehouses.add(warehouse2);

        assertEquals(2, warehouses.size());
        assertEquals("BU-001", warehouses.get(0).businessUnitCode);
        assertEquals("BU-002", warehouses.get(1).businessUnitCode);
    }

    @Test
    void shouldHaveCorrectFieldTypes() throws NoSuchFieldException {
        // Verify field types match the expected types
        Field idField = DbWarehouse.class.getDeclaredField("id");
        Field businessUnitCodeField = DbWarehouse.class.getDeclaredField("businessUnitCode");
        Field locationField = DbWarehouse.class.getDeclaredField("location");
        Field capacityField = DbWarehouse.class.getDeclaredField("capacity");
        Field stockField = DbWarehouse.class.getDeclaredField("stock");
        Field createdAtField = DbWarehouse.class.getDeclaredField("createdAt");
        Field archivedAtField = DbWarehouse.class.getDeclaredField("archivedAt");

        assertEquals(Long.class, idField.getType());
        assertEquals(String.class, businessUnitCodeField.getType());
        assertEquals(String.class, locationField.getType());
        assertEquals(Integer.class, capacityField.getType());
        assertEquals(Integer.class, stockField.getType());
        assertEquals(LocalDateTime.class, createdAtField.getType());
        assertEquals(LocalDateTime.class, archivedAtField.getType());
    }
}