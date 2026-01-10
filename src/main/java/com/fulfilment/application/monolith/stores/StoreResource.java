package com.fulfilment.application.monolith.stores;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/stores")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StoreResource {

  @Inject
  LegacyStoreManagerGateway legacyStoreManagerGateway;

  @Inject
  EntityManager entityManager;

  @POST
  @Transactional
  public Response createStore(Store store) {
    try {
      // Persist the store to database
      entityManager.persist(store);

      // Flush to ensure the data is written to database
      entityManager.flush();

      // Now that transaction is committed, call the legacy system
      legacyStoreManagerGateway.createStoreOnLegacySystem(store);

      return Response.status(Response.Status.CREATED).entity(store).build();

    } catch (Exception e) {
      // Transaction will be rolled back automatically
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity("Error creating store: " + e.getMessage())
              .build();
    }
  }

  @PUT
  @Path("/{id}")
  @Transactional
  public Response updateStore(@PathParam("id") Long id, Store updatedStore) {
    try {
      Store existingStore = entityManager.find(Store.class, id);
      if (existingStore == null) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("Store not found with id: " + id)
                .build();
      }

      // Update the store
      existingStore.setName(updatedStore.getName());
      existingStore.setQuantityProductsInStock(updatedStore.getQuantityProductsInStock());

      // Merge and flush to ensure data is written
      entityManager.merge(existingStore);
      entityManager.flush();

      // Now that transaction is committed, call the legacy system
      legacyStoreManagerGateway.updateStoreOnLegacySystem(existingStore);

      return Response.ok(existingStore).build();

    } catch (Exception e) {
      // Transaction will be rolled back automatically
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
              .entity("Error updating store: " + e.getMessage())
              .build();
    }
  }
}