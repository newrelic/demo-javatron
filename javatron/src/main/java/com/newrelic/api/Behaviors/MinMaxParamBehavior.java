package com.newrelic.api.behaviors;

import java.util.*;
import java.lang.*;
import java.io.*;

import org.json.simple.*;
import org.json.simple.parser.*;

public class MinMaxParamBehavior extends Behavior
{
    public MinMaxParamBehavior(String name, String value)
    {
        this.setName(name);
        this.setValue(value);
    }

    public int Sample(int min, int max)
    {
        // From https://en.wikipedia.org/wiki/Box%E2%80%93Muller_transform
        var u1 = Math.random();
        var u2 = Math.random();
        var z0 = Math.sqrt(-2*Math.log(u1)) * Math.cos(Math.PI * 2 * u2);
        var z1 = z0/10 +0.5;
        return min + (int)(z1 * (max+1 - min));
    }

    public Integer GetMinValue() throws ParseException
    {
        var json = getValue();
        var list = GetJsonAsArray(json);
        if (list.size() >= 1)
        {
            return list.get(0);
        }
        return null;
    }

    public Integer GetMaxValue() throws ParseException
    {
        var json = getValue();
        var list = GetJsonAsArray(json);
        if (list.size() >= 2)
        {
            return list.get(1);
        }
        return null;
    }

    public List<Integer> GetJsonAsArray(String json) throws ParseException
    {
        var values = new ArrayList<Integer>();
        var jsonParser = new JSONParser();
        var obj = jsonParser.parse(json);
        if (obj != null)
        {
            var list = (JSONArray) obj;
            for (int index=0; index<list.size(); index++)
            {
                var value = list.get(index).toString();
                values.add(new Integer(value));
            }
        }
        return values;
    }
}
