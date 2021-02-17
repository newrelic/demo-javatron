package com.newrelic.lib.Inventory;

import java.util.*;
import java.lang.Thread;
import java.lang.ClassLoader;
import java.util.function.Supplier;

import com.newrelic.lib.*;

public class LocalStorageManager implements IInventoryManager {
    private Supplier<Hashtable<String, Hashtable<String,String>>> _loader;

    public LocalStorageManager() {
        _loader = () -> GetDefaultList();
    }

    public LocalStorageManager(Supplier<Hashtable<String, Hashtable<String,String>>> loader) {
        _loader = loader;
    }

    public Inventory FindOrNull(String id) {
        var dictionary = _loader.get();
        if (id != null)
        {
            var item = dictionary.get(id);
            if (item!=null)
            {
                return createInventory(item);
            }
        }
        return null;
    }

    public Inventory[] Query() {
        return FindAll();
    }

    public Inventory Query(String id) {
        return FindOrNull(id);
    }

    public Inventory[] FindAll() {
        var dictionary = _loader.get();
        var list = new ArrayList<Inventory>();
        dictionary.values().forEach(item -> list.add(createInventory(item)));
        Inventory output[] = new Inventory[list.size()];
        output = list.toArray(output);
        return output;
    }

    private Inventory createInventory(Hashtable<String,String> attributes) {
        var inventory = new Inventory();
        inventory.setId(attributes.get("id"));
        inventory.setItem(attributes.get("item"));
        inventory.setPrice(attributes.get("price"));
        inventory.setSku(attributes.get("sku"));
        return inventory;
    }

    public Hashtable<String, Hashtable<String,String>> GetDefaultList() {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        var content = TomcatFileReader.GetContent(loader, "data/inventory.json");
        var reader = new JsonFileReader();
        var dictionary = reader.ReadJson(content);
        return dictionary;
    }
}
