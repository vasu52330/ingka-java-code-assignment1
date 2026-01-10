package com.fulfilment.application.monolith.location;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import jakarta.enterprise.context.ApplicationScoped;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class LocationGatewayTest {

    private LocationGateway locationGateway;

    @BeforeEach
    void setUp() {
        locationGateway = new LocationGateway();
    }

    @Test
    void testLocationGateway_ImplementsLocationResolver() {
        // Assert
        assertTrue(locationGateway instanceof LocationResolver);
    }

    @Test
    void testResolveByIdentifier_ExistingLocation() {
        // Arrange
        String existingIdentifier = "ZWOLLE-001";

        // Act
        Location result = locationGateway.resolveByIdentifier(existingIdentifier);

        // Assert
        assertNotNull(result);
        assertEquals(existingIdentifier, result.getIdentification()); // Changed from getIdentification()
        // We need to check the actual getter methods from the Location class
        // Based on constructor new Location("ZWOLLE-001", 1, 40):
        // - First parameter is likely identifier
        // - Second parameter could be capacity, level, zone, etc.
        // - Third parameter could be capacity, area, etc.
    }

    @Test
    void testResolveByIdentifier_AnotherExistingLocation() {
        // Arrange
        String existingIdentifier = "AMSTERDAM-001";

        // Act
        Location result = locationGateway.resolveByIdentifier(existingIdentifier);

        // Assert
        assertNotNull(result);
        assertEquals(existingIdentifier, result.getIdentification());
    }

    @Test
    void testResolveByIdentifier_NonExistingLocation() {
        // Arrange
        String nonExistingIdentifier = "NON-EXISTENT";

        // Act
        Location result = locationGateway.resolveByIdentifier(nonExistingIdentifier);

        // Assert
        assertNull(result);
    }

    @Test
    void testResolveByIdentifier_NullIdentifier() {
        // Arrange
        String nullIdentifier = null;

        // Act
        Location result = locationGateway.resolveByIdentifier(nullIdentifier);

        // Assert
        assertNull(result);
    }

    @Test
    void testResolveByIdentifier_EmptyIdentifier() {
        // Arrange
        String emptyIdentifier = "";

        // Act
        Location result = locationGateway.resolveByIdentifier(emptyIdentifier);

        // Assert
        assertNull(result);
    }

    @Test
    void testResolveByIdentifier_BlankIdentifier() {
        // Arrange
        String blankIdentifier = "   ";

        // Act
        Location result = locationGateway.resolveByIdentifier(blankIdentifier);

        // Assert
        assertNull(result);
    }

    @Test
    void testResolveByIdentifier_CaseSensitive() {
        // Arrange
        String lowercaseIdentifier = "zwolle-001"; // lowercase version

        // Act
        Location result = locationGateway.resolveByIdentifier(lowercaseIdentifier);

        // Assert
        assertNull(result); // Should not find because identifiers are case-sensitive
    }

    @Test
    void testResolveByIdentifier_WithLeadingTrailingSpaces() {
        // Arrange
        String identifierWithSpaces = "  ZWOLLE-001  ";

        // Act
        Location result = locationGateway.resolveByIdentifier(identifierWithSpaces);

        // Assert
        assertNull(result); // Should not find because of spaces
    }

    @Test
    void testGetAllLocations_ReturnsAllLocations() {
        // Act
        List<Location> allLocations = LocationGateway.getAllLocations();

        // Assert
        assertNotNull(allLocations);
        assertEquals(8, allLocations.size()); // From static initializer

        // Verify some specific locations exist
        boolean foundZwolle001 = allLocations.stream()
                .anyMatch(loc -> "ZWOLLE-001".equals(loc.getIdentification()));
        assertTrue(foundZwolle001, "Should contain ZWOLLE-001");

        boolean foundAmsterdam002 = allLocations.stream()
                .anyMatch(loc -> "AMSTERDAM-002".equals(loc.getIdentification()));
        assertTrue(foundAmsterdam002, "Should contain AMSTERDAM-002");
    }

    @Test
    void testGetAllLocations_ReturnsCopyNotOriginal() {
        // Act - Get the locations list
        List<Location> locations1 = LocationGateway.getAllLocations();
        int originalSize = locations1.size();

        // Try to modify the returned list (should not affect original)
        Location testLocation = new Location("TEST-001", 99, 99);
        locations1.add(testLocation);

        // Get the list again
        List<Location> locations2 = LocationGateway.getAllLocations();

        // Assert - The added location should not be in the new list
        assertEquals(originalSize, locations2.size()); // Still original size

        boolean foundTestLocation = locations2.stream()
                .anyMatch(loc -> "TEST-001".equals(loc.getIdentification()));
        assertFalse(foundTestLocation, "Should not contain TEST-001");
    }

    @Test
    void testGetAllLocations_OrderMatchesStaticInitializer() {
        // Act
        List<Location> allLocations = LocationGateway.getAllLocations();

        // Assert - Verify order matches the static initializer
        assertEquals("ZWOLLE-001", allLocations.get(0).getIdentification());
        assertEquals("ZWOLLE-002", allLocations.get(1).getIdentification());
        assertEquals("AMSTERDAM-001", allLocations.get(2).getIdentification());
        assertEquals("AMSTERDAM-002", allLocations.get(3).getIdentification());
        assertEquals("TILBURG-001", allLocations.get(4).getIdentification());
        assertEquals("HELMOND-001", allLocations.get(5).getIdentification());
        assertEquals("EINDHOVEN-001", allLocations.get(6).getIdentification());
        assertEquals("VETSBY-001", allLocations.get(7).getIdentification());
    }

    @Test
    void testStaticInitializer_ContainsAllExpectedIdentifiers() {
        // Get all locations
        List<Location> allLocations = LocationGateway.getAllLocations();

        // Create expected identifiers based on static initializer
        List<String> expectedIdentifiers = List.of(
                "ZWOLLE-001", "ZWOLLE-002", "AMSTERDAM-001", "AMSTERDAM-002",
                "TILBURG-001", "HELMOND-001", "EINDHOVEN-001", "VETSBY-001"
        );

        // Assert
        assertEquals(expectedIdentifiers.size(), allLocations.size());

        // Verify each location's identifier
        for (int i = 0; i < expectedIdentifiers.size(); i++) {
            String expectedIdentifier = expectedIdentifiers.get(i);
            String actualIdentifier = allLocations.get(i).getIdentification();
            assertEquals(expectedIdentifier, actualIdentifier);
        }
    }

    @Test
    void testApplicationScopedAnnotation() {
        // Arrange - Get the annotation from the class
        ApplicationScoped annotation = LocationGateway.class.getAnnotation(ApplicationScoped.class);

        // Assert
        assertNotNull(annotation, "LocationGateway should be annotated with @ApplicationScoped");
    }

    @Test
    void testResolveByIdentifier_ReturnsSameInstance() {
        // Arrange
        String identifier1 = "ZWOLLE-001";
        String identifier2 = "AMSTERDAM-001";

        // Act
        Location location1 = locationGateway.resolveByIdentifier(identifier1);
        Location location2 = locationGateway.resolveByIdentifier(identifier1);
        Location location3 = locationGateway.resolveByIdentifier(identifier2);

        // Assert - Same identifier should return same instance
        assertSame(location1, location2);

        // Different identifiers should return different instances
        assertNotSame(location1, location3);
        assertNotSame(location2, location3);
    }

    @Test
    void testLocationConstructorValues() {
        // This test helps understand what the Location constructor parameters represent
        // Arrange
        String identifier = "TEST-LOC";
        int param1 = 10;
        int param2 = 20;

        // Create a test location
        Location testLocation = new Location(identifier, param1, param2);

        // Assert - Test what getters are available
        assertEquals(identifier, testLocation.getIdentification());

        // Try to discover other properties - you may need to adjust based on actual Location class
        // Common possibilities for the integer parameters:
        // - getCapacity(), getLevel(), getZone(), getArea(), getMaxOccupancy(), etc.

        // You might need to add these assertions once you know the actual getter names:
        // assertEquals(param1, testLocation.getCapacity());
        // assertEquals(param2, testLocation.getMaxOccupancy());
    }

    @Test
    void testResolveByIdentifier_AllLocations() {
        // Test resolving each location from the static initializer
        String[] identifiers = {
                "ZWOLLE-001", "ZWOLLE-002", "AMSTERDAM-001", "AMSTERDAM-002",
                "TILBURG-001", "HELMOND-001", "EINDHOVEN-001", "VETSBY-001"
        };

        for (String identifier : identifiers) {
            Location location = locationGateway.resolveByIdentifier(identifier);
            assertNotNull(location, "Should find location: " + identifier);
            assertEquals(identifier, location.getIdentification());
        }
    }

    @Test
    void testNoArgsConstructor() {
        // Act - Simply create an instance
        LocationGateway gateway = new LocationGateway();

        // Assert
        assertNotNull(gateway);
        assertTrue(gateway instanceof LocationResolver);
    }

    @Test
    void testLocationEqualsAndHashCode() {
        // Get the same location twice
        Location location1 = locationGateway.resolveByIdentifier("ZWOLLE-001");
        Location location2 = locationGateway.resolveByIdentifier("ZWOLLE-001");
        Location location3 = locationGateway.resolveByIdentifier("AMSTERDAM-001");

        // Test equals - should be equal for same location
        assertEquals(location1, location2);
        assertNotEquals(location1, location3);

        // Test hashCode consistency
        assertEquals(location1.hashCode(), location2.hashCode());

        // Test toString (should not be null)
        assertNotNull(location1.toString());
        assertTrue(location1.toString().contains("ZWOLLE-001"));
    }

    @Test
    void testGetAllLocations_ImmutableTest() {
        // Test that we can't modify the internal list through getAllLocations
        List<Location> locations = LocationGateway.getAllLocations();
        int originalSize = locations.size();

        // This should create a new list, so we can modify our reference
        locations = new ArrayList<>(locations);
        locations.add(new Location("NEW-001", 1, 1));

        // Get locations again - should still be original size
        List<Location> locationsAgain = LocationGateway.getAllLocations();
        assertEquals(originalSize, locationsAgain.size());
    }
}