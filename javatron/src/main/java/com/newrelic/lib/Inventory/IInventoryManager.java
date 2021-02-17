package com.newrelic.lib.Inventory;

public interface IInventoryManager
{
    public Inventory Query(String id);
    public Inventory[] Query();
}
