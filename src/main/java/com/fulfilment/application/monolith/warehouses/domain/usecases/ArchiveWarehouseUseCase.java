package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ArchiveWarehouseUseCase implements ArchiveWarehouseOperation {

  private final WarehouseStore warehouseStore;

  @Inject
  public ArchiveWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void archive(Warehouse warehouse) {
    // Check if warehouse exists
    Warehouse existingWarehouse = warehouseStore.findByIdentifier(warehouse.getIdentifier());
    if (existingWarehouse == null) {
      throw new IllegalArgumentException("Warehouse not found: " + warehouse.getIdentifier());
    }

    // Check if warehouse has any stock
    if (existingWarehouse.getCurrentStock() > 0) {
      throw new IllegalStateException("Cannot archive warehouse with existing stock. " +
              "Current stock: " + existingWarehouse.getCurrentStock());
    }

    // Check if warehouse is already archived
    if (existingWarehouse.isArchived()) {
      throw new IllegalStateException("Warehouse is already archived: " + warehouse.getIdentifier());
    }

    // Archive the warehouse
    existingWarehouse.setArchived(true);
    existingWarehouse.setActive(false);

    warehouseStore.update(existingWarehouse);
  }
}