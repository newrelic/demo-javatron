package com.newrelic.lib;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.lang.*;
import java.util.*;
import java.util.function.*;
import java.net.URI;
import java.net.MalformedURLException;

import org.apache.http.HttpResponse;

public class TronHandler
{
    public static final String HEADER_DEMOTRON_KEY_PREFIX = "X-DEMO";
    private static final String HEADER_DEMOTRON_TRACE_KEY = "X-DEMOTRON-TRACE";

    public TronHandler(IHttpUtil httpUtil, AppConfigDependency[] dependencies)
    {
        _httpUtil = httpUtil;
        _dependencies = dependencies;
        _trace = null;
    }

    public void InvokeDependencies(String path) throws Exception
    {
        for (var dependency : _dependencies)
        {
            InvokeDependency(dependency, path);
        }
    }

    public void InvokeDependency(AppConfigDependency dependency, String path) throws Exception
    {
        for (var url : dependency.getUrls())
        {
            InvokeUrl(url, path);
        }
    }

    public void InvokeUrl(String url, String path) throws Exception
    {
        var uri = URI.create(url);
        uri = uri.resolve(path);
        var headers = _httpUtil.GetRequestHeaders();
        var requestDemoHeaders = GetDemoHttpHeaders(headers);
        try
        {
            var queryUrl = uri.toURL().toString();
            Logger.GetOrCreate().Info("Invoking queryUrl:"+ queryUrl);
            var downstreamResponse = _httpUtil.QueryService(queryUrl, requestDemoHeaders);

            if(downstreamResponse.GetStatusCode() < 200 || downstreamResponse.GetStatusCode() > 299){
                throw new Exception();
            }

            if (IsTracingEnabled())
            {
                var responseHeaders = downstreamResponse.GetHeaders();
                var responseDemoHeaders = GetDemoHttpHeaders(responseHeaders);
                var trace = responseDemoHeaders.get(HEADER_DEMOTRON_TRACE_KEY);
                if (trace != null)
                {
                    appendTrace(trace);
                }
            }
        }
        catch(MalformedURLException exception)
        {
            exception.printStackTrace();
        }
    }

    public Response CreateResponse(Object entity)
    {
        var responseBuilder = Response.ok(entity);
        if (IsTracingEnabled())
        {
            var appId = GetAppId();
            var trace = GetTrace(appId);
            responseBuilder.header(HEADER_DEMOTRON_TRACE_KEY, trace);
        }
        return responseBuilder.build();
    }

    public boolean IsTracingEnabled()
    {
        var headers = GetDemoHttpHeaders(_httpUtil.GetRequestHeaders());
        if (headers.get(HEADER_DEMOTRON_TRACE_KEY) != null)
        {
            return true;
        }
        return false;
    }

    public String GetTrace(String origin)
    {
        if (IsTracingEnabled())
        {
            if (_trace == null)
            {
                return origin;
            }
            return origin +"," +_trace;
        }
        return null;
    }

    private void appendTrace(String element)
    {
        if (_trace == null)
        {
            _trace = element;
        }
        else
        {
            _trace = _trace +"," +element;
        }
    }

    private String GetAppId()
    {
        var repository = new AppConfigRepository();
        return repository.FindAppId();
    }

	public static Hashtable<String, String> GetDemoHttpHeaders(Hashtable<String, String> source)
    {
        var headers = new Hashtable<String, String>();
        source.forEach((k,v) -> AddDemoHeader(headers, k, v));
        return headers;
	}

    private static void AddDemoHeader(Hashtable<String, String> headers, String key, String value)
    {
        if (key.toUpperCase().startsWith(HEADER_DEMOTRON_KEY_PREFIX))
        {
            headers.put(key.toUpperCase(), value);
        }
    }

    private String _trace;
    private IHttpUtil _httpUtil;
    private AppConfigDependency[] _dependencies;
}
