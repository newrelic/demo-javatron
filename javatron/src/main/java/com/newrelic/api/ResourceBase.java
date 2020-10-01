package com.newrelic.api;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.lang.*;
import java.lang.management.*;
import java.util.*;
import java.util.function.*;

import com.newrelic.lib.*;

import com.newrelic.api.behaviors.*;

import com.newrelic.lib.Logger;

public class ResourceBase
{
    @Context HttpHeaders httpHeaders;

    public ResourceBase()
    {
        _httpUtil = null;
    }

    public void EnsureAppIsStarted() throws Exception
    {
        var delayStartMs = GetAppConfigRepository().FindDelayStartMs();
        if (delayStartMs > 0)
        {
            var runtime = ManagementFactory.getRuntimeMXBean();
            var startTimeMs = runtime.getStartTime();
            var currentTimeMs = System.currentTimeMillis();
            if (currentTimeMs < startTimeMs+delayStartMs)
            {
                throw new Exception("The application is not yet ready to accept traffic");
            }
        }
    }

    public Hashtable<String,String> GetHttpHeaders()
    {
        var headers = new Hashtable<String,String>();
        var keyValues = this.httpHeaders.getRequestHeaders();
        keyValues.forEach((k,v) -> headers.put(k, v.get(0)));
        return headers;
    }

    public IHttpUtil GetHttpUtil()
    {
        if (_httpUtil == null)
        {
            _httpUtil = new HttpUtil(() -> GetHttpHeaders());
        }
        return _httpUtil;
    }

    public TronHandler CreateTronHandler()
    {
        var httpUtil = GetHttpUtil();
        var appConfigRepository = GetAppConfigRepository();
        var dependencies = appConfigRepository.FindDependencies();
        return new TronHandler(httpUtil, dependencies);
    }

    public IAppConfigRepository GetAppConfigRepository()
    {
        if (_appConfigRepository == null)
        {
            _appConfigRepository = new AppConfigRepository();
        }
        return _appConfigRepository;
    }

    public BehaviorService GetBehaviorService()
    {
        if (_behaviorService == null)
        {
            _behaviorService = new BehaviorService(new BehaviorsRepository(), GetHttpUtil(), GetAppConfigRepository());
        }
        return _behaviorService;
    }

    private BehaviorService _behaviorService;
    private IHttpUtil _httpUtil;
    private IAppConfigRepository _appConfigRepository;
}
