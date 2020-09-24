package com.newrelic.api.behaviors;

import java.util.*;
import java.lang.*;
import java.io.*;
import java.util.stream.*;
import java.util.function.*;
import org.json.simple.parser.*;

import com.newrelic.lib.Logger;

public class ComputeBehavior extends MinMaxParamBehavior
{
    public static final String Name = "COMPUTE";

    public ComputeBehavior(String value)
    {
        super(ComputeBehavior.Name, value);
        this._computeFunc = (delayMs, concurrency) -> compute(delayMs.intValue(), concurrency.intValue());
    }

    public ComputeBehavior(String value, BiConsumer<Integer,Integer> computeFunc)
    {
        this(value);
        this._computeFunc = computeFunc;
    }

    public void Execute()
    {
        super.Execute();

        Integer min = null;
        Integer max = null;
        int concurrency = 10;
        try
        {
            min = GetMinValue();
            max = GetMaxValue();
            concurrency = GetConcurrencyValueOrDefault(concurrency);
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            var errorMessage = sw.toString();
            Logger.GetOrCreate().Warning("Could not get compute parameters for behavior, input expected is an array of 2 integers, got:" +getValue() +" details:" +errorMessage);
            return;
        }

        if (min != null && max != null && min<=max)
        {
            var delayMs = Sample(min.intValue(), max.intValue());
            this._computeFunc.accept(new Integer(delayMs), new Integer(concurrency));
        }
        else{
            Logger.GetOrCreate().Warning("Could not get valid compute parameters for behavior, min:" +min +" max:" +max);
        }
    }

    public int GetConcurrencyValueOrDefault(int defaultValue) throws ParseException
    {
        var json = getValue();
        var list = GetJsonAsArray(json);
        if (list.size() >= 3)
        {
            return list.get(2).intValue();
        }
        return defaultValue;
    }

    private void compute(int delayMs, int concurrency)
    {
        var startTimeMS = System.currentTimeMillis();
        while (startTimeMS+delayMs > System.currentTimeMillis())
        {
            var stream = IntStream.rangeClosed(1, concurrency);
            stream.parallel().forEach(element -> computeSingle(element));
        }
    }

    private double computeSingle(int seed)
    {
        var list = new ArrayList<Double>();
        for (int index=1; index<=seed; index++)
        {
            var value = System.currentTimeMillis() / Math.pow(Math.PI, index);
            Double result = Math.log10(value * System.currentTimeMillis());
            list.add(result);
        }
        return average(list);
    }

    private double average(List<Double> x) {
        double sum = 0;
        for (double aX : x) sum += aX;
        return (sum / x.size());
    }

    private BiConsumer<Integer,Integer> _computeFunc;
}
