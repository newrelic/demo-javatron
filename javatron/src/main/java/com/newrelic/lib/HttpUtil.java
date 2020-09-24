package com.newrelic.lib;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import org.apache.http.client.fluent.Request;
import org.apache.http.HttpResponse;

public class HttpUtil implements IHttpUtil
{    
    public HttpUtil(Supplier<Hashtable<String, String>> getRequestHeadersFunc)
    {
        _getRequestHeadersFunc = getRequestHeadersFunc;
    }

    public Hashtable<String, String> GetRequestHeaders()
    {
        return _getRequestHeadersFunc.get();
    }

    public IHttpResponseAdapter QueryService(String url, Hashtable<String, String> headers)
    {
        try{
            var request = Request.Get(url);
            headers.forEach((k,v) -> request.addHeader(k, v));
            var result = request.execute();
            var response = result.returnResponse();
            return new HttpResponseAdapter(response);
        } catch (Exception e) {
            Logger.GetOrCreate().Error("Error while querying url "+url+ " detail:"+e.toString());
        }
        return null;
    }

    private Supplier<Hashtable<String, String>> _getRequestHeadersFunc;
}
