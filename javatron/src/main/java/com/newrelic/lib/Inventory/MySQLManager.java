package com.newrelic.lib.Inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.*;

import com.newrelic.lib.Logger;
import com.newrelic.lib.AppConfigMySQLConfiguration;

public class MySQLManager implements IInventoryManager
{
    private static MySQLManager singletonInstance;

    private Connection conn;
    private AppConfigMySQLConfiguration config;
    private String databaseName;
    private InventoryRepository repo;


    private MySQLManager(AppConfigMySQLConfiguration config, String databaseName)
    {
        this.repo = new InventoryRepository();
        this.config = config;
        this.databaseName = databaseName;
    }

    private MySQLManager(AppConfigMySQLConfiguration config, String databaseName, InventoryRepository repo)
    {
        this.repo = repo;
        this.config = config;
        this.databaseName = databaseName;
    }

    public static MySQLManager getInstance(AppConfigMySQLConfiguration config, String databaseName)
    {
       if (MySQLManager.singletonInstance == null) {
           MySQLManager.singletonInstance = new MySQLManager(config, databaseName);
       }

       return MySQLManager.singletonInstance;
    }

    public static MySQLManager getInstance(AppConfigMySQLConfiguration config, String databaseName, InventoryRepository repo)
    {
       if (MySQLManager.singletonInstance == null) {
           MySQLManager.singletonInstance = new MySQLManager(config, databaseName, repo);
       }

       return MySQLManager.singletonInstance;
    }

    public Inventory[] Query() throws Exception
    {
        if (!initialize())
        {
            throw new Exception("Unable to connect to the database.");
        }

        var results = runQuery("SELECT * FROM inventory;");
        if (results == null)
        {
            throw new Exception("Unable to query database.");
        }
        return extractInventory(results);
    }

    public Inventory Query(String id) throws Exception
    {
        if (!initialize())
        {
            throw new Exception("Unable to connect to the database.");
        }

        var query = String.format("SELECT * FROM inventory WHERE ID = %s;", id);
        var results = runQuery(query);
        if (results == null)
        {
            throw new Exception("Unable to query database.");
        }

        var inventory = extractInventory(results);

        if (inventory.length > 0)
        {
            return inventory[0];
        }

        return null;
    }

    private ResultSet runQuery(String queryString)
    {
        try
        {
            var statement = this.conn.createStatement();
            var results = statement.executeQuery(queryString);
            statement.close();

            return results;
        }
        catch (SQLException ex)
        {
            Logger.GetOrCreate().Error(ex.getMessage());
        }

        return null;
    }

    private Inventory[] extractInventory(ResultSet results)
    {
        try
        {
            var rows = new ArrayList<Inventory>();
            while(results.next())
            {
                Hashtable<String,String> entry = new Hashtable<>();
                var id = results.getString("ID");
                var item = results.getString("ITEM");
                var price = results.getString("PRICE");
                var sku = results.getString("SKU");

                entry.put("id", id);
                entry.put("item", item);
                entry.put("price", price);
                entry.put("sku", sku);

                rows.add(Inventory.create(entry));
            }
            var output = new Inventory[rows.size()];
            return rows.toArray(output);
        }
        catch (SQLException ex)
        {
            Logger.GetOrCreate().Error(ex.getMessage());
            return null;
        }
    }

    private boolean initialize()
    {
        if (this.conn != null)
        {
            return true;
        }
        var baseConnectionString = String.format("jdbc:mariadb://%s:%s", config.getHost(),
                config.getPort());
        var connectionStringWithDB = String.format("%s/%s", baseConnectionString, this.databaseName);

        var isSetup = true;
        isSetup = isSetup && connect(baseConnectionString);
        isSetup = isSetup && createDatabase(this.databaseName);
        isSetup = isSetup && connect(connectionStringWithDB);
        isSetup = isSetup && createTable();
        return isSetup;
    }

    private boolean connect(String connectionString)
    {
        var config = this.config;
        try
        {
            Class.forName("org.mariadb.jdbc.Driver");
            if (this.conn != null)
            {
                this.conn.close();
            }

            this.conn = DriverManager.getConnection(connectionString, config.getUser(), config.getPassword());
            return true;
        }
        catch (SQLException ex)
        {
            Logger.GetOrCreate().Error(ex.getMessage());
        }
        catch (ClassNotFoundException ex)
        {
            Logger.GetOrCreate().Error(ex.getMessage());
        }

        return false;
    }

    private boolean createDatabase(String name)
    {
        var query = String.format("CREATE DATABASE IF NOT EXISTS %s;", name);
        var result = runQuery(query);
        if (result == null)
        {
            return false;
        }
        return true;
    }

    private boolean createTable()
    {
        var query = String.format("CREATE TABLE IF NOT EXISTS `%s`.`inventory` (`id` INT UNSIGNED NOT NULL, `item` VARCHAR(255) NOT NULL,`price` VARCHAR(255) NOT NULL, `sku` VARCHAR(255) NOT NULL, PRIMARY KEY (id));", this.databaseName);
        var result = runQuery(query);
        var inventory = this.repo.FindAll();

        for (Inventory item : inventory)
        {
            var insertQuery = String.format(
                    "INSERT IGNORE INTO inventory (id, item, price, sku) VALUES ('%s', '%s', '%s', '%s')",
                    item.getId(), item.getItem(), item.getPrice(), item.getSku());
            runQuery(insertQuery);
        }

        if (result == null)
        {
            return false;
        }
        return true;
    }

}
