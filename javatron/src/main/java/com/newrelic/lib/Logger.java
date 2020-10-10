package com.newrelic.lib;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import java.io.IOException;
import org.apache.logging.log4j.*;

public class Logger implements ILogger
{
    private Logger()
    {
    }

    public void Debug(String message)
    {
        logger.debug(message);
    }

    public void Info(String message)
    {
        logger.info(message);
    }
    
    public void Warning(String message)
    {
        logger.warn(message);
    }

    public void Error(String message)
    {
        logger.error(message);
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
        // Nothing specific for log4j
    }

    private static ILogger proxyInstance = null;
    private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(Logger.class);
}
