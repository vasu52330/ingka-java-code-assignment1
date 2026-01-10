package com.fulfilment.application.monolith.products;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.quarkus.panache.common.Sort;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductResourceTest {

  @Mock
  private ProductRepository productRepository;

  @Mock
  private Logger logger;

  @InjectMocks
  private ProductResource productResource;

  private Product sampleProduct;
  private Product sampleProduct2;

  @BeforeEach
  void setUp() {
    // Reset the mocks
    reset(productRepository, logger);

    sampleProduct = new Product();
    sampleProduct.id = 1L;
    sampleProduct.name = "Test Product";
    sampleProduct.description = "Test Description";
    sampleProduct.price = BigDecimal.valueOf(99.99);
    sampleProduct.stock = 100;

    sampleProduct2 = new Product();
    sampleProduct2.id = 2L;
    sampleProduct2.name = "Another Product";
    sampleProduct2.description = "Another Description";
    sampleProduct2.price = BigDecimal.valueOf(49.99);
    sampleProduct2.stock = 50;
  }

  @Test
  void testGet_ReturnsAllProducts() {
    // Arrange
    List<Product> productList = Arrays.asList(sampleProduct, sampleProduct2);
    when(productRepository.listAll(any(Sort.class))).thenReturn(productList);

    // Act
    List<Product> result = productResource.get();

    // Assert
    assertNotNull(result);
    assertEquals(2, result.size());
    assertEquals("Test Product", result.get(0).name);
    assertEquals("Another Product", result.get(1).name);

    verify(productRepository).listAll(any(Sort.class));
  }

  @Test
  void testGet_ReturnsEmptyList() {
    // Arrange
    when(productRepository.listAll(any(Sort.class))).thenReturn(Collections.emptyList());

    // Act
    List<Product> result = productResource.get();

    // Assert
    assertNotNull(result);
    assertTrue(result.isEmpty());

    verify(productRepository).listAll(any(Sort.class));
  }

  @Test
  void testGetSingle_ReturnsProduct() {
    // Arrange
    Long productId = 1L;
    when(productRepository.findById(productId)).thenReturn(sampleProduct);

    // Act
    Product result = productResource.getSingle(productId);

    // Assert
    assertNotNull(result);
    assertEquals(productId, result.id);
    assertEquals("Test Product", result.name);
    assertEquals("Test Description", result.description);
    assertEquals(100, result.stock);

    verify(productRepository).findById(productId);
  }

  @Test
  void testGetSingle_ThrowsWebApplicationException_WhenProductNotFound() {
    // Arrange
    Long nonExistentId = 999L;
    when(productRepository.findById(nonExistentId)).thenReturn(null);

    // Act & Assert
    WebApplicationException exception = assertThrows(
            WebApplicationException.class,
            () -> productResource.getSingle(nonExistentId)
    );

    assertEquals(404, exception.getResponse().getStatus());
    assertTrue(exception.getMessage().contains("Product with id of " + nonExistentId + " does not exist."));

    verify(productRepository).findById(nonExistentId);
  }

  @Test
  void testCreate_Success() {
    // Arrange
    Product newProduct = new Product();
    newProduct.name = "New Product";
    newProduct.description = "New Description";
    newProduct.price = BigDecimal.valueOf(29.99);
    newProduct.stock = 25;
    newProduct.id = null; // ID should be null for create

    doNothing().when(productRepository).persist(newProduct);

    // Act
    Response response = productResource.create(newProduct);

    // Assert
    assertNotNull(response);
    assertEquals(201, response.getStatus());
    assertNotNull(response.getEntity());
    assertEquals(newProduct, response.getEntity());

    verify(productRepository).persist(newProduct);
  }

  @Test
  void testCreate_ThrowsWebApplicationException_WhenIdIsNotNull() {
    // Arrange
    Product productWithId = new Product();
    productWithId.id = 100L; // ID should be null for create
    productWithId.name = "Product with ID";

    // Act & Assert
    WebApplicationException exception = assertThrows(
            WebApplicationException.class,
            () -> productResource.create(productWithId)
    );

    assertEquals(422, exception.getResponse().getStatus());
    assertEquals("Id was invalidly set on request.", exception.getMessage());

    verify(productRepository, never()).persist(any(Product.class));
  }

  @Test
  void testUpdate_Success() {
    // Arrange
    Long productId = 1L;
    Product updatedProduct = new Product();
    updatedProduct.name = "Updated Product Name";
    updatedProduct.description = "Updated Description";
    updatedProduct.price = BigDecimal.valueOf(149.99);
    updatedProduct.stock = 75;

    when(productRepository.findById(productId)).thenReturn(sampleProduct);

    // Act
    Product result = productResource.update(productId, updatedProduct);

    // Assert
    assertNotNull(result);
    assertEquals(productId, result.id);
    assertEquals("Updated Product Name", result.name);
    assertEquals("Updated Description", result.description);
    assertEquals(75, result.stock);

    verify(productRepository).findById(productId);
    verify(productRepository).persist(sampleProduct);
  }

  @Test
  void testUpdate_ThrowsWebApplicationException_WhenNameIsNull() {
    // Arrange
    Long productId = 1L;
    Product productWithoutName = new Product();
    productWithoutName.name = null;
    productWithoutName.description = "Some description";

    // Act & Assert
    WebApplicationException exception = assertThrows(
            WebApplicationException.class,
            () -> productResource.update(productId, productWithoutName)
    );

    assertEquals(422, exception.getResponse().getStatus());
    assertEquals("Product Name was not set on request.", exception.getMessage());

    verify(productRepository, never()).findById(anyLong());
    verify(productRepository, never()).persist(any(Product.class));
  }

  @Test
  void testUpdate_ThrowsWebApplicationException_WhenProductNotFound() {
    // Arrange
    Long nonExistentId = 999L;
    Product updatedProduct = new Product();
    updatedProduct.name = "Updated Name";

    when(productRepository.findById(nonExistentId)).thenReturn(null);

    // Act & Assert
    WebApplicationException exception = assertThrows(
            WebApplicationException.class,
            () -> productResource.update(nonExistentId, updatedProduct)
    );

    assertEquals(404, exception.getResponse().getStatus());
    assertTrue(exception.getMessage().contains("Product with id of " + nonExistentId + " does not exist."));

    verify(productRepository).findById(nonExistentId);
    verify(productRepository, never()).persist(any(Product.class));
  }

  @Test
  void testDelete_Success() {
    // Arrange
    Long productId = 1L;
    when(productRepository.findById(productId)).thenReturn(sampleProduct);

    // Act
    Response response = productResource.delete(productId);

    // Assert
    assertNotNull(response);
    assertEquals(204, response.getStatus());
    assertNull(response.getEntity());

    verify(productRepository).findById(productId);
    verify(productRepository).delete(sampleProduct);
  }

  @Test
  void testDelete_ThrowsWebApplicationException_WhenProductNotFound() {
    // Arrange
    Long nonExistentId = 999L;
    when(productRepository.findById(nonExistentId)).thenReturn(null);

    // Act & Assert
    WebApplicationException exception = assertThrows(
            WebApplicationException.class,
            () -> productResource.delete(nonExistentId)
    );

    assertEquals(404, exception.getResponse().getStatus());
    assertTrue(exception.getMessage().contains("Product with id of " + nonExistentId + " does not exist."));

    verify(productRepository).findById(nonExistentId);
    verify(productRepository, never()).delete(any(Product.class));
  }

  @Test
  void testUpdate_WithPartialFields() {
    // Arrange
    Long productId = 1L;
    Product updatedProduct = new Product();
    updatedProduct.name = "Updated Name Only";
    // description, price, stock are null

    when(productRepository.findById(productId)).thenReturn(sampleProduct);

    // Act
    Product result = productResource.update(productId, updatedProduct);

    // Assert
    assertEquals("Updated Name Only", result.name);
    assertNull(result.description); // Should be null from update request
    assertNull(result.price); // Should be null from update request

    verify(productRepository).findById(productId);
    verify(productRepository).persist(sampleProduct);
  }
}