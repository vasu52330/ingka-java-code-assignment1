package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WarehouseResourceImplTest {

  @Mock
  private CreateWarehouseUseCase createWarehouseUseCase;

  @Mock
  private ReplaceWarehouseUseCase replaceWarehouseUseCase;

  @Mock
  private ArchiveWarehouseUseCase archiveWarehouseUseCase;

  @Mock
  private WarehouseStore warehouseStore;

  @InjectMocks
  private WarehouseResourceImpl warehouseResource;

  private WarehouseRequest validWarehouseRequest;
  private Warehouse sampleWarehouse;
  private String warehouseId;

  @BeforeEach
  void setUp() {
    warehouseId = "WH-" + java.util.UUID.randomUUID().toString();

    validWarehouseRequest = new WarehouseRequest();
    validWarehouseRequest.setBusinessUnitCode("BU-001");
    validWarehouseRequest.setLocationIdentifier("LOC-001");
    validWarehouseRequest.setCapacity(1000);
    validWarehouseRequest.setStock(500);

    sampleWarehouse = Warehouse.builder()
            .id(1L)
            .businessUnitCode("BU-001")
            .locationIdentifier("LOC-001")
            .capacity(1000)
            .currentStock(500)
            .name("Warehouse BU-001 - LOC-001")
            .active(true)
            .archived(false)
            .build();
  }

  @Test
  void testListAllWarehousesUnits_Success() {
    // Arrange
    List<Warehouse> warehouses = Arrays.asList(
            sampleWarehouse,
            Warehouse.builder()
                    .id(2L)
                    .businessUnitCode("BU-002")
                    .locationIdentifier("LOC-002")
                    .capacity(2000)
                    .currentStock(1000)
                    .name("Warehouse BU-002 - LOC-002")
                    .active(true)
                    .archived(false)
                    .build()
    );
    when(warehouseStore.findAllActive()).thenReturn(warehouses);

    // Act
    Response response = warehouseResource.listAllWarehousesUnits();

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());

    @SuppressWarnings("unchecked")
    List<WarehouseResponse> responseList = (List<WarehouseResponse>) response.getEntity();
    assertEquals(2, responseList.size());

    WarehouseResponse firstResponse = responseList.get(0);
    assertEquals("BU-001", firstResponse.getBusinessUnitCode());
    assertEquals("LOC-001", firstResponse.getLocationIdentifier());
    assertEquals(1000, firstResponse.getCapacity());
    assertEquals(500, firstResponse.getStock());
    assertEquals("Warehouse BU-001 - LOC-001", firstResponse.getName());
    assertFalse(firstResponse.isArchived());

    verify(warehouseStore).findAllActive();
  }

  @Test
  void testListAllWarehousesUnits_EmptyList() {
    // Arrange
    when(warehouseStore.findAllActive()).thenReturn(Collections.emptyList());

    // Act
    Response response = warehouseResource.listAllWarehousesUnits();

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());

    @SuppressWarnings("unchecked")
    List<WarehouseResponse> responseList = (List<WarehouseResponse>) response.getEntity();
    assertTrue(responseList.isEmpty());

    verify(warehouseStore).findAllActive();
  }

  @Test
  void testListAllWarehousesUnits_Exception() {
    // Arrange
    when(warehouseStore.findAllActive()).thenThrow(new RuntimeException("Database error"));

    // Act
    Response response = warehouseResource.listAllWarehousesUnits();

    // Assert
    assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertNotNull(errorResponse);
    assertTrue(errorResponse.getError().contains("Failed to retrieve warehouses"));

    verify(warehouseStore).findAllActive();
  }

  @Test
  void testCreateANewWarehouseUnit_StockExceedsCapacity() {
    // Arrange
    WarehouseRequest invalidRequest = new WarehouseRequest();
    invalidRequest.setBusinessUnitCode("BU-001");
    invalidRequest.setLocationIdentifier("LOC-001");
    invalidRequest.setCapacity(100);
    invalidRequest.setStock(150); // Stock > Capacity

    // Act
    Response response = warehouseResource.createANewWarehouseUnit(invalidRequest);

    // Assert
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertEquals("Stock cannot exceed capacity", errorResponse.getError());

    verify(createWarehouseUseCase, never()).create(any(Warehouse.class));
  }

  @Test
  void testGetAWarehouseUnitByID_Success() {
    // Arrange
    when(warehouseStore.findByIdentifier(warehouseId)).thenReturn(sampleWarehouse);

    // Act
    Response response = warehouseResource.getAWarehouseUnitByID(warehouseId);

    // Assert
    assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
    assertNotNull(response.getEntity());

    WarehouseResponse warehouseResponse = (WarehouseResponse) response.getEntity();
    assertEquals("BU-001", warehouseResponse.getBusinessUnitCode());
    assertEquals("LOC-001", warehouseResponse.getLocationIdentifier());
    assertEquals(1000, warehouseResponse.getCapacity());
    assertEquals(500, warehouseResponse.getStock());
    assertEquals("Warehouse BU-001 - LOC-001", warehouseResponse.getName());
    assertFalse(warehouseResponse.isArchived());

    verify(warehouseStore).findByIdentifier(warehouseId);
  }

  @Test
  void testGetAWarehouseUnitByID_NotFound() {
    // Arrange
    String nonExistentId = "WH-" + java.util.UUID.randomUUID().toString();
    when(warehouseStore.findByIdentifier(nonExistentId)).thenReturn(null);

    // Act
    Response response = warehouseResource.getAWarehouseUnitByID(nonExistentId);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertTrue(errorResponse.getError().contains("Warehouse not found with id: " + nonExistentId));

    verify(warehouseStore).findByIdentifier(nonExistentId);
  }

  @Test
  void testGetAWarehouseUnitByID_InvalidIdFormat() {
    // Arrange
    String invalidId = "INVALID-ID";

    // Act
    Response response = warehouseResource.getAWarehouseUnitByID(invalidId);

    // Assert
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertEquals("Invalid warehouse ID format", errorResponse.getError());

    verify(warehouseStore, never()).findByIdentifier(anyString());
  }

  @Test
  void testGetAWarehouseUnitByID_NullId() {
    // Act
    Response response = warehouseResource.getAWarehouseUnitByID(null);

    // Assert
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertEquals("Warehouse ID cannot be null or empty", errorResponse.getError());

    verify(warehouseStore, never()).findByIdentifier(anyString());
  }

  @Test
  void testGetAWarehouseUnitByID_EmptyId() {
    // Act
    Response response = warehouseResource.getAWarehouseUnitByID("");

    // Assert
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertEquals("Warehouse ID cannot be null or empty", errorResponse.getError());

    verify(warehouseStore, never()).findByIdentifier(anyString());
  }

  @Test
  void testUpdateWarehouseUnit_NotFound() {
    // Arrange
    String nonExistentId = "WH-" + java.util.UUID.randomUUID().toString();
    when(warehouseStore.findByIdentifier(nonExistentId)).thenReturn(null);

    // Act
    Response response = warehouseResource.updateWarehouseUnit(nonExistentId, validWarehouseRequest);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertTrue(errorResponse.getError().contains("Warehouse not found with id: " + nonExistentId));

    verify(warehouseStore).findByIdentifier(nonExistentId);
    verify(replaceWarehouseUseCase, never()).replace(any(Warehouse.class));
  }

  @Test
  void testUpdateWarehouseUnit_InvalidIdFormat() {
    // Arrange
    String invalidId = "INVALID-ID";

    // Act
    Response response = warehouseResource.updateWarehouseUnit(invalidId, validWarehouseRequest);

    // Assert
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertEquals("Invalid warehouse ID format", errorResponse.getError());

    verify(warehouseStore, never()).findByIdentifier(anyString());
    verify(replaceWarehouseUseCase, never()).replace(any(Warehouse.class));
  }

  @Test
  void testArchiveAWarehouseUnitByID_Success() {
    // Arrange
    when(warehouseStore.findByIdentifier(warehouseId)).thenReturn(sampleWarehouse);
    doNothing().when(archiveWarehouseUseCase).archive(sampleWarehouse);

    // Act
    Response response = warehouseResource.archiveAWarehouseUnitByID(warehouseId);

    // Assert
    assertEquals(Response.Status.NO_CONTENT.getStatusCode(), response.getStatus());
    assertNull(response.getEntity());

    verify(warehouseStore).findByIdentifier(warehouseId);
    verify(archiveWarehouseUseCase).archive(sampleWarehouse);
  }

  @Test
  void testArchiveAWarehouseUnitByID_NotFound() {
    // Arrange
    String nonExistentId = "WH-" + java.util.UUID.randomUUID().toString();
    when(warehouseStore.findByIdentifier(nonExistentId)).thenReturn(null);

    // Act
    Response response = warehouseResource.archiveAWarehouseUnitByID(nonExistentId);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertTrue(errorResponse.getError().contains("Warehouse not found with id: " + nonExistentId));

    verify(warehouseStore).findByIdentifier(nonExistentId);
    verify(archiveWarehouseUseCase, never()).archive(any(Warehouse.class));
  }

  @Test
  void testArchiveAWarehouseUnitByID_IllegalStateException() {
    // Arrange
    when(warehouseStore.findByIdentifier(warehouseId)).thenReturn(sampleWarehouse);
    doThrow(new IllegalStateException("Warehouse cannot be archived"))
            .when(archiveWarehouseUseCase).archive(sampleWarehouse);

    // Act
    Response response = warehouseResource.archiveAWarehouseUnitByID(warehouseId);

    // Assert
    assertEquals(Response.Status.CONFLICT.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertEquals("Warehouse cannot be archived", errorResponse.getError());

    verify(warehouseStore).findByIdentifier(warehouseId);
    verify(archiveWarehouseUseCase).archive(sampleWarehouse);
  }

  @Test
  void testArchiveAWarehouseUnitByID_IllegalArgumentException() {
    // Arrange
    when(warehouseStore.findByIdentifier(warehouseId)).thenReturn(sampleWarehouse);
    doThrow(new IllegalArgumentException("Invalid archive request"))
            .when(archiveWarehouseUseCase).archive(sampleWarehouse);

    // Act
    Response response = warehouseResource.archiveAWarehouseUnitByID(warehouseId);

    // Assert
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertEquals("Invalid archive request", errorResponse.getError());

    verify(warehouseStore).findByIdentifier(warehouseId);
    verify(archiveWarehouseUseCase).archive(sampleWarehouse);
  }

  @Test
  void testArchiveAWarehouseUnitByID_InvalidIdFormat() {
    // Arrange
    String invalidId = "INVALID-ID";

    // Act
    Response response = warehouseResource.archiveAWarehouseUnitByID(invalidId);

    // Assert
    assertEquals(Response.Status.BAD_REQUEST.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertEquals("Invalid warehouse ID format", errorResponse.getError());

    verify(warehouseStore, never()).findByIdentifier(anyString());
    verify(archiveWarehouseUseCase, never()).archive(any(Warehouse.class));
  }

  @Test
  void testToApiResponse_Conversion() {
    // Act
    WarehouseResponse response = warehouseResource.toApiResponse(sampleWarehouse);

    // Assert
    assertNotNull(response);
    assertEquals("BU-001", response.getBusinessUnitCode());
    assertEquals("LOC-001", response.getLocationIdentifier());
    assertEquals(1000, response.getCapacity());
    assertEquals(500, response.getStock());
    assertEquals("Warehouse BU-001 - LOC-001", response.getName());
    assertFalse(response.isArchived());
  }

  @Test
  void testValidateWarehouseRequest_Valid() {
    // Should not throw exception
    warehouseResource.validateWarehouseRequest(validWarehouseRequest);
  }

  @Test
  void testValidateWarehouseRequest_StockExceedsCapacity() {
    // Arrange
    WarehouseRequest invalidRequest = new WarehouseRequest();
    invalidRequest.setBusinessUnitCode("BU-001");
    invalidRequest.setLocationIdentifier("LOC-001");
    invalidRequest.setCapacity(100);
    invalidRequest.setStock(150);

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> warehouseResource.validateWarehouseRequest(invalidRequest)
    );
    assertEquals("Stock cannot exceed capacity", exception.getMessage());
  }

  @Test
  void testValidateWarehouseId_Valid() {
    // Should not throw exception
    warehouseResource.validateWarehouseId(warehouseId);
  }

  @Test
  void testValidateWarehouseId_Null() {
    // Act & Assert
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> warehouseResource.validateWarehouseId(null)
    );
    assertEquals("Warehouse ID cannot be null or empty", exception.getMessage());
  }

  @Test
  void testValidateWarehouseId_Empty() {
    // Act & Assert
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> warehouseResource.validateWarehouseId("")
    );
    assertEquals("Warehouse ID cannot be null or empty", exception.getMessage());
  }

  @Test
  void testValidateWarehouseId_Blank() {
    // Act & Assert
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> warehouseResource.validateWarehouseId("   ")
    );
    assertEquals("Warehouse ID cannot be null or empty", exception.getMessage());
  }

  @Test
  void testValidateWarehouseId_InvalidFormat() {
    // Arrange
    String invalidId = "INVALID-ID";

    // Act & Assert
    IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> warehouseResource.validateWarehouseId(invalidId)
    );
    assertEquals("Invalid warehouse ID format", exception.getMessage());
  }

  @Test
  void testGenerateWarehouseId() {
    // Act
    String generatedId = warehouseResource.generateWarehouseId();

    // Assert
    assertNotNull(generatedId);
    assertTrue(generatedId.startsWith("WH-"));
    assertEquals(36 + 3, generatedId.length()); // "WH-" + UUID length
  }

  @Test
  void testGenerateWarehouseName() {
    // Act
    String warehouseName = warehouseResource.generateWarehouseName("BU-001", "LOC-001");

    // Assert
    assertEquals("Warehouse BU-001 - LOC-001", warehouseName);
  }

  @Test
  void testBuildNotFoundResponse() {
    // Act
    Response response = warehouseResource.buildNotFoundResponse(warehouseId);

    // Assert
    assertEquals(Response.Status.NOT_FOUND.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertTrue(errorResponse.getError().contains("Warehouse not found with id: " + warehouseId));
  }

  @Test
  void testBuildInternalServerErrorResponse() {
    // Arrange
    Exception exception = new RuntimeException("Database error");

    // Act
    Response response = warehouseResource.buildInternalServerErrorResponse("Operation failed", exception);

    // Assert
    assertEquals(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), response.getStatus());

    ErrorResponse errorResponse = (ErrorResponse) response.getEntity();
    assertTrue(errorResponse.getError().contains("Operation failed"));
    assertTrue(errorResponse.getError().contains("Database error"));
  }
}