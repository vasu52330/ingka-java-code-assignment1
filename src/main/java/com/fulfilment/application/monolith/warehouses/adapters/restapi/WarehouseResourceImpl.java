package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.usecases.CreateWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ReplaceWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.usecases.ArchiveWarehouseUseCase;
import com.fulfilment.application.monolith.warehouses.domain.ports.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Path("/warehouses")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@ApplicationScoped
public class WarehouseResourceImpl {

    private static final String WAREHOUSE_ID_PREFIX = "WH-";

    @Inject
    CreateWarehouseUseCase createWarehouseUseCase;

    @Inject
    ReplaceWarehouseUseCase replaceWarehouseUseCase;

    @Inject
    ArchiveWarehouseUseCase archiveWarehouseUseCase;

    @Inject
    WarehouseStore warehouseStore;

    @GET
    public Response listAllWarehousesUnits() {
        try {
            List<Warehouse> warehouses = warehouseStore.findAllActive();
            List<WarehouseResponse> response = warehouses.stream()
                    .map(this::toApiResponse)
                    .collect(Collectors.toList());
            return Response.ok(response).build();
        } catch (Exception e) {
            return buildInternalServerErrorResponse("Failed to retrieve warehouses", e);
        }
    }

    @POST
    @Transactional
    public Response createANewWarehouseUnit(@Valid WarehouseRequest request) {
        try {
            validateWarehouseRequest(request);

            String warehouseId = generateWarehouseId();
            String warehouseName = generateWarehouseName(request.getBusinessUnitCode(), request.getLocationIdentifier());

            Warehouse domainWarehouse = Warehouse.builder()
                    .identifier(warehouseId)
                    .businessUnitCode(request.getBusinessUnitCode())
                    .locationIdentifier(request.getLocationIdentifier())
                    .capacity(request.getCapacity())
                    .currentStock(request.getStock())
                    .name(warehouseName)
                    .active(true)
                    .archived(false)
                    .build();

            createWarehouseUseCase.create(domainWarehouse);

            WarehouseResponse response = toApiResponse(domainWarehouse);
            return Response.status(Response.Status.CREATED).entity(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return buildInternalServerErrorResponse("Failed to create warehouse", e);
        }
    }

    @GET
    @Path("/{id}")
    public Response getAWarehouseUnitByID(@PathParam("id") String id) {
        try {
            validateWarehouseId(id);

            Warehouse domainWarehouse = warehouseStore.findByIdentifier(id);
            if (domainWarehouse == null) {
                return buildNotFoundResponse(id);
            }

            WarehouseResponse response = toApiResponse(domainWarehouse);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return buildInternalServerErrorResponse("Failed to retrieve warehouse", e);
        }
    }

    @PUT
    @Path("/{id}")
    @Transactional
    public Response updateWarehouseUnit(@PathParam("id") String id, @Valid WarehouseRequest request) {
        try {
            validateWarehouseId(id);
            validateWarehouseRequest(request);

            Warehouse existingWarehouse = warehouseStore.findByIdentifier(id);
            if (existingWarehouse == null) {
                return buildNotFoundResponse(id);
            }

            Warehouse updatedWarehouse = Warehouse.builder()
                    .identifier(id)
                    .businessUnitCode(request.getBusinessUnitCode())
                    .locationIdentifier(request.getLocationIdentifier())
                    .capacity(request.getCapacity())
                    .currentStock(request.getStock())
                    .name(existingWarehouse.getName())
                    .active(true)
                    .archived(false)
                    .build();

            replaceWarehouseUseCase.replace(updatedWarehouse);

            WarehouseResponse response = toApiResponse(updatedWarehouse);
            return Response.ok(response).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return buildInternalServerErrorResponse("Failed to update warehouse", e);
        }
    }

    @DELETE
    @Path("/{id}")
    @Transactional
    public Response archiveAWarehouseUnitByID(@PathParam("id") String id) {
        try {
            validateWarehouseId(id);

            Warehouse domainWarehouse = warehouseStore.findByIdentifier(id);
            if (domainWarehouse == null) {
                return buildNotFoundResponse(id);
            }

            archiveWarehouseUseCase.archive(domainWarehouse);

            return Response.noContent().build();
        } catch (IllegalStateException e) {
            return Response.status(Response.Status.CONFLICT)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorResponse(e.getMessage()))
                    .build();
        } catch (Exception e) {
            return buildInternalServerErrorResponse("Failed to archive warehouse", e);
        }
    }

    // Helper Methods

    void validateWarehouseId(String id) {
        if (id == null || id.trim().isEmpty()) {
            throw new IllegalArgumentException("Warehouse ID cannot be null or empty");
        }
        if (!id.startsWith(WAREHOUSE_ID_PREFIX)) {
            throw new IllegalArgumentException("Invalid warehouse ID format");
        }
    }

    void validateWarehouseRequest(WarehouseRequest request) {
        if (request.getStock() > request.getCapacity()) {
            throw new IllegalArgumentException("Stock cannot exceed capacity");
        }
    }

    String generateWarehouseId() {
        return WAREHOUSE_ID_PREFIX + UUID.randomUUID().toString();
    }

    String generateWarehouseName(String businessUnitCode, String location) {
        return String.format("Warehouse %s - %s", businessUnitCode, location);
    }

    WarehouseResponse toApiResponse(Warehouse domainWarehouse) {
        WarehouseResponse response = new WarehouseResponse();
        response.setId(String.valueOf(domainWarehouse.getId()));
        response.setIdentifier(domainWarehouse.getIdentifier());
        response.setActive(domainWarehouse.isActive());
        response.setCreationDate(domainWarehouse.creationDate);
        response.setBusinessUnitCode(domainWarehouse.getBusinessUnitCode());
        response.setLocationIdentifier(domainWarehouse.getLocationIdentifier());
        response.setCapacity(domainWarehouse.getCapacity());
        response.setStock(domainWarehouse.getCurrentStock());
        response.setArchived(domainWarehouse.isArchived());
        response.setName(domainWarehouse.getName());
        return response;
    }

    Response buildNotFoundResponse(String id) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity(new ErrorResponse("Warehouse not found with id: " + id))
                .build();
    }

    Response buildInternalServerErrorResponse(String message, Exception e) {
        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(new ErrorResponse(message + ": " + e.getMessage()))
                .build();
    }
}