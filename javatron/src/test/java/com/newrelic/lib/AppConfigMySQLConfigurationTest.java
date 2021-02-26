package com.newrelic.lib;

import java.util.function.Supplier;

import static org.junit.Assert.*;
import org.junit.*;

import org.json.simple.JSONObject;

public class AppConfigMySQLConfigurationTest
{
    @Test
    public void shouldCreateConfiguration()
    {
        GivenRepository();
        var config = Repository.FindMySQLConfiguration();
        assertTrue( config != null );
    }

    @Test
    public void shouldNotBeConfigured()
    {
        GivenRepository();
        var config = Repository.FindMySQLConfiguration();
        assertTrue( config.isConfigured() == false );
    }

    @Test
    public void shouldBeConfigured()
    {
        GivenMySQLConfiguration("fakehost", "4000", "testuser", "testpassword");
        GivenRepository();
        var config = Repository.FindMySQLConfiguration();
        assertTrue( config.isConfigured() == true);
    }

    @Test
    public void ShouldNotBeConfiguredWithIncompleteCredentials()
    {
        GivenIncompleteMySQLConfiguration("fakehost");
        GivenRepository();
        var config = Repository.FindMySQLConfiguration();
        assertTrue( config.isConfigured() == false );
    }

    @Test
    public void shouldNotBeConfiguredWithEmptyStrings()
    {
        GivenMySQLConfiguration("", "", "", "");
        GivenRepository();
        var config = Repository.FindMySQLConfiguration();
        assertTrue( config.isConfigured() == false );
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

    public void GivenMySQLConfiguration(String host, String port, String user, String password)
    {
        var config = new JSONObject();
        config.put("host", host);
        config.put("port", port);
        config.put("user", user);
        config.put("password", password);

        _json.put("database", config);
    }

    public void GivenIncompleteMySQLConfiguration(String host)
    {
        var config = new JSONObject();
        config.put("host", host);

        _json.put("database", config);
    }

    private JSONObject _json;
    private AppConfigRepository Repository;
}
