// Store.java (if not exists)
package com.fulfilment.application.monolith.stores;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Store {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public String name;
  public int quantityProductsInStock;

  // Constructors
  public Store() {}

  public Store(String name, int quantityProductsInStock) {
    this.name = name;
    this.quantityProductsInStock = quantityProductsInStock;
  }

  // Getters and setters
  public Long getId() { return id; }
  public void setId(Long id) { this.id = id; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public int getQuantityProductsInStock() { return quantityProductsInStock; }
  public void setQuantityProductsInStock(int quantityProductsInStock) {
    this.quantityProductsInStock = quantityProductsInStock;
  }
}