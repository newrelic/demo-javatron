package com.newrelic.api.message;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import com.newrelic.api.ResourceBase;
import com.newrelic.lib.Logger;

@Path("/validateMessage")
public class MessageResource extends ResourceBase
{
   @GET
   @Produces("application/json")
   public Response get() throws Exception
   {
      EnsureAppIsStarted();
      Logger.GetOrCreate().Info("/api/validateMessage GET");
      GetBehaviorService().HandlePreFunc();
      var message = new Message();
      message.setResult(true);
      var handler = CreateTronHandler();
      handler.InvokeDependencies("/api/validateMessage");
      var response = handler.CreateResponse(message);
      GetBehaviorService().HandlePostFunc();
      return response;
   }
}
