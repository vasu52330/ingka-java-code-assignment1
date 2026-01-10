package com.fulfilment.application.monolith.Validation;

import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseCapacityValidator;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.HashMap;

@ApplicationScoped
public class WarehouseCapacityValidatorImpl implements WarehouseCapacityValidator {

    private static final Map<String, LocationCapacity> LOCATION_CAPACITIES = new HashMap<>();

    static {
        LOCATION_CAPACITIES.put("ZWOLLE-001", new LocationCapacity(5, 500));
        LOCATION_CAPACITIES.put("ZWOLLE-002", new LocationCapacity(5, 500));
        LOCATION_CAPACITIES.put("AMSTERDAM-001", new LocationCapacity(10, 1000));
        LOCATION_CAPACITIES.put("AMSTERDAM-002", new LocationCapacity(8, 800));
        LOCATION_CAPACITIES.put("TILBURG-001", new LocationCapacity(3, 300));
        LOCATION_CAPACITIES.put("HELMOND-001", new LocationCapacity(3, 300));
        LOCATION_CAPACITIES.put("EINDHOVEN-001", new LocationCapacity(5, 500));
        LOCATION_CAPACITIES.put("VETSBY-001", new LocationCapacity(2, 200));
    }

    @Override
    public boolean canCreateWarehouseAtLocation(String locationIdentifier, int currentWarehouseCount) {
        LocationCapacity capacity = LOCATION_CAPACITIES.get(locationIdentifier);
        if (capacity == null) {
            return false;
        }
        return currentWarehouseCount < capacity.getMaxWarehouses();
    }

    @Override
    public boolean hasSufficientCapacity(String locationIdentifier, int requestedCapacity) {
        LocationCapacity capacity = LOCATION_CAPACITIES.get(locationIdentifier);
        if (capacity == null) {
            return false;
        }
        // For simplicity, we're assuming each warehouse uses capacity
        // In reality, you might track used capacity differently
        return requestedCapacity <= capacity.getTotalCapacity();
    }

    private static class LocationCapacity {
        private final int maxWarehouses;
        private final int totalCapacity;

        public LocationCapacity(int maxWarehouses, int totalCapacity) {
            this.maxWarehouses = maxWarehouses;
            this.totalCapacity = totalCapacity;
        }

        public int getMaxWarehouses() {
            return maxWarehouses;
        }

        public int getTotalCapacity() {
            return totalCapacity;
        }
    }
}