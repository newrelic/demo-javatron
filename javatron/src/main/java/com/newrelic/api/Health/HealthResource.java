package com.newrelic.api.Health;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import com.newrelic.api.ResourceBase;
import com.newrelic.lib.Logger;
import com.newrelic.lib.Inventory.MySQLRepository;

@Path("/database/health")
public class HealthResource extends ResourceBase
{
   @GET
   @Produces("application/json")
   public Response get() throws Exception
   {
      EnsureAppIsStarted();
      Logger.GetOrCreate().Info("/api/database/health GET");
      var appConfig = GetAppConfigRepository();
      var mySQLConfig = appConfig.FindMySQLConfiguration();
      var handler = CreateTronHandler();
      var isConnected = false;
      if (!mySQLConfig.isConfigured())
      {
         throw new Exception("Database is not configured.");
      }

      var repo = MySQLRepository.getInstance(appConfig.FindMySQLConfiguration());
      isConnected = repo.isConnected();

      if (!isConnected)
      {
         throw new Exception("Unable to query the database.");
      }
      handler.InvokeDependencies("/api/database/health");
      var response = handler.CreateResponse(isConnected);
      return response;
   }
}
