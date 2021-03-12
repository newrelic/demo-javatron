package com.newrelic.lib;

import javax.ws.rs.*;
import javax.ws.rs.core.*;

import java.lang.*;
import java.lang.management.*;
import java.util.*;
import java.util.function.*;

import com.newrelic.lib.*;
import com.newrelic.api.Behaviors.*;

import com.newrelic.lib.Logger;
import com.newrelic.lib.Inventory.IInventoryRepository;
import com.newrelic.lib.Inventory.InventoryRepositoryFactory;

public class ApplicationContainer
{
    private static ApplicationContainer instance;

    public static ApplicationContainer getInstance()
    {
       if (ApplicationContainer.instance == null)
       {
           ApplicationContainer.instance = new ApplicationContainer();
       }

       return ApplicationContainer.instance;
    }

    private ApplicationContainer()
    {
    }

    public IAppConfigRepository GetAppConfigRepository()
    {
        if (_appConfigRepository == null)
        {
            _appConfigRepository = new AppConfigRepository();
        }
        return _appConfigRepository;
    }

    public IInventoryRepository GetInventoryRepository()
    {
        if (_inventoryRepository == null)
        {
            _inventoryRepository = InventoryRepositoryFactory.createInventoryRepository(GetAppConfigRepository());
        }
        return _inventoryRepository;
    }

    private IAppConfigRepository _appConfigRepository;
    private IInventoryRepository _inventoryRepository;
}
