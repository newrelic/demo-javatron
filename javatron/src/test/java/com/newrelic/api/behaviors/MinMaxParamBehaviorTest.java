package com.newrelic.api.Behaviors;

import org.json.simple.*;
import org.json.simple.parser.*;
import static org.junit.Assert.assertTrue;

import org.junit.*;

public class MinMaxParamBehaviorTest
{
    @Test
    public void shouldCreateBehavior()
    {
        var behavior = GivenBehavior("");
        assertTrue( behavior != null );
    }

    @Test
    public void shouldGetMinValue() throws ParseException
    {
        var behavior = GivenBehavior("[100,150]");
        var minValue = behavior.GetMinValue();
        assertTrue( minValue == 100 );
    }

    @Test
    public void shouldGetMaxValue() throws ParseException
    {
        var behavior = GivenBehavior("[100,150]");
        var maxValue = behavior.GetMaxValue();
        assertTrue( maxValue == 150 );
    }

    public MinMaxParamBehavior GivenBehavior(String json)
    {
        return new MinMaxParamBehavior("MinMaxTest", json);
    }

}
