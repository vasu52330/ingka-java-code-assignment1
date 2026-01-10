// WarehouseRepository.java
package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore {

  @Inject
  EntityManager entityManager;

  @Override
  public void create(Warehouse warehouse) {
    entityManager.persist(warehouse);
  }

  @Override
  public void update(Warehouse warehouse) {
    entityManager.merge(warehouse);
  }

  @Override
  public Warehouse findByIdentifier(String identifier) {
    return entityManager.createQuery(
                    "SELECT w FROM Warehouse w WHERE w.identifier = :identifier", Warehouse.class)
            .setParameter("identifier", identifier)
            .getResultStream()
            .findFirst()
            .orElse(null);
  }

  @Override
  public Warehouse findByBusinessUnitCode(String businessUnitCode) {
    return entityManager.createQuery(
                    "SELECT w FROM Warehouse w WHERE w.businessUnitCode = :businessUnitCode AND w.active = true", Warehouse.class)
            .setParameter("businessUnitCode", businessUnitCode)
            .getResultStream()
            .findFirst()
            .orElse(null);
  }

  @Override
  public int countWarehousesAtLocation(String locationIdentifier) {
    Long count = entityManager.createQuery(
                    "SELECT COUNT(w) FROM Warehouse w WHERE w.locationIdentifier = :locationIdentifier AND w.active = true", Long.class)
            .setParameter("locationIdentifier", locationIdentifier)
            .getSingleResult();
    return count != null ? count.intValue() : 0;
  }

  @Override
  public int getTotalCapacityAtLocation(String locationIdentifier) {
    Integer totalCapacity = entityManager.createQuery(
                    "SELECT SUM(w.capacity) FROM Warehouse w WHERE w.locationIdentifier = :locationIdentifier AND w.active = true", Integer.class)
            .setParameter("locationIdentifier", locationIdentifier)
            .getSingleResult();
    return totalCapacity != null ? totalCapacity : 0;
  }

  @Override
  public List<Warehouse> findAllByLocation(String locationIdentifier) {
    return entityManager.createQuery(
                    "SELECT w FROM Warehouse w WHERE w.locationIdentifier = :locationIdentifier AND w.active = true", Warehouse.class)
            .setParameter("locationIdentifier", locationIdentifier)
            .getResultList();
  }

  @Override
  public List<Warehouse> findAllActive() {
    return entityManager.createQuery(
                    "SELECT w FROM Warehouse w WHERE w.active = true", Warehouse.class)
            .getResultList();
  }
}