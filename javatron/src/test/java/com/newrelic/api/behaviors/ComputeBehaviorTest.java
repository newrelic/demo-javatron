package com.newrelic.api.Behaviors;

import org.json.simple.*;
import org.json.simple.parser.*;
import java.util.function.*;

import static org.junit.Assert.assertTrue;

import org.junit.*;

public class ComputeBehaviorTest
{
    @Test
    public void shouldCreateBehavior()
    {
        var behavior = GivenBehavior("");
        assertTrue( behavior != null );
    }

    @Test
    public void shouldGetConcurrencyValue() throws ParseException
    {
        var behavior = GivenBehavior("[100,150,200]");
        var concurrency = behavior.GetConcurrencyValueOrDefault(10);
        assertTrue( concurrency == 200 );
    }

    @Test
    public void shouldGetDefaultConcurrencyValue() throws ParseException
    {
        var behavior = GivenBehavior("[100,150]");
        var concurrency = behavior.GetConcurrencyValueOrDefault(10);
        assertTrue( concurrency == 10 );
    }

    @Test
    public void shouldExecute() throws Exception
    {
        var behavior = GivenBehavior("[100,150]");
        behavior.Execute();
        assertTrue( (LastDelayMs>=100 && LastDelayMs<=150) );
    }

    @Test
    public void shouldExecuteHighConcurrency() throws Exception
    {
        var behavior = GivenBehavior("[100,150,200]");
        behavior.Execute();
        assertTrue( (LastDelayMs>=100 && LastDelayMs<=150) );
        assertTrue( LastConcurrency == 200 );
    }

    @Test
    public void shouldNotExecuteInvalidRange() throws Exception
    {
        var behavior = GivenBehavior("[101,100]");
        behavior.Execute();
        assertTrue( LastDelayMs == null );
    }

    @Test
    public void shouldNotExecuteMissingRange() throws Exception
    {
        var behavior = GivenBehavior("[100]");
        behavior.Execute();
        assertTrue( LastDelayMs == null );
    }

    @Test
    public void shouldNotExecuteMissingEntireRange() throws Exception
    {
        var behavior = GivenBehavior("[]");
        behavior.Execute();
        assertTrue( LastDelayMs == null );
    }

    @Test
    public void shouldNotExecuteMissingValue() throws Exception
    {
        var behavior = GivenBehavior("");
        behavior.Execute();
        assertTrue( LastDelayMs == null );
    }

    @Before
    public void Init()
    {
        LastDelayMs = null;
        LastConcurrency = null;
    }

    public ComputeBehavior GivenBehavior(String json)
    {
        BiConsumer<Integer,Integer> computeFunc = (delayMs, concurrency) -> StubCompute(delayMs, concurrency);
        return new ComputeBehavior(json, computeFunc);
    }

    public void StubCompute(Integer delayMs, Integer concurrency)
    {
        LastDelayMs = delayMs;
        LastConcurrency = concurrency;
    }

    private Integer LastDelayMs;
    private Integer LastConcurrency;
}
