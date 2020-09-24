package com.newrelic.lib;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import org.apache.http.HttpResponse;

public interface IHttpResponseAdapter
{
    Hashtable<String, String> GetHeaders();

    int GetStatusCode();
}
