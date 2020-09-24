package com.newrelic.lib;

import java.util.*;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import org.junit.*;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class AppConfigRepositoryTest 
{
    @Test
    public void shouldCreateRepository()
    {
        GivenRepository();
        assertTrue( Repository != null );
    }

    @Test
    public void shouldFindNullAppIdWhenEmpty()
    {
        GivenRepository();
        var appId = Repository.FindAppId();
        assertTrue( appId == null );
    }

    @Test
    public void shouldFindAppId()
    {
        GivenRepository();
        GivenAppId("app1");
        var appId = Repository.FindAppId();
        assertEquals(appId, "app1" );
    }

    @Test
    public void shouldFindDefaultPortWhenEmpty()
    {
        GivenRepository();
        var port = Repository.FindPortOrDefault(8081);
        assertEquals(port, 8081 );
    }


    @Test
    public void shouldFindPort()
    {
        GivenRepository();
        GivenPort(5009);
        var port = Repository.FindPortOrDefault(8081);
        assertEquals(port, 5009);
    }

    @Test
    public void shouldFindNoDependency()
    {
        GivenRepository();
        GivenAppId("app1");
        GivenPort(5009);
        var dependencies = Repository.FindDependencies();
        assertEquals(dependencies.length, 0);
    }

    @Test
    public void shouldFindDependencyWithoutUrls()
    {
        GivenRepository();
        GivenDependencyNoUrls("app2");
        var dependencies = Repository.FindDependencies();
        assertEquals(dependencies.length, 1);
        assertEquals(dependencies[0].getId(), "app2");
        assertEquals(dependencies[0].getUrls().length, 0);
    }
    
    @Test
    public void shouldFindEmptyDependency()
    {
        GivenRepository();
        GivenDependencyEmpty();
        var dependencies = Repository.FindDependencies();
        assertEquals(dependencies.length, 1);
        assertEquals(dependencies[0].getId(), null);
        assertEquals(dependencies[0].getUrls().length, 0);
    }

    @Test
    public void shouldFindSingleDependency()
    {
        GivenRepository();
        GivenDependency("app2", "http://localhost:12345");
        var dependencies = Repository.FindDependencies();
        assertEquals(dependencies.length, 1);
        assertEquals(dependencies[0].getId(), "app2");
        assertEquals(dependencies[0].getUrls().length, 1);
        assertEquals(dependencies[0].getUrls()[0], "http://localhost:12345");
    }

    @Test
    public void shouldFindMultipleDependencies()
    {
        GivenRepository();
        GivenDependency("app2", "http://localhost:12345");
        GivenDependency("app3", "http://anotherhost:5678");
        var dependencies = Repository.FindDependencies();
        assertEquals(dependencies.length, 2);
        assertEquals(dependencies[0].getId(), "app2");
        assertEquals(dependencies[0].getUrls().length, 1);
        assertEquals(dependencies[0].getUrls()[0], "http://localhost:12345");
        assertEquals(dependencies[1].getId(), "app3");
        assertEquals(dependencies[1].getUrls().length, 1);
        assertEquals(dependencies[1].getUrls()[0], "http://anotherhost:5678");
    }

    @Test
    public void shouldFindSingleDependencyMultipleUrls()
    {
        GivenRepository();
        GivenDependencyMultipleUrls("app2", new String[]{"http://localhost:12345", "http://localhost:56789"});
        var dependencies = Repository.FindDependencies();
        assertEquals(dependencies.length, 1);
        assertEquals(dependencies[0].getId(), "app2");
        assertEquals(dependencies[0].getUrls().length, 2);
        assertEquals(dependencies[0].getUrls()[0], "http://localhost:12345");
        assertEquals(dependencies[0].getUrls()[1], "http://localhost:56789");
    }

    @Before
    public void Init()
    {
        _json = new JSONObject();
        Repository = null;
    }

    public AppConfigRepository GivenRepository()
    {
        if (Repository == null)
        {
            Repository = new AppConfigRepository(GetLoader());
        }
        return Repository;
    }

    public Supplier<String> GetLoader()
    {
        return () -> _json.toJSONString();
    }

    public void GivenAppId(String appId)
    {
        _json.put("id", appId);
    }
    
    public void GivenPort(int port)
    {
        _json.put("port", port);
    }

    public void GivenDependency(String appId, String url)
    {
        var urls = new String[1];
        urls[0] = url;
        GivenDependencyMultipleUrls(appId, urls);
    }

    public void GivenDependencyNoUrls(String appId)
    {
        GivenDependencyMultipleUrls(appId, null);
    }

    public void GivenDependencyEmpty()
    {
        GivenDependencyMultipleUrls(null, null);
    }

    public void GivenDependencyMultipleUrls(String appId, String[] urls)
    {
        if (_json.get("dependencies") == null)
        {
            _json.put("dependencies", new JSONArray());
        }
        var dependencies = (JSONArray) _json.get("dependencies");
        var dependency = new JSONObject();
        if (appId != null)
        {
            dependency.put("id", appId);
        }
        if (urls!=null)
        {
            var urlJsonArray = new JSONArray();
            for (String url: urls)
            {
                urlJsonArray.add(url);
            }
            dependency.put("urls", urlJsonArray);
        }
        dependencies.add(dependency);
    }    

    private JSONObject _json;
    private AppConfigRepository Repository;
}
