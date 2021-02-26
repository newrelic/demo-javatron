package com.newrelic.api.Health;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class HealthApplication extends Application
{
   private Set<Object> singletons = new HashSet<Object>();
   private Set<Class<?>> empty = new HashSet<Class<?>>();

   public HealthApplication()
   {
      singletons.add(new HealthResource());
   }

   @Override
   public Set<Class<?>> getClasses()
   {
      return empty;
   }

   @Override
   public Set<Object> getSingletons()
   {
      return singletons;
   }

}
