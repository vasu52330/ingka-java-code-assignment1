package com.fulfilment.application.monolith.warehouses.domain.models;

public class Location {
  private String identification;
  private String name;  // Added name field
  private int maxNumberOfWarehouses;
  private int maxCapacity;

  // Public no-arg constructor
  public Location() {
  }

  // Original constructor
  public Location(String identification, int maxNumberOfWarehouses, int maxCapacity) {
    this.identification = identification;
    this.maxNumberOfWarehouses = maxNumberOfWarehouses;
    this.maxCapacity = maxCapacity;
  }

  // Constructor with name
  public Location(String identification, String name, int maxNumberOfWarehouses, int maxCapacity) {
    this.identification = identification;
    this.name = name;
    this.maxNumberOfWarehouses = maxNumberOfWarehouses;
    this.maxCapacity = maxCapacity;
  }

  // Private constructor for builder
  private Location(Builder builder) {
    this.identification = builder.identification;
    this.name = builder.name;
    this.maxNumberOfWarehouses = builder.maxNumberOfWarehouses;
    this.maxCapacity = builder.maxCapacity;
  }

  // Static factory method for builder
  public static Builder builder() {
    return new Builder();
  }

  // Builder class
  public static class Builder {
    private String identification;
    private String name;
    private int maxNumberOfWarehouses = 0;  // Default value
    private int maxCapacity = 0;  // Default value

    private Builder() {
    }

    // Alias method for identifier() to match your test code
    public Builder identifier(String identification) {
      this.identification = identification;
      return this;
    }

    // Also keep identification() for consistency
    public Builder identification(String identification) {
      this.identification = identification;
      return this;
    }

    // Name method that you're trying to use
    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder maxNumberOfWarehouses(int maxNumberOfWarehouses) {
      this.maxNumberOfWarehouses = maxNumberOfWarehouses;
      return this;
    }

    public Builder maxCapacity(int maxCapacity) {
      this.maxCapacity = maxCapacity;
      return this;
    }

    public Location build() {
      validate();
      return new Location(this);
    }

    private void validate() {
      if (identification == null || identification.trim().isEmpty()) {
        throw new IllegalArgumentException("Identification cannot be null or empty");
      }
      if (maxNumberOfWarehouses < 0) {
        throw new IllegalArgumentException("Max number of warehouses cannot be negative");
      }
      if (maxCapacity < 0) {
        throw new IllegalArgumentException("Max capacity cannot be negative");
      }
    }
  }

  // Getters
  public String getIdentification() {
    return identification;
  }

  public String getName() {
    return name;
  }

  public int getMaxNumberOfWarehouses() {
    return maxNumberOfWarehouses;
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }

  // Setters
  public void setIdentification(String identification) {
    this.identification = identification;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setMaxNumberOfWarehouses(int maxNumberOfWarehouses) {
    this.maxNumberOfWarehouses = maxNumberOfWarehouses;
  }

  public void setMaxCapacity(int maxCapacity) {
    this.maxCapacity = maxCapacity;
  }

  @Override
  public String toString() {
    return "Location{" +
            "identification='" + identification + '\'' +
            ", name='" + name + '\'' +
            ", maxNumberOfWarehouses=" + maxNumberOfWarehouses +
            ", maxCapacity=" + maxCapacity +
            '}';
  }
}