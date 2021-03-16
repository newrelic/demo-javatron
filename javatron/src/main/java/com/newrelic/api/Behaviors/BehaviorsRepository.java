package com.newrelic.api.Behaviors;

import java.util.*;
import java.util.function.Supplier;

import com.newrelic.lib.AppConfigRepository;
import com.newrelic.lib.ApplicationContainer;
import com.newrelic.lib.Inventory.MySQLRepository;

public class BehaviorsRepository implements IBehaviorRepository
{
    private Supplier<List<String>> _loader;

    public BehaviorsRepository()
    {
        _loader = () -> new ArrayList<>( 
                List.of(ThrowBehavior.Name, ComputeBehavior.Name, MallocBehavior.Name, InvalidQueryBehavior.Name)
            );
    }

    public BehaviorsRepository(Supplier<List<String>> loader)
    {
        _loader = loader;
    }

    public List<String> FindAllNames()
    {
        List<String> behaviorNames = _loader.get();
        return behaviorNames;
    }

    public Behavior Create(String name)
    {
        return Create(name, null);
    }
    
    public Behavior Create(String name, String value)
    {

        switch(name.toUpperCase())
        {
            case ThrowBehavior.Name:
                return new ThrowBehavior();
            case ComputeBehavior.Name:
                return new ComputeBehavior(value);
            case MallocBehavior.Name:
                return new MallocBehavior(value);
            case InvalidQueryBehavior.Name:
                var config = ApplicationContainer.getInstance().GetAppConfigRepository();
                var mysqlConfig = config.FindMySQLConfiguration();
                var mysqlRepo = MySQLRepository.getInstance(mysqlConfig);
                return new InvalidQueryBehavior(mysqlRepo);
        }
        return null;
    }

}
