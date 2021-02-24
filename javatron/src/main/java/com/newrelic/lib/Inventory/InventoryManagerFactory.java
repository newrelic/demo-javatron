package com.newrelic.lib.Inventory;

import com.newrelic.lib.IAppConfigRepository;
import com.newrelic.lib.Logger;

public class InventoryManagerFactory
{
    public static IInventoryManager createInventoryManager(IAppConfigRepository appConfig)
    {
        if (appConfig.FindMySQLConfiguration().isConfigured())
        {
            Logger.GetOrCreate().Info("MySQL configuration found, using MySQLManager.");
            return MySQLManager.getInstance(appConfig.FindMySQLConfiguration(), appConfig.FindAppId());
        }
        else
        {
            Logger.GetOrCreate().Info("Using LocalStorageManager.");
            return new LocalStorageManager();
        }
    }
}
