package com.newrelic.api.Behaviors;

import static org.junit.Assert.assertTrue;

import org.junit.*;

public class MallocBehaviorTest
{
    @Test
    public void shouldCreateBehavior()
    {
        var behavior = GivenBehavior("");
        assertTrue( behavior != null );
    }

    public MallocBehavior GivenBehavior(String json)
    {
        return new MallocBehavior(json);
    }

}
