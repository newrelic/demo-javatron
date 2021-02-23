package com.newrelic.lib.Inventory;

public interface IInventoryManager
{
    public Inventory Query(String id) throws Exception;
    public Inventory[] Query() throws Exception;
}
