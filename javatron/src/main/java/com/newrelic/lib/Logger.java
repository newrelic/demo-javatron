package com.newrelic.lib;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import java.io.IOException;
import java.util.logging.*;

public class Logger implements ILogger
{
    private Logger()
    {
    }

    public void Debug(String message)
    {
        jlogger.fine(message);
    }

    public void Info(String message)
    {
        jlogger.info(message);
    }
    
    public void Warning(String message)
    {
        jlogger.warning(message);
    }

    public void Error(String message)
    {
        jlogger.severe(message);
    }

    public static ILogger GetOrCreate()
    {
        if (proxyInstance == null)
        {
            var instance = new Logger();
            instance.Configure();
            proxyInstance = instance;
        }
        return proxyInstance;
    }

    private void Configure()
    {
        try
        {
            LogManager.getLogManager().readConfiguration();
            LogManager.getLogManager().addLogger(jlogger);
        }
        catch (IOException ioException)
        {
            System.out.println(ioException.getMessage());
        }
    }

    private static ILogger proxyInstance = null;
    private static final java.util.logging.Logger jlogger = java.util.logging.Logger.getGlobal();
}
