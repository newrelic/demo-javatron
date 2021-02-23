package com.newrelic.lib.Inventory;

import java.util.*;
import java.lang.Thread;
import java.lang.ClassLoader;
import java.util.function.Supplier;

import com.newrelic.lib.*;

public class InventoryRepository
{
    private Supplier<Hashtable<String, Hashtable<String,String>>> _loader;

    public InventoryRepository()
    {
        _loader = () -> GetDefaultList();
    }

    public InventoryRepository(Supplier<Hashtable<String, Hashtable<String,String>>> loader)
    {
        _loader = loader;
    }

    public Inventory[] FindAll()
    {
        var dictionary = _loader.get();
        var list = new ArrayList<Inventory>();
        dictionary.values().forEach(item -> list.add(Inventory.create(item)));
        Inventory output[] = new Inventory[list.size()];
        output = list.toArray(output);
        return output;
    }

    public Hashtable<String, Hashtable<String,String>> GetDefaultList()
    {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        var content = TomcatFileReader.GetContent(loader, "data/inventory.json");
        var reader = new JsonFileReader();
        var dictionary = reader.ReadJson(content);
        return dictionary;
    }
}
