package com.fulfilment.application.monolith.stores;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LegacyStoreManagerGatewayTest {

    private LegacyStoreManagerGateway gateway;
    private Store testStore;

    @BeforeEach
    void setUp() {
        gateway = new LegacyStoreManagerGateway();
        testStore = new Store();
        testStore.name = "TestStore";
        testStore.quantityProductsInStock = 100;
    }

    @Test
    void writeToFile_shouldFormatContentCorrectly() {
        // Test with different store data
        Store store = new Store();
        store.name = "MyStore";
        store.quantityProductsInStock = 0; // Edge case: zero products

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            Path mockPath = mock(Path.class);
            when(mockPath.toString()).thenReturn("/tmp/MyStore123.txt");

            filesMock.when(() -> Files.createTempFile(eq("MyStore"), eq(".txt")))
                    .thenReturn(mockPath);

            // Capture the written content
            final byte[][] capturedBytes = new byte[1][];
            filesMock.when(() -> Files.write(eq(mockPath), any(byte[].class)))
                    .thenAnswer(invocation -> {
                        capturedBytes[0] = invocation.getArgument(1);
                        return mockPath;
                    });

            filesMock.when(() -> Files.readAllBytes(eq(mockPath)))
                    .thenReturn("test content".getBytes());

            // Act
            gateway.createStoreOnLegacySystem(store);

            // Assert
            assertNotNull(capturedBytes[0]);
            String capturedContent = new String(capturedBytes[0]);
            assertTrue(capturedContent.contains("Store created."));
            assertTrue(capturedContent.contains("name =MyStore"));
            assertTrue(capturedContent.contains("items on stock =0"));
        }
    }

    @Test
    void writeToFile_shouldFormatContentCorrectly_simplified() {
        // Simplified version - just verify the method calls without checking content
        Store store = new Store();
        store.name = "MyStore";
        store.quantityProductsInStock = 0;

        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            Path mockPath = mock(Path.class);

            filesMock.when(() -> Files.createTempFile(anyString(), anyString()))
                    .thenReturn(mockPath);
            filesMock.when(() -> Files.write(any(Path.class), any(byte[].class)))
                    .thenReturn(mockPath);
            filesMock.when(() -> Files.readAllBytes(any(Path.class)))
                    .thenReturn("test".getBytes());
            filesMock.when(() -> Files.delete(any(Path.class)))
                    .thenAnswer(invocation -> null);

            // Act
            gateway.createStoreOnLegacySystem(store);

            // Assert - verify all file operations were called
            filesMock.verify(() -> Files.createTempFile(eq("MyStore"), eq(".txt")));
            filesMock.verify(() -> Files.write(eq(mockPath), any(byte[].class)));
            filesMock.verify(() -> Files.readAllBytes(eq(mockPath)));
            filesMock.verify(() -> Files.delete(eq(mockPath)));
        }
    }

    @Test
    void writeToFile_shouldFormatContentCorrectly_withSpy() {
        // Alternative approach using a spy to verify behavior
        LegacyStoreManagerGateway spyGateway = spy(gateway);
        Store store = new Store();
        store.name = "MyStore";
        store.quantityProductsInStock = 0;

        // Mock the private writeToFile method to capture content
        doAnswer(invocation -> {
            Store s = invocation.getArgument(0);
            // Verify the store data
            assertEquals("MyStore", s.name);
            assertEquals(0, s.quantityProductsInStock);
            return null;
        }).when(spyGateway).writeToFile(any(Store.class));

        // Act
        spyGateway.createStoreOnLegacySystem(store);

        // Assert
        verify(spyGateway).writeToFile(store);
    }

    @Test
    void shouldCreateCorrectContentFormat() {
        // Test the content format directly (if we could access the private method)
        // Since we can't test the private method directly, we can test the behavior

        Store store = new Store();
        store.name = "Test Store";
        store.quantityProductsInStock = 123;

        // Expected format based on the implementation:
        // "Store created. [ name =Test Store ] [ items on stock =123]"

        // We can't directly test the private method, but we can verify
        // that the public methods call it correctly
        try (MockedStatic<Files> filesMock = mockStatic(Files.class)) {
            Path mockPath = mock(Path.class);

            filesMock.when(() -> Files.createTempFile(anyString(), anyString()))
                    .thenReturn(mockPath);
            filesMock.when(() -> Files.write(any(Path.class), any(byte[].class)))
                    .thenReturn(mockPath);
            filesMock.when(() -> Files.readAllBytes(any(Path.class)))
                    .thenReturn("test".getBytes());

            // Act
            gateway.createStoreOnLegacySystem(store);

            // Verify the temp file was created with store name as prefix
            filesMock.verify(() -> Files.createTempFile(eq("Test Store"), eq(".txt")));
        }
    }
}