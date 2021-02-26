package com.newrelic.lib.Inventory;

public class LocalStorageRepository implements IInventoryRepository
{

    private InventoryLoader inventoryLoader;

    public LocalStorageRepository()
    {
        this.inventoryLoader = new InventoryLoader();
    }

    public LocalStorageRepository(InventoryLoader loader)
    {
        this.inventoryLoader = loader;
    }

    public Inventory[] FindAll()
    {
        return this.inventoryLoader.LoadAll();
    }

    public Inventory FindOrNull(String id)
    {
        var inventory = this.inventoryLoader.LoadAll();
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
