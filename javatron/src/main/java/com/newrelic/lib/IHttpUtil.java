package com.newrelic.lib;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import org.apache.http.HttpResponse;

public interface IHttpUtil
{
    IHttpResponseAdapter QueryService(String url, Hashtable<String, String> headers);
    Hashtable<String, String> GetRequestHeaders();
}
