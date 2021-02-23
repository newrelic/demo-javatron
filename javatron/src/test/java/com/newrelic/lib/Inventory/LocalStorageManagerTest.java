package com.newrelic.lib.Inventory;

import java.util.*;
import java.util.function.Supplier;

import static org.junit.Assert.*;
import org.junit.*;

// import jdk.vm.ci.meta.Local;

public class LocalStorageManagerTest
{
    @Test
    public void shouldCreateManager()
    {
        GivenManager();
        assertTrue( Manager != null );
    }

    @Test
    public void ShouldFindExistingItem()
    {
        GivenItem("123", "Item 1", "$99", "SKUX");
        GivenManager();
        var item = Manager.Query("123");
        assertTrue( item != null );
        assertEquals(item.getId(), "123");
        assertEquals(item.getItem(), "Item 1");
        assertEquals(item.getPrice(), "$99");
        assertEquals(item.getSku(), "SKUX");
    }

    @Test
    public void ShouldFindOneOfManyItems()
    {
        GivenItem("123", "Item 1", "$11", "SKUX");
        GivenItem("456", "Item 2", "$22", "SKUY");
        GivenItem("789", "Item 3", "$33", "SKUZ");
        GivenManager();
        var item = Manager.Query("456");
        assertTrue( item != null );
    }

    @Test
    public void ShouldFindItemNoAttributes()
    {
        GivenItemEmpty("123");
        GivenManager();
        var item = Manager.Query("123");
        assertTrue( item != null );
    }

    @Test
    public void ShouldNotFindMissingItem()
    {
        GivenManager();
        var item = Manager.Query("123");
        assertTrue( item == null );
    }

    @Test
    public void ShouldNotFindItemWithoutIdentity()
    {
        GivenManager();
        GivenItemWithoutIdentity("Item 1", "$99", "SKUX");
        var item = Manager.Query("123");
        assertTrue( item == null );
    }

    @Test
    public void ShouldNotFindItemWhenNoIdentityProvided()
    {
        GivenManager();
        GivenItem("123", "Item 1", "$99", "SKUX");
        var item = Manager.Query(null);
        assertTrue( item == null );
    }

    @Before
    public void Init()
    {
        _items = new Hashtable<String, Hashtable<String,String>>();
        Repository = null;
    }

    public LocalStorageManager GivenManager()
    {
        if (Manager == null)
        {
            Manager = new LocalStorageManager(new InventoryRepository(GetLoader()));
        }
        return Manager;
    }

    public void GivenItemEmpty(String id)
    {
        var attributes = new Hashtable<String,String>();
        attributes.put("id", String.valueOf(id));
        _items.put(String.valueOf(id), attributes);
    }

    public void GivenItemWithoutIdentity(String item, String price, String sku)
    {
        var attributes = new Hashtable<String,String>();
        attributes.put("item", item);
        attributes.put("price", price);
        attributes.put("sku", sku);
        _items.put("", attributes);
    }

    public void GivenItem(String id, String item, String price, String sku)
    {
        var attributes = new Hashtable<String,String>();
        attributes.put("id", id);
        attributes.put("item", item);
        attributes.put("price", price);
        attributes.put("sku", sku);
        _items.put(String.valueOf(id), attributes);
    }

    public Supplier<Hashtable<String, Hashtable<String,String>>> GetLoader()
    {
        return () -> _items;
    }

    private Hashtable<String, Hashtable<String,String>> _items;
    private InventoryRepository Repository;
    private LocalStorageManager Manager;
}
