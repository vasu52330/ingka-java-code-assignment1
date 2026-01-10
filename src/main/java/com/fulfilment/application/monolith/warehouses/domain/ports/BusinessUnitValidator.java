package com.fulfilment.application.monolith.warehouses.domain.ports;

public interface BusinessUnitValidator {

    boolean isBusinessUnitCodeUnique(String businessUnitCode);
}
