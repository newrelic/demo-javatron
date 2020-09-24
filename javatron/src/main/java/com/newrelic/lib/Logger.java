package com.newrelic.lib;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Handler;
import java.util.logging.ConsoleHandler;
import java.util.logging.SimpleFormatter;

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
        jlogger.setUseParentHandlers(false);
        var allHandlers = jlogger.getHandlers();
        for(var handler : allHandlers)
        {
            jlogger.removeHandler(handler);
        }
        Handler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter(){
            private static final String format = "%1$s %2$s %n";
            @Override
            public synchronized String format(java.util.logging.LogRecord lr) {
                return String.format(format,
                        lr.getLevel().getLocalizedName(),
                        lr.getMessage()
                );
            }
        });
        jlogger.addHandler(consoleHandler);
    }

    private static ILogger proxyInstance = null;
    private static final java.util.logging.Logger jlogger = java.util.logging.Logger.getGlobal();
}
