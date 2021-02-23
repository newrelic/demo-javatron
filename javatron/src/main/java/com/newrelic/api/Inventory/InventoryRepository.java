package com.newrelic.api.Inventory;

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
