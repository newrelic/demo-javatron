package com.newrelic.lib.Inventory;

public interface IInventoryRepository
{
    public Inventory FindOrNull(String id) throws Exception;
    public Inventory[] FindAll() throws Exception;
}
