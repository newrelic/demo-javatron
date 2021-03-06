package com.newrelic.api.Behaviors;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

public class BehaviorsApplication extends Application
{
   private Set<Object> singletons = new HashSet<Object>();
   private Set<Class<?>> empty = new HashSet<Class<?>>();

   public BehaviorsApplication()
   {
      singletons.add(new BehaviorsResource());
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
