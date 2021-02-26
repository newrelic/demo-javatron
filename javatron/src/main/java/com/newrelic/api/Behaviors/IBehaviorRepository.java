package com.newrelic.api.Behaviors;

import java.util.*;
import java.util.function.Supplier;

public interface IBehaviorRepository
{
    List<String> FindAllNames();
    Behavior Create(String name);
    Behavior Create(String name, String value);
}
