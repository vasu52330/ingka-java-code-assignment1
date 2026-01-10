// WarehouseStore.java
package com.fulfilment.application.monolith.warehouses.domain.ports;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import java.util.List;

public interface WarehouseStore {
  void create(Warehouse warehouse);
  void update(Warehouse warehouse);
  Warehouse findByIdentifier(String identifier);
  Warehouse findByBusinessUnitCode(String businessUnitCode);
  int countWarehousesAtLocation(String locationIdentifier);
  int getTotalCapacityAtLocation(String locationIdentifier);
  List<Warehouse> findAllByLocation(String locationIdentifier);
  List<Warehouse> findAllActive();
}