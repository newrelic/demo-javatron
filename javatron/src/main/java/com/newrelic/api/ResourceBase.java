package com.newrelic.api;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.lang.*;
import java.lang.management.*;
import java.util.*;
import java.util.function.*;

import com.newrelic.lib.*;
import com.newrelic.api.Behaviors.*;

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
        var delayStartMs = GetApplicationContainer().GetAppConfigRepository().FindDelayStartMs();
        if (delayStartMs > 0)
        {
            var runtime = ManagementFactory.getRuntimeMXBean();
            var startTimeMs = runtime.getStartTime();
            var currentTimeMs = System.currentTimeMillis();
            if (currentTimeMs < startTimeMs+delayStartMs)
            {
                var message = "The application is not yet ready to accept traffic";
                Logger.GetOrCreate().Error(message);
                throw new Exception(message);
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
        var appConfigRepository = GetApplicationContainer().GetAppConfigRepository();
        var dependencies = appConfigRepository.FindDependencies();
        return new TronHandler(httpUtil, dependencies);
    }

    public ApplicationContainer GetApplicationContainer()
    {
        if (_applicationContainer == null)
        {
            _applicationContainer = ApplicationContainer.getInstance();
        }
        return _applicationContainer;
    }

    public BehaviorService GetBehaviorService()
    {
        if (_behaviorService == null)
        {
            var appConfig = GetApplicationContainer().GetAppConfigRepository();
            _behaviorService = new BehaviorService(new BehaviorsRepository(), GetHttpUtil(), appConfig);
        }
        return _behaviorService;
    }

    private BehaviorService _behaviorService;
    private IHttpUtil _httpUtil;
    private ApplicationContainer _applicationContainer;
}
