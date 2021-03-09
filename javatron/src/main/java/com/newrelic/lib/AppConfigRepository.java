package com.newrelic.lib;

import java.util.*;
import java.lang.*;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class AppConfigRepository implements IAppConfigRepository
{
    public AppConfigRepository()
    {
        _loader = () -> LoadDefaultConfig();
    }
    
    public AppConfigRepository(Supplier<String> loader)
    {
        _loader = loader;
    }

	public static String LoadDefaultConfig()
    {        
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        var content = TomcatFileReader.GetContent(loader, "config/app_config.json");
        return content;
	}
    
    public String FindAppId()
    {
        var obj = GetRootJsonObject();
        String id = (String) obj.get("id");
        return id;
    }
    
    public int FindPortOrDefault(int defaultPort)
    {
        var obj = GetRootJsonObject();
        var value = obj.get("port");
        if (value != null)
        {
            return ((Number)value).intValue();
        }
        return defaultPort;
    }

    public int FindDelayStartMs()
    {
        var obj = GetRootJsonObject();
        var value = obj.get("delayStartMs");
        if (value != null)
        {
            return ((Number)value).intValue();
        }
        return 0;
    }

    public AppConfigMySQLConfiguration FindMySQLConfiguration()
    {
        var dbConfig = new AppConfigMySQLConfiguration();

        var obj = GetRootJsonObject();
        var dbObj = (JSONObject) obj.get("database");
        if (dbObj != null)
        {
            var host = (String) dbObj.get("host");
            var port = (String) dbObj.get("port");
            var user = (String) dbObj.get("user");
            var password = (String) dbObj.get("password");
            var name = (String) dbObj.get("name");

            dbConfig.setHost(host);
            dbConfig.setPort(port);
            dbConfig.setUser(user);
            dbConfig.setPassword(password);
            dbConfig.setName(name);
        }

        return dbConfig;
    }

    public AppConfigDependency[] FindDependencies()
    {
        var obj = GetRootJsonObject();
        var dependencies = (JSONArray) obj.get("dependencies");
        var list = new ArrayList<AppConfigDependency>();
        if (dependencies != null)
        {
            dependencies.forEach(dependency -> list.add(createDependency((JSONObject) dependency)));
        }
        AppConfigDependency[] output = new AppConfigDependency[list.size()];
        output = list.toArray(output);
        return output;
    }

    private AppConfigDependency createDependency(JSONObject obj)
    {
        var dependency = new AppConfigDependency();
        var id = (String) obj.get("id");
        dependency.setId(id);
        var urls = (JSONArray) obj.get("urls");
        var list = new ArrayList<String>();
        if (urls != null)
        {
            urls.forEach(url -> list.add((String)url));
        }
        String[] urlArray = new String[list.size()];
        urlArray = list.toArray(urlArray);
        dependency.setUrls(urlArray);
        return dependency;
    }

    private JSONObject GetRootJsonObject()
    {
        var json = _loader.get();
        var reader = new JsonFileReader();
        var obj = (JSONObject) reader.ParseJson(json);
        return obj;
    }

    private Supplier<String> _loader;
}
