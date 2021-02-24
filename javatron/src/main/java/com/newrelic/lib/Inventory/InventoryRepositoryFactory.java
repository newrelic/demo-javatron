package com.newrelic.lib.Inventory;

import com.newrelic.lib.IAppConfigRepository;
import com.newrelic.lib.Logger;

public class InventoryRepositoryFactory
{
    public static IInventoryRepository createInventoryRepository(IAppConfigRepository appConfig)
    {
        if (appConfig.FindMySQLConfiguration().isConfigured())
        {
            Logger.GetOrCreate().Info("MySQL configuration found, using MySQLManager.");
            return MySQLRepository.getInstance(appConfig.FindMySQLConfiguration(), appConfig.FindAppId());
        }
        else
        {
            Logger.GetOrCreate().Info("Using LocalStorageManager.");
            return new LocalStorageRepository();
        }
    }
}
