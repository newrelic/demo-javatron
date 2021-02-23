package com.newrelic.api.Inventory;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.newrelic.api.ResourceBase;
import com.newrelic.lib.Logger;
import com.newrelic.lib.Inventory.InventoryManagerFactory;

@Path("/inventory")
public class InventoryResource extends ResourceBase
{
   @GET
   @Produces("application/json")
   public Response get() throws Exception
   {
      EnsureAppIsStarted();
      Logger.GetOrCreate().Info("/api/inventory GET");
      GetBehaviorService().HandlePreFunc();

      var appConfig = GetAppConfigRepository();
      var manager = InventoryManagerFactory.createInventoryManager(appConfig);
      var repository = new InventoryRepository(manager);
      var entities = repository.FindAll();
      var handler = CreateTronHandler();
      handler.InvokeDependencies("/api/inventory");
      var response = handler.CreateResponse(entities);
      GetBehaviorService().HandlePostFunc();
      return response;
   }

   @GET
   @Path("{id}")
   @Produces("application/json")
   public Response getById(@PathParam("id") String id) throws Exception
   {
      EnsureAppIsStarted();
      Logger.GetOrCreate().Info("/api/inventory/"+id +" GET");
      GetBehaviorService().HandlePreFunc();

      var appConfig = GetAppConfigRepository();
      var manager = InventoryManagerFactory.createInventoryManager(appConfig);
      var repository = new InventoryRepository(manager);
      var entity = repository.FindOrNull(id);
      var handler = CreateTronHandler();
      handler.InvokeDependencies("/api/inventory/"+id);
      var response = handler.CreateResponse(entity);
      GetBehaviorService().HandlePostFunc();
      return response;
   }
}
