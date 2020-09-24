package com.newrelic.api.behaviors;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import com.newrelic.api.ResourceBase;
import com.newrelic.lib.Logger;

@Path("/behaviors")
public class BehaviorsResource extends ResourceBase
{
   @GET
   @Produces("application/json")
   public Response get() throws Exception
   {
      Logger.GetOrCreate().Info("/api/behaviors GET");
      var repository = new BehaviorsRepository();
      var entities = repository.FindAllNames();
      var handler = CreateTronHandler();
      handler.InvokeDependencies("/api/behaviors");
      return handler.CreateResponse(entities);
   }

}
