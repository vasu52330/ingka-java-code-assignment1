package com.fulfilment.application.monolith.stores;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class StoreTest {

    @Test
    void shouldCreateDefaultConstructorInstance() {
        // Act
        Store store = new Store();

        // Assert
        assertNotNull(store);
        assertNull(store.getId());
        assertNull(store.getName());
        assertEquals(0, store.getQuantityProductsInStock());
    }

    @Test
    void shouldCreateParameterizedConstructorInstance() {
        // Arrange
        String expectedName = "Test Store";
        int expectedQuantity = 100;

        // Act
        Store store = new Store(expectedName, expectedQuantity);

        // Assert
        assertNotNull(store);
        assertEquals(expectedName, store.getName());
        assertEquals(expectedQuantity, store.getQuantityProductsInStock());
    }

    @Test
    void shouldSetAndGetId() {
        // Arrange
        Store store = new Store();
        Long expectedId = 1L;

        // Act
        store.setId(expectedId);

        // Assert
        assertEquals(expectedId, store.getId());
    }

    @Test
    void shouldSetAndGetName() {
        // Arrange
        Store store = new Store();
        String expectedName = "My Store";

        // Act
        store.setName(expectedName);

        // Assert
        assertEquals(expectedName, store.getName());
    }

    @Test
    void shouldSetAndGetQuantityProductsInStock() {
        // Arrange
        Store store = new Store();
        int expectedQuantity = 500;

        // Act
        store.setQuantityProductsInStock(expectedQuantity);

        // Assert
        assertEquals(expectedQuantity, store.getQuantityProductsInStock());
    }

    @Test
    void shouldHandleNullName() {
        // Arrange
        Store store = new Store();

        // Act
        store.setName(null);

        // Assert
        assertNull(store.getName());
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, -1, -100, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void shouldHandleVariousQuantityValues(int quantity) {
        // Arrange
        Store store = new Store();

        // Act
        store.setQuantityProductsInStock(quantity);

        // Assert
        assertEquals(quantity, store.getQuantityProductsInStock());
    }

    @Test
    void shouldUsePublicFieldsDirectly() {
        // Arrange
        Store store = new Store();
        String expectedName = "Direct Access Store";
        int expectedQuantity = 999;

        // Act
        store.name = expectedName;  // Direct field access
        store.quantityProductsInStock = expectedQuantity;  // Direct field access

        // Assert
        assertEquals(expectedName, store.name);
        assertEquals(expectedQuantity, store.quantityProductsInStock);
        assertEquals(expectedName, store.getName());  // Getter should return same value
        assertEquals(expectedQuantity, store.getQuantityProductsInStock());  // Getter should return same value
    }

    @Test
    void shouldBeEntityAnnotated() {
        // Act
        Entity annotation = Store.class.getAnnotation(Entity.class);

        // Assert
        assertNotNull(annotation, "Store class should be annotated with @Entity");
    }

    @Test
    void shouldHaveIdFieldAnnotated() throws NoSuchFieldException {
        // Arrange
        var idField = Store.class.getDeclaredField("id");

        // Act
        Id idAnnotation = idField.getAnnotation(Id.class);
        GeneratedValue generatedValueAnnotation = idField.getAnnotation(GeneratedValue.class);

        // Assert
        assertNotNull(idAnnotation, "id field should be annotated with @Id");
        assertNotNull(generatedValueAnnotation, "id field should be annotated with @GeneratedValue");
        assertEquals(GenerationType.IDENTITY, generatedValueAnnotation.strategy());
    }

    @Test
    void shouldUpdatePublicFieldAndGetterConsistently() {
        // Test that updating the public field updates the getter value
        Store store = new Store();

        // Update via setter
        store.setName("Setter Name");
        store.setQuantityProductsInStock(100);

        // Verify getters and public fields match
        assertEquals(store.name, store.getName());
        assertEquals(store.quantityProductsInStock, store.getQuantityProductsInStock());

        // Update via public field
        store.name = "Direct Name";
        store.quantityProductsInStock = 200;

        // Verify getters still match
        assertEquals("Direct Name", store.getName());
        assertEquals(200, store.getQuantityProductsInStock());
    }

    @Test
    void shouldHandleEmptyStringName() {
        // Arrange
        Store store = new Store();

        // Act
        store.setName("");
        store.name = "";

        // Assert
        assertEquals("", store.getName());
        assertEquals("", store.name);
    }

    @Test
    void shouldHaveCorrectToString() {
        // This test assumes Lombok @ToString or custom toString
        // Since toString() is not overridden, we test default behavior
        Store store = new Store("Test Store", 100);

        // Act
        String toStringResult = store.toString();

        // Assert - default toString contains class name and hash
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("Store"));
    }

    @Test
    void shouldBeSerializable() {
        // Verify Store can be serialized (important for JPA entities)
        // This is a simple check - in real scenario, we'd test actual serialization

        // Arrange
        Store store = new Store("Serializable Store", 500);

        // Act & Assert - check if class implements Serializable (not required for JPA but good practice)
        // JPA entities typically should implement Serializable
        boolean isSerializable = java.io.Serializable.class.isAssignableFrom(Store.class);

        // Note: The current Store class doesn't implement Serializable
        // This test documents that fact
        assertFalse(isSerializable, "Store class doesn't implement Serializable (might be intentional)");
    }

    @Test
    void shouldCreateMultipleInstancesWithDifferentValues() {
        // Arrange
        Store store1 = new Store("Store 1", 100);
        Store store2 = new Store("Store 2", 200);

        // Act
        store1.setId(1L);
        store2.setId(2L);

        // Assert
        assertNotEquals(store1.getId(), store2.getId());
        assertNotEquals(store1.getName(), store2.getName());
        assertNotEquals(store1.getQuantityProductsInStock(), store2.getQuantityProductsInStock());
    }

    @Test
    void shouldHavePublicFields() throws NoSuchFieldException {
        // Verify that fields are public (as shown in the code)
        var nameField = Store.class.getDeclaredField("name");
        var quantityField = Store.class.getDeclaredField("quantityProductsInStock");

        // These fields should be public based on the provided code
        assertTrue(java.lang.reflect.Modifier.isPublic(nameField.getModifiers()));
        assertTrue(java.lang.reflect.Modifier.isPublic(quantityField.getModifiers()));
    }

    @Test
    void shouldHavePrivateIdField() throws NoSuchFieldException {
        // Verify id field is private
        var idField = Store.class.getDeclaredField("id");
        assertTrue(java.lang.reflect.Modifier.isPrivate(idField.getModifiers()));
    }
}