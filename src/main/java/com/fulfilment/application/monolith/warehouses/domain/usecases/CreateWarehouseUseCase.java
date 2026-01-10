package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.BusinessUnitValidator;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseCapacityValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;
  private final BusinessUnitValidator businessUnitValidator;
  private final WarehouseCapacityValidator capacityValidator;

  @Inject
  public CreateWarehouseUseCase(WarehouseStore warehouseStore,
                                LocationResolver locationResolver,
                                BusinessUnitValidator businessUnitValidator,
                                WarehouseCapacityValidator capacityValidator) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
    this.businessUnitValidator = businessUnitValidator;
    this.capacityValidator = capacityValidator;
  }

  @Override
  public void create(Warehouse warehouse) {
    // 1. Business Unit Code Verification
    if (!businessUnitValidator.isBusinessUnitCodeUnique(warehouse.getBusinessUnitCode())) {
      throw new IllegalArgumentException("Business unit code already exists: " + warehouse.getBusinessUnitCode());
    }

    // 2. Location Validation
    Location location = locationResolver.resolveByIdentifier(warehouse.getLocationIdentifier());
    if (location == null) {
      throw new IllegalArgumentException("Invalid location identifier: " + warehouse.getLocationIdentifier());
    }

    // 3. Warehouse Creation Feasibility
    int currentWarehouseCount = warehouseStore.countWarehousesAtLocation(warehouse.getLocationIdentifier());
    if (!capacityValidator.canCreateWarehouseAtLocation(warehouse.getLocationIdentifier(), currentWarehouseCount)) {
      throw new IllegalArgumentException("Maximum number of warehouses reached at location: " + warehouse.getLocationIdentifier());
    }

    // 4. Capacity and Stock Validation
    if (warehouse.getCapacity() > location.getMaxCapacity()) {
      throw new IllegalArgumentException("Warehouse capacity exceeds maximum capacity for location. Max: " +
              location.getMaxCapacity() + ", Requested: " + warehouse.getCapacity());
    }

    if (warehouse.getCurrentStock() > warehouse.getCapacity()) {
      throw new IllegalArgumentException("Current stock exceeds warehouse capacity. Capacity: " +
              warehouse.getCapacity() + ", Stock: " + warehouse.getCurrentStock());
    }

    if (!capacityValidator.hasSufficientCapacity(warehouse.getLocationIdentifier(), warehouse.getCapacity())) {
      throw new IllegalArgumentException("Insufficient capacity at location for new warehouse");
    }

    // Set warehouse as active
    warehouse.setActive(true);
    warehouse.setArchived(false);

    // If all validations pass, create the warehouse
    warehouseStore.create(warehouse);
  }
}