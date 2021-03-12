package com.newrelic.api.Behaviors;

import java.util.*;
import java.util.concurrent.*;
import java.lang.*;
import java.io.*;

import com.newrelic.lib.Logger;

public class MallocBehavior extends MinMaxParamBehavior 
{
    public static final String Name = "MALLOC";
    private static ConcurrentHashMap<UUID,UUID[]> Allocated = new ConcurrentHashMap<UUID,UUID[]>();

    public MallocBehavior(String value)
    {
        super(MallocBehavior.Name, value);
    }

    public void Execute() throws Exception
    {
        super.Execute();

        Integer min = null;
        Integer max = null;
        try
        {
            min = GetMinValue();
            max = GetMaxValue();
        }
        catch (Exception e)
        {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            var errorMessage = sw.toString();
            Logger.GetOrCreate().Warning("Could not get malloc parameters for behavior, input expected is an array of 2 integers, got:" +getValue() +" details:" +errorMessage);
            return;
        }

        if (min != null && max != null && min<=max)
        {
            var numberKb = Sample(min.intValue(), max.intValue());
            Logger.GetOrCreate().Info("Allocating " +numberKb +"KB");
            AllocateKB(numberKb);
        }
        else{
            Logger.GetOrCreate().Warning("Could not get valid malloc parameters for behavior, min:" +min +" max:" +max);
        }
    }

    private void AllocateKB(Integer numberKb)
    {
        var key = UUID.randomUUID();
        // 1KB is 64 guids (16 bytes)
        var size = (numberKb*64)-1;
        var data = new UUID[size];
        for (var index=0; index<size; index++)
        {
            data[index] = UUID.randomUUID();
        }
        Allocated.put(key, data);
    }

}
