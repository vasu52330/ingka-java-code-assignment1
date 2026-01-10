package com.fulfilment.application.monolith.Validation;

import com.fulfilment.application.monolith.warehouses.domain.ports.BusinessUnitValidator;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.HashSet;
import java.util.Set;

@ApplicationScoped
public class BusinessUnitValidatorImpl implements BusinessUnitValidator {

    // In a real application, this would come from a database or configuration
    private static final Set<String> REGISTERED_BUSINESS_UNITS = new HashSet<>();

    static {
        // Pre-registered business units
        REGISTERED_BUSINESS_UNITS.add("BU-001");
        REGISTERED_BUSINESS_UNITS.add("BU-002");
        REGISTERED_BUSINESS_UNITS.add("BU-003");
        REGISTERED_BUSINESS_UNITS.add("BU-004");
        REGISTERED_BUSINESS_UNITS.add("BU-005");
    }

    @Override
    public boolean isBusinessUnitCodeUnique(String businessUnitCode) {
        if (businessUnitCode == null || businessUnitCode.trim().isEmpty()) {
            return false;
        }
        // Check if the business unit code is not already registered
        return !REGISTERED_BUSINESS_UNITS.contains(businessUnitCode.trim());
    }
}