package com.newrelic.api.Inventory;

import java.util.*;
import java.lang.Thread;
import java.lang.ClassLoader;
import java.util.function.Supplier;

import com.newrelic.lib.TomcatFileReader;
import com.newrelic.lib.JsonFileReader;
import com.newrelic.lib.AppConfigRepository;
import com.newrelic.lib.Inventory.InventoryManagerFactory;
import com.newrelic.lib.Inventory.IInventoryManager;
import com.newrelic.lib.Inventory.Inventory;

public class InventoryRepository
{
    private IInventoryManager inventoryManager;

    public InventoryRepository(IInventoryManager manager)
    {
        inventoryManager = manager;
    }

    public Inventory FindOrNull(String id) throws Exception
    {
        return inventoryManager.Query(id);
    }

    public Inventory[] FindAll() throws Exception
    {
        return inventoryManager.Query();
    }
}
