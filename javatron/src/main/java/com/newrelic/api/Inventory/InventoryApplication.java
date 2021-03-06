package com.newrelic.api.Inventory;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class InventoryApplication extends Application
{
   private Set<Object> singletons = new HashSet<Object>();
   private Set<Class<?>> empty = new HashSet<Class<?>>();

   public InventoryApplication()
   {
      singletons.add(new InventoryResource());
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
