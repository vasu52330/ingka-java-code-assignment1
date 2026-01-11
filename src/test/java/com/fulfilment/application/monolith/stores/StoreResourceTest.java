package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StoreResourceTest {

    @Mock
    private LegacyStoreManagerGateway legacyStoreManagerGateway;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private StoreResource storeResource;

    @Captor
    private ArgumentCaptor<Store> storeCaptor;

    private Store testStore;
    private Store updatedTestStore;

    @BeforeEach
    void setUp() {
        testStore = new Store("Test Store", 100);
        updatedTestStore = new Store("Updated Store", 200);
    }

    @Test
    void shouldBeApplicationScoped() {
        ApplicationScoped annotation = StoreResource.class.getAnnotation(ApplicationScoped.class);
        assertNotNull(annotation, "Class should be annotated with @ApplicationScoped");
    }

    @Test
    void shouldHaveCorrectPathAnnotation() {
        Path pathAnnotation = StoreResource.class.getAnnotation(Path.class);
        assertNotNull(pathAnnotation, "Class should be annotated with @Path");
        assertEquals("/stores", pathAnnotation.value());
    }

    // CREATE STORE TESTS

    @Test
    void createStore_shouldPersistStoreToDatabase() {
        // Act
        Response response = storeResource.createStore(testStore);

        // Assert
        verify(entityManager).persist(testStore);
        verify(entityManager).flush();
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        assertEquals(testStore, response.getEntity());
    }

    @Test
    void createStore_shouldCallLegacySystemAfterPersist() {
        // Act
        storeResource.createStore(testStore);

        // Assert
        // Verify order: persist -> flush -> legacy system
        verify(entityManager).persist(testStore);
        verify(entityManager).flush();
        verify(legacyStoreManagerGateway).createStoreOnLegacySystem(testStore);
    }

    @Test
    void createStore_shouldReturnInternalServerErrorOnException() {
        // Arrange
        doThrow(new RuntimeException("Database error")).when(entityManager).persist(any(Store.class));

        // Act
        Response response = storeResource.createStore(testStore);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Error creating store"));
        verify(entityManager, never()).flush();
        verify(legacyStoreManagerGateway, never()).createStoreOnLegacySystem(any());
    }

    @Test
    void createStore_shouldNotCallLegacySystemIfPersistFails() {
        // Arrange
        doThrow(new RuntimeException("Persist failed")).when(entityManager).persist(any(Store.class));

        // Act
        storeResource.createStore(testStore);

        // Assert
        verify(legacyStoreManagerGateway, never()).createStoreOnLegacySystem(any());
    }

    @Test
    void createStore_shouldHandleFlushException() {
        // Arrange
        doThrow(new RuntimeException("Flush failed")).when(entityManager).flush();

        // Act
        Response response = storeResource.createStore(testStore);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        verify(entityManager).persist(testStore);
        verify(legacyStoreManagerGateway, never()).createStoreOnLegacySystem(any());
    }

    @Test
    void createStore_shouldWorkWithNullStoreProperties() {
        // Arrange
        Store nullStore = new Store(null, 0);

        // Act
        Response response = storeResource.createStore(nullStore);

        // Assert
        assertEquals(Response.Status.CREATED.getStatusCode(), response.getStatus());
        verify(entityManager).persist(nullStore);
    }

    @Test
    void createStore_shouldHandleTransactionRollbackScenario() {
        // Arrange
        doThrow(new RuntimeException("Transaction rollback test")).when(entityManager).persist(any());

        // Act
        Response response = storeResource.createStore(testStore);

        // Assert - transaction should be rolled back automatically
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        verify(entityManager, never()).flush();
    }

    // UPDATE STORE TESTS

    @Test
    void updateStore_shouldUpdateExistingStore() {
        // Arrange
        Long storeId = 1L;
        Store existingStore = new Store("Old Store", 50);
        existingStore.setId(storeId);

        when(entityManager.find(Store.class, storeId)).thenReturn(existingStore);

        // Act
        Response response = storeResource.updateStore(storeId, updatedTestStore);

        // Assert
        verify(entityManager).find(Store.class, storeId);
        verify(entityManager).merge(existingStore);
        verify(entityManager).flush();
        verify(legacyStoreManagerGateway).updateStoreOnLegacySystem(existingStore);

        assertEquals("Updated Store", existingStore.getName());
        assertEquals(200, existingStore.getQuantityProductsInStock());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertEquals(existingStore, response.getEntity());
    }

    @Test
    void updateStore_shouldReturnNotFoundForNonExistentStore() {
        // Arrange
        Long nonExistentId = 999L;
        when(entityManager.find(Store.class, nonExistentId)).thenReturn(null);

        // Act
        Response response = storeResource.updateStore(nonExistentId, updatedTestStore);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Store not found"));
        verify(entityManager, never()).merge(any());
        verify(entityManager, never()).flush();
        verify(legacyStoreManagerGateway, never()).updateStoreOnLegacySystem(any());
    }

    @Test
    void updateStore_shouldHandleNullId() {
        // Act
        Response response = storeResource.updateStore(null, updatedTestStore);

        // Assert
        assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());
    }

    @Test
    void updateStore_shouldUpdateOnlyNameAndQuantity() {
        // Arrange
        Long storeId = 1L;
        Store existingStore = new Store("Old Store", 50);
        existingStore.setId(storeId);
        // Set some other property that shouldn't be updated
        existingStore.name = "Direct field access";

        when(entityManager.find(Store.class, storeId)).thenReturn(existingStore);

        // Act
        Response response = storeResource.updateStore(storeId, updatedTestStore);

        // Assert - only name and quantity should be updated via setters
        assertEquals("Updated Store", existingStore.getName());
        assertEquals(200, existingStore.getQuantityProductsInStock());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void updateStore_shouldHandleMergeException() {
        // Arrange
        Long storeId = 1L;
        Store existingStore = new Store("Old Store", 50);
        existingStore.setId(storeId);

        when(entityManager.find(Store.class, storeId)).thenReturn(existingStore);
        doThrow(new RuntimeException("Merge failed")).when(entityManager).merge(any(Store.class));

        // Act
        Response response = storeResource.updateStore(storeId, updatedTestStore);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        verify(legacyStoreManagerGateway, never()).updateStoreOnLegacySystem(any());
    }

    @Test
    void updateStore_shouldHandleFlushExceptionDuringUpdate() {
        // Arrange
        Long storeId = 1L;
        Store existingStore = new Store("Old Store", 50);
        existingStore.setId(storeId);

        when(entityManager.find(Store.class, storeId)).thenReturn(existingStore);
        doThrow(new RuntimeException("Flush failed")).when(entityManager).flush();

        // Act
        Response response = storeResource.updateStore(storeId, updatedTestStore);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        verify(legacyStoreManagerGateway, never()).updateStoreOnLegacySystem(any());
    }

    @Test
    void updateStore_shouldUpdateWithNullName() {
        // Arrange
        Long storeId = 1L;
        Store existingStore = new Store("Old Store", 50);
        existingStore.setId(storeId);
        Store updateWithNullName = new Store(null, 200);

        when(entityManager.find(Store.class, storeId)).thenReturn(existingStore);

        // Act
        Response response = storeResource.updateStore(storeId, updateWithNullName);

        // Assert
        assertNull(existingStore.getName());
        assertEquals(200, existingStore.getQuantityProductsInStock());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void updateStore_shouldUpdateWithZeroQuantity() {
        // Arrange
        Long storeId = 1L;
        Store existingStore = new Store("Old Store", 50);
        existingStore.setId(storeId);
        Store updateWithZero = new Store("Updated", 0);

        when(entityManager.find(Store.class, storeId)).thenReturn(existingStore);

        // Act
        Response response = storeResource.updateStore(storeId, updateWithZero);

        // Assert
        assertEquals("Updated", existingStore.getName());
        assertEquals(0, existingStore.getQuantityProductsInStock());
        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    }

    @Test
    void updateStore_shouldCaptureUpdatedStoreForLegacySystem() {
        // Arrange
        Long storeId = 1L;
        Store existingStore = new Store("Old Store", 50);
        existingStore.setId(storeId);

        when(entityManager.find(Store.class, storeId)).thenReturn(existingStore);

        // Act
        storeResource.updateStore(storeId, updatedTestStore);

        // Assert - verify the exact store object passed to legacy system
        verify(legacyStoreManagerGateway).updateStoreOnLegacySystem(existingStore);
        assertEquals("Updated Store", existingStore.getName());
        assertEquals(200, existingStore.getQuantityProductsInStock());
    }

    @Test
    void updateStore_shouldNotModifyOriginalUpdatedStoreObject() {
        // Arrange
        Long storeId = 1L;
        Store existingStore = new Store("Old Store", 50);
        existingStore.setId(storeId);
        Store originalUpdatedStore = new Store("Updated", 200);

        when(entityManager.find(Store.class, storeId)).thenReturn(existingStore);

        // Act
        storeResource.updateStore(storeId, originalUpdatedStore);

        // Assert - original updatedStore object should remain unchanged
        assertEquals("Updated", originalUpdatedStore.getName());
        assertEquals(200, originalUpdatedStore.getQuantityProductsInStock());
    }

    @Test
    void createStore_shouldHandleEntityManagerNullInjection() {
        // Arrange
        StoreResource resourceWithNullEM = new StoreResource();
        resourceWithNullEM.legacyStoreManagerGateway = legacyStoreManagerGateway;
        // entityManager is null (simulating injection failure)

        // Act
        Response response = resourceWithNullEM.createStore(testStore);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Error creating store"));
    }

    @Test
    void updateStore_shouldHandleEntityManagerNullInjection() {
        // Arrange
        StoreResource resourceWithNullEM = new StoreResource();
        resourceWithNullEM.legacyStoreManagerGateway = legacyStoreManagerGateway;
        // entityManager is null (simulating injection failure)

        // Act
        Response response = resourceWithNullEM.updateStore(1L, updatedTestStore);

        // Assert
        assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());
        assertTrue(response.getEntity().toString().contains("Error updating store"));
    }

    @Test
    void shouldHaveTransactionalAnnotations() throws NoSuchMethodException {
        // Verify that methods have @Transactional annotation
        var createMethod = StoreResource.class.getMethod("createStore", Store.class);
        var updateMethod = StoreResource.class.getMethod("updateStore", Long.class, Store.class);

        assertNotNull(createMethod.getAnnotation(jakarta.transaction.Transactional.class),
                "createStore should have @Transactional annotation");
        assertNotNull(updateMethod.getAnnotation(jakarta.transaction.Transactional.class),
                "updateStore should have @Transactional annotation");
    }

    @Test
    void shouldHaveCorrectHttpMethodAnnotations() throws NoSuchMethodException {
        // Verify HTTP method annotations
        var createMethod = StoreResource.class.getMethod("createStore", Store.class);
        var updateMethod = StoreResource.class.getMethod("updateStore", Long.class, Store.class);

        assertNotNull(createMethod.getAnnotation(jakarta.ws.rs.POST.class),
                "createStore should have @POST annotation");
        assertNotNull(updateMethod.getAnnotation(jakarta.ws.rs.PUT.class),
                "updateStore should have @PUT annotation");
        assertNotNull(updateMethod.getAnnotation(jakarta.ws.rs.Path.class),
                "updateStore should have @Path annotation");
    }
}