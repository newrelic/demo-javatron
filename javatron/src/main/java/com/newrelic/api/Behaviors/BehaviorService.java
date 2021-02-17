package com.newrelic.api.Behaviors;

import java.util.*;
import java.io.*;

import com.newrelic.lib.IHttpUtil;
import com.newrelic.lib.TronHandler;
import com.newrelic.lib.IAppConfigRepository;
import com.newrelic.lib.Logger;

public class BehaviorService
{
    public BehaviorService(IBehaviorRepository repository, IHttpUtil httpUtil, IAppConfigRepository appConfigRepository)
    {
        _repository = repository;
        _httpUtil = httpUtil;
        _appConfigRepository = appConfigRepository;
    }

    public void HandlePreFunc() 
    {
        handleFunc("PRE");
    }

    public void HandlePostFunc() 
    {
        handleFunc("POST");
    }

    private void handleFunc(String stepName)
    {
        var behaviors = new Hashtable<String, Behavior>();
        var headers = _httpUtil.GetRequestHeaders();
        var requestDemoHeaders = TronHandler.GetDemoHttpHeaders(headers);
        var appId = _appConfigRepository.FindAppId();
        for (var behaviorName : _repository.FindAllNames())
        {
            var formatSpecific = TronHandler.HEADER_DEMOTRON_KEY_PREFIX +"-" +behaviorName +"-" +stepName +"-" +appId;
            requestDemoHeaders.forEach((key, value) -> addIfNotExistMatch(behaviors, behaviorName, formatSpecific, key, value) );
            
            var formatAll = TronHandler.HEADER_DEMOTRON_KEY_PREFIX +"-" +behaviorName +"-" +stepName;
            requestDemoHeaders.forEach((key, value) -> addIfNotExistMatch(behaviors, behaviorName, formatAll, key, value) );
        }
        execute(behaviors.values());
    }

    private void execute(Collection<Behavior> behaviors)
    {
        for (var behavior : behaviors)
        {
            try 
            {
                behavior.Execute();
            } 
            catch (Exception e)
            {
                StringWriter sw = new StringWriter();
                e.printStackTrace(new PrintWriter(sw));
                var message = sw.toString();
                Logger.GetOrCreate().Error(message);
                throw e;
            }
        }
    }

    private void addIfNotExistMatch(Hashtable<String, Behavior> behaviors, String behaviorName, String format, String key, String value)
    {
        if (behaviors.containsKey(behaviorName))
        {
            return;
        }
        if (key.equalsIgnoreCase(format))
        {
            var behavior = _repository.Create(behaviorName, value);
            behaviors.put(behaviorName, behavior);
        }
    }

    private IBehaviorRepository _repository;
    private IHttpUtil _httpUtil;
    private IAppConfigRepository _appConfigRepository;
}
