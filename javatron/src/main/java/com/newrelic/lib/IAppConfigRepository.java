package com.newrelic.lib;

import java.lang.*;
import java.util.*;
import java.util.function.*;

public interface IAppConfigRepository
{
    public String FindAppId();
    public int FindPortOrDefault(int defaultPort);
    public int FindDelayStartMs();
    public AppConfigDependency[] FindDependencies();
}
