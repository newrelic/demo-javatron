package com.newrelic.api.behaviors;

import java.util.*;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import org.junit.*;

public class BehaviorsRepositoryTest
{
    @Test
    public void shouldCreateRepository()
    {
        GivenRepository();
        assertTrue( Repository != null );
    }

    @Test
    public void ShouldFindEmptyRepository()
    {
        GivenRepository();
        var behaviors = Repository.FindAllNames();
        assertTrue( behaviors != null );
        assertTrue( behaviors.size() == 0 );
    }

    @Test
    public void ShouldFindOne()
    {
        GivenItem("Behavior_1");
        GivenRepository();
        List<String> result = Repository.FindAllNames();
        assertTrue( result != null );
        assertTrue( result.lastIndexOf("Behavior_1") != -1 );
    }

    @Test
    public void ShouldFindTwo()
    {
        GivenItem("Behavior_1");
        GivenItem("Behavior_2");
        GivenRepository();
        List<String> result = Repository.FindAllNames();
        assertTrue( result != null );
        assertTrue( result.lastIndexOf("Behavior_1") != -1 );
        assertTrue( result.lastIndexOf("Behavior_2") != -1 );
    }

    @Test
    public void ShouldCreateThrow()
    {
        GivenItem(ThrowBehavior.Name);
        GivenRepository();
        var behavior = Repository.Create("THROW");
        assertTrue( behavior != null );
        assertTrue( behavior.getName() == "THROW" );
    }

    @Test
    public void ShouldCreateThrowCaseInsensitive()
    {
        GivenItem(ThrowBehavior.Name);
        GivenRepository();
        var behavior = Repository.Create("Throw");
        assertTrue( behavior != null );
        assertTrue( behavior.getName() == "THROW" );
    }

    @Before
    public void Init()
    {
        _items = new ArrayList<>( 
                List.of()
            );
        Repository = null;
    }

    public BehaviorsRepository GivenRepository()
    {
        if (Repository == null)
        {
            Repository = new BehaviorsRepository(GetLoader());
        }
        return Repository;
    }

    public void GivenItem(String name)
    {
        _items.add(name);
    }

    public Supplier<List<String>> GetLoader()
    {
        return () -> _items;
    }

    private List<String> _items;
    private BehaviorsRepository Repository;

}
