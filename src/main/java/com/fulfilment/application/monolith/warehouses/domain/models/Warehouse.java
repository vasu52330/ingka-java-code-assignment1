package com.fulfilment.application.monolith.warehouses.domain.models;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse")
public class Warehouse extends PanacheEntity {

    @Column(name = "identifier", unique = true, nullable = false)
    public String identifier;

    @Column(name = "business_unit_code", nullable = false)
    public String businessUnitCode;

    @Column(name = "location_identifier", nullable = false)
    public String locationIdentifier;

    @Column(name = "capacity", nullable = false)
    public Integer capacity;

    @Column(name = "current_stock", nullable = false)
    public Integer currentStock;

    @Column(name = "name")
    public String name;

    @Column(name = "active", nullable = false)
    public Boolean active = true;

    @Column(name = "archived", nullable = false)
    public Boolean archived = false;

    @Column(name = "creation_date")
    public LocalDateTime creationDate = LocalDateTime.now();

    // Public no-arg constructor
    public Warehouse() {
        this.active = false;
        this.archived = false;
    }

    // Private constructor for builder - FIXED: Added identifier
    private Warehouse(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.identifier = builder.identifier;
        this.businessUnitCode = builder.businessUnitCode;
        this.locationIdentifier = builder.locationIdentifier;
        this.capacity = builder.capacity;
        this.currentStock = builder.currentStock;
        this.active = builder.active;
        this.archived = builder.archived;
    }

    // Getters
    public Long getId() {
        return id;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public String getBusinessUnitCode() {
        return businessUnitCode;
    }

    public String getLocationIdentifier() {
        return locationIdentifier;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isArchived() {
        return archived;
    }

    // Setters
    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBusinessUnitCode(String businessUnitCode) {
        this.businessUnitCode = businessUnitCode;
    }

    public void setLocationIdentifier(String locationIdentifier) {
        this.locationIdentifier = locationIdentifier;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    // Static factory method for builder
    public static Builder builder() {
        return new Builder();
    }

    // Builder class
    public static class Builder {
        private Long id;
        private String name;
        private String identifier;
        private String businessUnitCode;
        private String locationIdentifier;
        private int capacity;
        private int currentStock;
        private boolean active = false;
        private boolean archived = false;

        // Private constructor
        private Builder() {}

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder id(long id) {
            this.id = id;
            return this;
        }

        public Builder identifier(String identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder businessUnitCode(String businessUnitCode) {
            this.businessUnitCode = businessUnitCode;
            return this;
        }

        public Builder locationIdentifier(String locationIdentifier) {
            this.locationIdentifier = locationIdentifier;
            return this;
        }

        public Builder capacity(int capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder currentStock(int currentStock) {
            this.currentStock = currentStock;
            return this;
        }

        public Builder active(boolean active) {
            this.active = active;
            return this;
        }

        public Builder archived(boolean archived) {
            this.archived = archived;
            return this;
        }

        public Builder withGeneratedId() {
            if (this.id == null) {
                // For testing purposes only - real IDs come from DB
                this.id = System.currentTimeMillis() % 1000000L;
            }
            return this;
        }

        public Warehouse build() {
            validate();
            return new Warehouse(this);
        }

        private void validate() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Name cannot be null or empty");
            }
            if (businessUnitCode == null || businessUnitCode.trim().isEmpty()) {
                throw new IllegalArgumentException("Business unit code cannot be null or empty");
            }
            if (locationIdentifier == null || locationIdentifier.trim().isEmpty()) {
                throw new IllegalArgumentException("Location identifier cannot be null or empty");
            }
            if (capacity < 0) {
                throw new IllegalArgumentException("Capacity cannot be negative");
            }
            if (currentStock < 0) {
                throw new IllegalArgumentException("Current stock cannot be negative");
            }
            if (currentStock > capacity) {
                throw new IllegalArgumentException("Current stock cannot exceed capacity");
            }
        }
    }

    // Optional: Add toBuilder() method for modification - FIXED: Added identifier
    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .name(this.name)
                .identifier(this.identifier) // ADDED THIS LINE
                .businessUnitCode(this.businessUnitCode)
                .locationIdentifier(this.locationIdentifier)
                .capacity(this.capacity)
                .currentStock(this.currentStock)
                .active(this.active)
                .archived(this.archived);
    }

    // Optional: Helper methods
    public boolean hasId() {
        return id != null;
    }

    @Override
    public String toString() {
        return "Warehouse{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", identifier='" + identifier + '\'' + // ADDED identifier to toString
                ", businessUnitCode='" + businessUnitCode + '\'' +
                ", locationIdentifier='" + locationIdentifier + '\'' +
                ", capacity=" + capacity +
                ", currentStock=" + currentStock +
                ", active=" + active +
                ", archived=" + archived +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Warehouse warehouse = (Warehouse) o;
        return id != null && id.equals(warehouse.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}