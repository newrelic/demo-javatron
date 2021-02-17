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
    // private Supplier<Hashtable<String, Hashtable<String,String>>> _loader;

    // public InventoryRepository()
    // {
    //     _loader = () -> GetDefaultList();
    // }

    // public InventoryRepository(Supplier<Hashtable<String, Hashtable<String,String>>> loader)
    // {
    //     _loader = loader;
    // }

    private IInventoryManager inventoryManager;

    public InventoryRepository(IInventoryManager manager) {
        inventoryManager = manager;
    }

    public Inventory FindOrNull(String id) {
        return inventoryManager.Query(id);
    }

    public Inventory[] FindAll() {
        return inventoryManager.Query();
    }

    // private Inventory createInventory(Hashtable<String,String> attributes)
    // {
    //     var inventory = new Inventory();
    //     inventory.setId(attributes.get("id"));
    //     inventory.setItem(attributes.get("item"));
    //     inventory.setPrice(attributes.get("price"));
    //     inventory.setSku(attributes.get("sku"));
    //     return inventory;
    // }

    // public Hashtable<String, Hashtable<String,String>> GetDefaultList()
    // {
    //     ClassLoader loader = Thread.currentThread().getContextClassLoader();
    //     var content = TomcatFileReader.GetContent(loader, "data/inventory.json");
    //     var reader = new JsonFileReader();
    //     var dictionary = reader.ReadJson(content);
    //     return dictionary;
    // }
}
