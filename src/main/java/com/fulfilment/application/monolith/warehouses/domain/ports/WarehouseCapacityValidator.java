package com.fulfilment.application.monolith.warehouses.domain.ports;

public interface WarehouseCapacityValidator {

    boolean canCreateWarehouseAtLocation(String locationIdentifier, int currentWarehouseCount);
    boolean hasSufficientCapacity(String locationIdentifier, int requiredCapacity);
}
