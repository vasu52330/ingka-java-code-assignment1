package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.ports.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import com.fulfilment.application.monolith.warehouses.domain.ports.LocationResolver;
import com.fulfilment.application.monolith.warehouses.domain.ports.BusinessUnitValidator;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseCapacityValidator;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;
  private final LocationResolver locationResolver;
  private final BusinessUnitValidator businessUnitValidator;
  private final WarehouseCapacityValidator capacityValidator;

  @Inject
  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore,
                                 LocationResolver locationResolver,
                                 BusinessUnitValidator businessUnitValidator,
                                 WarehouseCapacityValidator capacityValidator) {
    this.warehouseStore = warehouseStore;
    this.locationResolver = locationResolver;
    this.businessUnitValidator = businessUnitValidator;
    this.capacityValidator = capacityValidator;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    // Get the existing warehouse being replaced
    Warehouse existingWarehouse = warehouseStore.findByIdentifier(newWarehouse.getIdentifier());
    if (existingWarehouse == null) {
      throw new IllegalArgumentException("Warehouse not found: " + newWarehouse.getIdentifier());
    }

    // 1. Business Unit Code Verification (if business unit code is changing)
    if (!existingWarehouse.getBusinessUnitCode().equals(newWarehouse.getBusinessUnitCode()) &&
            !businessUnitValidator.isBusinessUnitCodeUnique(newWarehouse.getBusinessUnitCode())) {
      throw new IllegalArgumentException("Business unit code already exists: " + newWarehouse.getBusinessUnitCode());
    }

    // 2. Location Validation
    Location location = locationResolver.resolveByIdentifier(newWarehouse.getLocationIdentifier());
    if (location == null) {
      throw new IllegalArgumentException("Invalid location identifier: " + newWarehouse.getLocationIdentifier());
    }

    // 3. Capacity and Stock Validation
    if (newWarehouse.getCapacity() > location.getMaxCapacity()) {
      throw new IllegalArgumentException("Warehouse capacity exceeds maximum capacity for location. Max: " +
              location.getMaxCapacity() + ", Requested: " + newWarehouse.getCapacity());
    }

    if (newWarehouse.getCurrentStock() > newWarehouse.getCapacity()) {
      throw new IllegalArgumentException("Current stock exceeds warehouse capacity. Capacity: " +
              newWarehouse.getCapacity() + ", Stock: " + newWarehouse.getCurrentStock());
    }

    // Additional Validations for Replacing a Warehouse

    // 4. Capacity Accommodation
    if (newWarehouse.getCapacity() < existingWarehouse.getCurrentStock()) {
      throw new IllegalArgumentException("New warehouse capacity cannot accommodate existing stock. " +
              "Required: " + existingWarehouse.getCurrentStock() +
              ", New capacity: " + newWarehouse.getCapacity());
    }

    // 5. Stock Matching
    if (newWarehouse.getCurrentStock() != existingWarehouse.getCurrentStock()) {
      throw new IllegalArgumentException("Stock must match existing warehouse stock. " +
              "Existing: " + existingWarehouse.getCurrentStock() +
              ", New: " + newWarehouse.getCurrentStock());
    }

    // Preserve the ID and active status
    newWarehouse.setId(existingWarehouse.getId());
    newWarehouse.setActive(true);
    newWarehouse.setArchived(false);

    // If all validations pass, update the warehouse
    warehouseStore.update(newWarehouse);
  }
}