package com.newrelic.lib.Inventory;

import com.newrelic.lib.IAppConfigRepository;

public class InventoryManagerFactory {
    public IInventoryManager createInventoryManager(IAppConfigRepository appConfig) {
        if (appConfig.FindMySQLConfiguration().isConfigured()) {
            return new LocalStorageManager();
        }
        else {
            return new LocalStorageManager();
        }
    }
}
