package com.newrelic.lib;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface ILogger
{
    void Debug(String message);
    void Info(String message);
    void Warning(String message);
    void Error(String message);
}
