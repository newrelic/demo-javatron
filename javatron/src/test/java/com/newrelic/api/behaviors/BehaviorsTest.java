package com.newrelic.api.behaviors;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BehaviorsTest
{
    @Test
    public void shouldCreateBehavior()
    {
        Behavior behvior = new Behavior();
        assertTrue( behvior != null );
    }
}
