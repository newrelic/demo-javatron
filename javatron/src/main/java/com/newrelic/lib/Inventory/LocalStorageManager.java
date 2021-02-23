package com.newrelic.lib.Inventory;

import com.newrelic.lib.Logger;

public class LocalStorageManager implements IInventoryManager
{

    private InventoryRepository repo;

    public LocalStorageManager()
    {
        this.repo = new InventoryRepository();
    }

    public LocalStorageManager(InventoryRepository repo)
    {
        this.repo = repo;
    }

    public Inventory[] Query()
    {
        return this.repo.FindAll();
    }

    public Inventory Query(String id)
    {
        return FindOrNull(id);
    }

    private Inventory FindOrNull(String id)
    {
        var inventory = this.repo.FindAll();
        if (id != null)
        {
            for (Inventory item : inventory)
            {
                if (id.equals(item.getId()))
                {
                    return item;
                }
            }
        }
        return null;
    }
}
