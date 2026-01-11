package com.fulfilment.application.monolith.warehouses.domain.models;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    @Test
    void shouldCreateWithDefaultConstructor() {
        // Act
        Location location = new Location();

        // Assert
        assertNotNull(location);
        assertNull(location.getIdentification());
        assertNull(location.getName());
        assertEquals(0, location.getMaxNumberOfWarehouses());
        assertEquals(0, location.getMaxCapacity());
    }

    @Test
    void shouldCreateWithThreeParameterConstructor() {
        // Arrange
        String identification = "LOC-001";
        int maxWarehouses = 5;
        int maxCapacity = 1000;

        // Act
        Location location = new Location(identification, maxWarehouses, maxCapacity);

        // Assert
        assertEquals(identification, location.getIdentification());
        assertNull(location.getName()); // Name not set in this constructor
        assertEquals(maxWarehouses, location.getMaxNumberOfWarehouses());
        assertEquals(maxCapacity, location.getMaxCapacity());
    }

    @Test
    void shouldCreateWithFourParameterConstructor() {
        // Arrange
        String identification = "LOC-001";
        String name = "Main Location";
        int maxWarehouses = 5;
        int maxCapacity = 1000;

        // Act
        Location location = new Location(identification, name, maxWarehouses, maxCapacity);

        // Assert
        assertEquals(identification, location.getIdentification());
        assertEquals(name, location.getName());
        assertEquals(maxWarehouses, location.getMaxNumberOfWarehouses());
        assertEquals(maxCapacity, location.getMaxCapacity());
    }

    @Test
    void shouldBuildWithBuilderPattern() {
        // Act
        Location location = Location.builder()
                .identification("LOC-001")
                .name("Main Location")
                .maxNumberOfWarehouses(5)
                .maxCapacity(1000)
                .build();

        // Assert
        assertNotNull(location);
        assertEquals("LOC-001", location.getIdentification());
        assertEquals("Main Location", location.getName());
        assertEquals(5, location.getMaxNumberOfWarehouses());
        assertEquals(1000, location.getMaxCapacity());
    }

    @Test
    void shouldBuildWithIdentifierAliasMethod() {
        // Act
        Location location = Location.builder()
                .identifier("LOC-001")  // Using alias method
                .name("Main Location")
                .maxNumberOfWarehouses(5)
                .maxCapacity(1000)
                .build();

        // Assert
        assertEquals("LOC-001", location.getIdentification());
    }

    @Test
    void shouldUseDefaultValuesInBuilder() {
        // Act
        Location location = Location.builder()
                .identification("LOC-001")
                .build();

        // Assert
        assertEquals("LOC-001", location.getIdentification());
        assertNull(location.getName());
        assertEquals(0, location.getMaxNumberOfWarehouses());
        assertEquals(0, location.getMaxCapacity());
    }

    @Test
    void shouldThrowExceptionWhenIdentificationIsNullInBuilder() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Location.builder().build()
        );
        assertEquals("Identification cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIdentificationIsEmptyInBuilder() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Location.builder().identification("").build()
        );
        assertEquals("Identification cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenIdentificationIsBlankInBuilder() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Location.builder().identification("   ").build()
        );
        assertEquals("Identification cannot be null or empty", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMaxWarehousesIsNegative() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Location.builder()
                        .identification("LOC-001")
                        .maxNumberOfWarehouses(-1)
                        .build()
        );
        assertEquals("Max number of warehouses cannot be negative", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionWhenMaxCapacityIsNegative() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                Location.builder()
                        .identification("LOC-001")
                        .maxCapacity(-1)
                        .build()
        );
        assertEquals("Max capacity cannot be negative", exception.getMessage());
    }

    @Test
    void shouldAllowZeroValuesInBuilder() {
        // Act
        Location location = Location.builder()
                .identification("LOC-001")
                .maxNumberOfWarehouses(0)
                .maxCapacity(0)
                .build();

        // Assert
        assertEquals(0, location.getMaxNumberOfWarehouses());
        assertEquals(0, location.getMaxCapacity());
    }

    @Test
    void shouldSetAndGetAllFields() {
        // Arrange
        Location location = new Location();

        // Act
        location.setIdentification("LOC-001");
        location.setName("Main Location");
        location.setMaxNumberOfWarehouses(5);
        location.setMaxCapacity(1000);

        // Assert
        assertEquals("LOC-001", location.getIdentification());
        assertEquals("Main Location", location.getName());
        assertEquals(5, location.getMaxNumberOfWarehouses());
        assertEquals(1000, location.getMaxCapacity());
    }

    @Test
    void shouldHandleNullName() {
        // Arrange
        Location location = new Location("LOC-001", 5, 1000);

        // Act
        location.setName(null);

        // Assert
        assertNull(location.getName());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "\t\n"})
    void shouldHandleEmptyOrBlankName(String name) {
        // Arrange
        Location location = new Location();

        // Act
        location.setName(name);

        // Assert
        assertEquals(name, location.getName());
    }

    @Test
    void shouldHaveToStringRepresentation() {
        // Arrange
        Location location = new Location("LOC-001", "Main Location", 5, 1000);

        // Act
        String toString = location.toString();

        // Assert
        assertNotNull(toString);
        assertTrue(toString.contains("LOC-001"));
        assertTrue(toString.contains("Main Location"));
        assertTrue(toString.contains("5"));
        assertTrue(toString.contains("1000"));
    }

    @Test
    void shouldHandleLargeValues() {
        // Arrange
        Location location = Location.builder()
                .identification("LOC-001")
                .maxNumberOfWarehouses(Integer.MAX_VALUE)
                .maxCapacity(Integer.MAX_VALUE)
                .build();

        // Assert
        assertEquals(Integer.MAX_VALUE, location.getMaxNumberOfWarehouses());
        assertEquals(Integer.MAX_VALUE, location.getMaxCapacity());
    }

    @Test
    void shouldBeUsableInCollections() {
        // Arrange
        Location location1 = Location.builder().identification("LOC-001").build();
        Location location2 = Location.builder().identification("LOC-002").build();

        // Act
        java.util.List<Location> locations = java.util.List.of(location1, location2);

        // Assert
        assertEquals(2, locations.size());
        assertEquals("LOC-001", locations.get(0).getIdentification());
        assertEquals("LOC-002", locations.get(1).getIdentification());
    }
}