package com.newrelic.lib.Inventory;

import java.util.function.Supplier;

import static org.junit.Assert.*;
import org.junit.*;

import org.json.simple.JSONObject;
import com.newrelic.lib.AppConfigRepository;

public class InventoryRepositoryFactoryTest
{
    @Test
    public void shouldCreateLocalStorageRepository()
    {
        GivenRepository();
        var manager = InventoryRepositoryFactory.createInventoryRepository(Repository);
        assertTrue( manager instanceof LocalStorageRepository );
    }

    @Test
    public void shouldCreateMySQLRepository()
    {
       GivenMySQLConfiguration("fakehost", "3000", "testuser", "testpassword");
       GivenRepository();
       var manager = InventoryRepositoryFactory.createInventoryRepository(Repository);
       assertTrue( manager instanceof MySQLRepository );
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

    private JSONObject _json;
    private AppConfigRepository Repository;
}
