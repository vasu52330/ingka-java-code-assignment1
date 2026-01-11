package com.fulfilment.application.monolith.products;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.lang.reflect.Field;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ProductTest {

    @Test
    void shouldCreateDefaultConstructorInstance() {
        // Act
        Product product = new Product();

        // Assert
        assertNotNull(product);
        assertNull(product.id);
        assertNull(product.name);
        assertNull(product.description);
        assertNull(product.price);
        assertEquals(0, product.stock);
    }

    @Test
    void shouldCreateWithNameConstructor() {
        // Arrange
        String expectedName = "Test Product";

        // Act
        Product product = new Product(expectedName);

        // Assert
        assertNotNull(product);
        assertEquals(expectedName, product.name);
        assertNull(product.id);
        assertNull(product.description);
        assertNull(product.price);
        assertEquals(0, product.stock);
    }

    @Test
    void shouldSetAndGetAllFieldsViaPublicAccess() {
        // Arrange
        Product product = new Product();
        Long expectedId = 1L;
        String expectedName = "Laptop";
        String expectedDescription = "High-performance laptop";
        BigDecimal expectedPrice = new BigDecimal("999.99");
        int expectedStock = 50;

        // Act
        product.id = expectedId;
        product.name = expectedName;
        product.description = expectedDescription;
        product.price = expectedPrice;
        product.stock = expectedStock;

        // Assert
        assertEquals(expectedId, product.id);
        assertEquals(expectedName, product.name);
        assertEquals(expectedDescription, product.description);
        assertEquals(expectedPrice, product.price);
        assertEquals(expectedStock, product.stock);
    }

    @Test
    void shouldBeEntityAnnotated() {
        // Act
        jakarta.persistence.Entity annotation = Product.class.getAnnotation(jakarta.persistence.Entity.class);

        // Assert
        assertNotNull(annotation, "Product class should be annotated with @Entity");
    }

    @Test
    void shouldBeCacheable() {
        // Act
        jakarta.persistence.Cacheable annotation = Product.class.getAnnotation(jakarta.persistence.Cacheable.class);

        // Assert
        assertNotNull(annotation, "Product class should be annotated with @Cacheable");
    }

    @Test
    void shouldHaveIdFieldWithGeneratedValue() throws NoSuchFieldException {
        // Arrange
        Field idField = Product.class.getDeclaredField("id");

        // Act
        jakarta.persistence.Id idAnnotation = idField.getAnnotation(jakarta.persistence.Id.class);
        jakarta.persistence.GeneratedValue generatedValueAnnotation =
                idField.getAnnotation(jakarta.persistence.GeneratedValue.class);

        // Assert
        assertNotNull(idAnnotation, "id field should be annotated with @Id");
        assertNotNull(generatedValueAnnotation, "id field should be annotated with @GeneratedValue");
    }

    @Test
    void shouldHaveNameFieldWithColumnAnnotation() throws NoSuchFieldException {
        // Arrange
        Field nameField = Product.class.getDeclaredField("name");

        // Act
        jakarta.persistence.Column columnAnnotation = nameField.getAnnotation(jakarta.persistence.Column.class);

        // Assert
        assertNotNull(columnAnnotation, "name field should have @Column annotation");
        assertEquals(40, columnAnnotation.length(), "name field should have length 40");
        assertTrue(columnAnnotation.unique(), "name field should be unique");
    }

    @Test
    void shouldHaveDescriptionFieldWithNullableColumn() throws NoSuchFieldException {
        // Arrange
        Field descriptionField = Product.class.getDeclaredField("description");

        // Act
        jakarta.persistence.Column columnAnnotation = descriptionField.getAnnotation(jakarta.persistence.Column.class);

        // Assert
        assertNotNull(columnAnnotation, "description field should have @Column annotation");
        assertTrue(columnAnnotation.nullable(), "description field should be nullable");
    }

    @Test
    void shouldHavePriceFieldWithDecimalPrecision() throws NoSuchFieldException {
        // Arrange
        Field priceField = Product.class.getDeclaredField("price");

        // Act
        jakarta.persistence.Column columnAnnotation = priceField.getAnnotation(jakarta.persistence.Column.class);

        // Assert
        assertNotNull(columnAnnotation, "price field should have @Column annotation");
        assertTrue(columnAnnotation.nullable(), "price field should be nullable");
        assertEquals(10, columnAnnotation.precision(), "price field should have precision 10");
        assertEquals(2, columnAnnotation.scale(), "price field should have scale 2");
    }

    @Test
    void shouldHavePublicFields() throws NoSuchFieldException {
        // Verify all fields are public
        String[] fieldNames = {"id", "name", "description", "price", "stock"};

        for (String fieldName : fieldNames) {
            Field field = Product.class.getDeclaredField(fieldName);
            assertTrue(java.lang.reflect.Modifier.isPublic(field.getModifiers()),
                    "Field '" + fieldName + "' should be public");
        }
    }

    @Test
    void shouldHandleNullValues() {
        // Arrange
        Product product = new Product();

        // Act - set all fields to null where applicable
        product.id = null;
        product.name = null;
        product.description = null;
        product.price = null;
        product.stock = 0; // primitive, can't be null

        // Assert
        assertNull(product.id);
        assertNull(product.name);
        assertNull(product.description);
        assertNull(product.price);
        assertEquals(0, product.stock);
    }

    @Test
    void shouldHandleEmptyAndBlankStrings() {
        // Arrange
        Product product = new Product();

        // Act
        product.name = "";
        product.description = "   ";

        // Assert
        assertEquals("", product.name);
        assertEquals("   ", product.description);
    }

    @Test
    void shouldHandleSpecialCharactersInStrings() {
        // Arrange
        Product product = new Product();

        // Act
        product.name = "Product@123#Special";
        product.description = "Description with\nnewline\tand tab";

        // Assert
        assertEquals("Product@123#Special", product.name);
        assertEquals("Description with\nnewline\tand tab", product.description);
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1, 100, -1, -100, Integer.MAX_VALUE, Integer.MIN_VALUE})
    void shouldHandleVariousStockValues(int stock) {
        // Arrange
        Product product = new Product();

        // Act
        product.stock = stock;

        // Assert
        assertEquals(stock, product.stock);
    }

    @Test
    void shouldHandleLargeStockValues() {
        // Arrange
        Product product = new Product();

        // Act
        product.stock = Integer.MAX_VALUE;

        // Assert
        assertEquals(Integer.MAX_VALUE, product.stock);

        // Act
        product.stock = Integer.MIN_VALUE;

        // Assert
        assertEquals(Integer.MIN_VALUE, product.stock);
    }

    @Test
    void shouldHandleDecimalPrices() {
        // Test various BigDecimal price values

        Product product = new Product();

        // Zero price
        product.price = BigDecimal.ZERO;
        assertEquals(BigDecimal.ZERO, product.price);

        // Small price
        product.price = new BigDecimal("0.01");
        assertEquals(new BigDecimal("0.01"), product.price);

        // Large price
        product.price = new BigDecimal("999999.99");
        assertEquals(new BigDecimal("999999.99"), product.price);

        // Negative price (if allowed by business logic)
        product.price = new BigDecimal("-10.50");
        assertEquals(new BigDecimal("-10.50"), product.price);

        // Price with many decimal places (will be stored with scale 2)
        product.price = new BigDecimal("123.456789");
        assertEquals(new BigDecimal("123.456789"), product.price);
    }

    @Test
    void shouldHandleNullPrice() {
        // Arrange
        Product product = new Product("Test Product");

        // Act
        product.price = null;

        // Assert
        assertNull(product.price);
        assertEquals("Test Product", product.name);
    }

    @Test
    void shouldHandleLongProductNames() {
        // Test name length up to column limit (40)
        Product product = new Product();

        // 40 character name (maximum)
        String maxLengthName = "A".repeat(40);
        product.name = maxLengthName;
        assertEquals(maxLengthName, product.name);

        // 41 character name (exceeds column length but JPA may truncate or throw)
        String tooLongName = "B".repeat(41);
        product.name = tooLongName;
        assertEquals(tooLongName, product.name); // Field will accept it, DB may truncate
    }

    @Test
    void shouldCreateMultipleProductInstances() {
        // Arrange
        Product product1 = new Product("Product 1");
        product1.id = 1L;
        product1.price = new BigDecimal("10.99");
        product1.stock = 100;

        Product product2 = new Product("Product 2");
        product2.id = 2L;
        product2.price = new BigDecimal("20.50");
        product2.stock = 50;

        // Assert
        assertNotEquals(product1.id, product2.id);
        assertNotEquals(product1.name, product2.name);
        assertNotEquals(product1.price, product2.price);
        assertNotEquals(product1.stock, product2.stock);
    }

    @Test
    void shouldHaveDefaultToString() {
        // Arrange
        Product product = new Product("Test Product");
        product.id = 1L;

        // Act
        String toStringResult = product.toString();

        // Assert
        assertNotNull(toStringResult);
        assertTrue(toStringResult.contains("Product"));
    }

    @Test
    void shouldHaveDefaultEqualsAndHashCode() {
        // Since equals() and hashCode() are not overridden, test default behavior
        Product product1 = new Product("Same Product");
        product1.id = 1L;

        Product product2 = new Product("Same Product");
        product2.id = 1L;

        // Default equals() compares object identity, not field values
        assertNotEquals(product1, product2);
        assertNotEquals(product1.hashCode(), product2.hashCode());
    }

    @Test
    void shouldNotImplementSerializable() {
        // Check if class implements Serializable (common for JPA entities but not required)
        boolean isSerializable = java.io.Serializable.class.isAssignableFrom(Product.class);

        // The current Product class doesn't implement Serializable
        assertFalse(isSerializable, "Product class doesn't implement Serializable");
    }

    @Test
    void shouldBeUsableInCollections() {
        // Test that Product can be used in standard Java collections
        Product product1 = new Product("Product 1");
        product1.id = 1L;

        Product product2 = new Product("Product 2");
        product2.id = 2L;

        java.util.List<Product> products = new java.util.ArrayList<>();
        products.add(product1);
        products.add(product2);

        assertEquals(2, products.size());
        assertEquals("Product 1", products.get(0).name);
        assertEquals("Product 2", products.get(1).name);
    }

    @Test
    void shouldHandleNameUniquenessConstraint() {
        // This test documents that the name field has unique constraint
        // Actual uniqueness enforcement happens at database level

        Product product1 = new Product("Duplicate Name");
        Product product2 = new Product("Duplicate Name");

        // Both can have same name at object level
        assertEquals(product1.name, product2.name);

        // Database would enforce uniqueness when persisted
    }

    @Test
    void shouldAllowNullNameInDefaultConstructor() {
        // Default constructor allows null name
        Product product = new Product();
        assertNull(product.name);
    }

    @Test
    void shouldAllowNullNameAfterConstruction() {
        // Name can be set to null after construction
        Product product = new Product("Initial Name");
        product.name = null;

        assertNull(product.name);
    }

    @Test
    void shouldHandleWhitespaceOnlyName() {
        Product product = new Product("   ");
        assertEquals("   ", product.name);
    }

    @Test
    void shouldHandleNewlineInDescription() {
        Product product = new Product();
        product.description = "Line 1\nLine 2\r\nLine 3";
        assertEquals("Line 1\nLine 2\r\nLine 3", product.description);
    }

    @Test
    void shouldHandleVeryLongDescription() {
        // Description has no length constraint in annotation
        Product product = new Product();
        String longDescription = "A".repeat(10000); // 10KB description
        product.description = longDescription;

        assertEquals(longDescription, product.description);
    }

    @Test
    void shouldHandlePricePrecisionAndScale() {
        // Test that BigDecimal values fit within precision 10, scale 2

        Product product = new Product();

        // Valid: 8 digits before decimal, 2 after
        product.price = new BigDecimal("99999999.99"); // 8+2 = 10 digits total
        assertEquals(new BigDecimal("99999999.99"), product.price);

        // Valid: 10 digits before decimal, 0 after
        product.price = new BigDecimal("9999999999"); // 10 digits
        assertEquals(new BigDecimal("9999999999"), product.price);

        // More than 10 digits total (may cause database issues)
        product.price = new BigDecimal("10000000000.00"); // 11 digits
        assertEquals(new BigDecimal("10000000000.00"), product.price); // Field accepts it
    }

    @Test
    void shouldHaveCorrectFieldTypes() throws NoSuchFieldException {
        // Verify field types match the expected types
        Field idField = Product.class.getDeclaredField("id");
        Field nameField = Product.class.getDeclaredField("name");
        Field descriptionField = Product.class.getDeclaredField("description");
        Field priceField = Product.class.getDeclaredField("price");
        Field stockField = Product.class.getDeclaredField("stock");

        assertEquals(Long.class, idField.getType());
        assertEquals(String.class, nameField.getType());
        assertEquals(String.class, descriptionField.getType());
        assertEquals(BigDecimal.class, priceField.getType());
        assertEquals(int.class, stockField.getType());
    }
}