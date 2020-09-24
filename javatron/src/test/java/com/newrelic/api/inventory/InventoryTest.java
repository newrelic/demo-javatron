package com.newrelic.api.inventory;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class InventoryTest 
{
    @Test
    public void shouldCreateInventory()
    {
        Inventory inventory = new Inventory();
        assertTrue( inventory != null );
    }
}
