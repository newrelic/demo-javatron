package com.newrelic.lib;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import org.apache.http.HttpResponse;

public class HttpResponseAdapter implements IHttpResponseAdapter
{
    public HttpResponseAdapter(HttpResponse response)
    {
        _response = response;
    }

    public Hashtable<String, String> GetHeaders()
    {
        var responseHeaders = new Hashtable<String,String>();
        for (var header : _response.getAllHeaders())
        {
            var key = header.getName();
            var value = header.getValue();
            responseHeaders.put(key, value);
        }
        return responseHeaders;
    }

    public int GetStatusCode(){
        return _response.getStatusLine().getStatusCode();
    }

    private HttpResponse _response;
}
