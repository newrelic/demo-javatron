package com.newrelic.lib.Inventory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.ResultSet;

import java.util.*;

import com.newrelic.lib.Logger;
import com.newrelic.lib.AppConfigMySQLConfiguration;

public class MySQLManager implements IInventoryManager {

    private Connection conn;
    private AppConfigMySQLConfiguration config;
    private String databaseName;
    private InventoryRepository repo;

    public MySQLManager(AppConfigMySQLConfiguration config, String databaseName) {
        this.repo = new InventoryRepository();
        this.config = config;
        this.databaseName = databaseName;
    }

    public MySQLManager(AppConfigMySQLConfiguration config, String databaseName, InventoryRepository repo) {
        this.repo = repo;
        this.config = config;
        this.databaseName = databaseName;
    }

    public Inventory[] Query() throws Exception {
        if (this.conn == null) {
            initialize();
        }

        var results = runQuery("SELECT * FROM inventory;");
        if (results == null) {
            throw new Exception("Unable to query database.");
        }

        var inventory = new ArrayList<Inventory>();

        while (results.next()) {
            inventory.add(extractInventory(results));
        }

        var output = new Inventory[inventory.size()];
        return inventory.toArray(output);
    }

    public Inventory Query(String id) throws Exception {
        if (this.conn == null) {
            initialize();
        }

        var query = String.format("SELECT * FROM inventory WHERE ID = %s;", id);
        var results = runQuery(query);
        if (results == null) {
            throw new Exception("Unable to query database.");
        }

        var inventory = new ArrayList<Inventory>();

        while (results.next()) {
            inventory.add(extractInventory(results));
        }

        if (inventory.size() > 0) {
            return inventory.get(0);
        }

        return null;
    }

    private ResultSet runQuery(String queryString) {
        try {
            var statement = this.conn.createStatement();
            var results = statement.executeQuery(queryString);
            statement.close();

            return results;
        }
        catch (SQLException ex) {
            Logger.GetOrCreate().Error(ex.getMessage());
        }

        return null;
    }

    private Inventory extractInventory(ResultSet row) {
        try {
            Hashtable<String,String> entry = new Hashtable<>();
            var id = row.getString("ID");
            var item = row.getString("ITEM");
            var price = row.getString("PRICE");
            var sku = row.getString("SKU");

            entry.put("id", id);
            entry.put("item", item);
            entry.put("price", price);
            entry.put("sku", sku);

            return Inventory.create(entry);
        }
        catch (SQLException ex) {
            Logger.GetOrCreate().Error(ex.getMessage());
            return null;
        }
    }

    private void initialize() {
        var baseConnectionString = String.format("jdbc:mariadb://%s:%s", config.getHost(),
                config.getPort());
        var connectionStringWithDB = String.format("%s/%s", baseConnectionString, this.databaseName);

        connect(baseConnectionString);
        createDatabase(this.databaseName);
        connect(connectionStringWithDB);
        createTable();
    }

    private boolean connect(String connectionString) {
        var config = this.config;
        try {
            Class.forName("org.mariadb.jdbc.Driver");
            if (this.conn != null) {
                this.conn.close();
            }
            Logger.GetOrCreate().Info(String.format("Using connection string: %s", connectionString));
            this.conn = DriverManager.getConnection(connectionString, config.getUser(), config.getPassword());
            return true;
        }
        catch (SQLException ex) {
            Logger.GetOrCreate().Error("Connect error");
            Logger.GetOrCreate().Error(ex.getMessage());
            Logger.GetOrCreate().Error(ex.getSQLState());
            Logger.GetOrCreate().Error(String.valueOf(ex.getErrorCode()));
        }
        catch (ClassNotFoundException ex) {
            Logger.GetOrCreate().Error(ex.getMessage());
        }

        return false;
    }

    private boolean createDatabase(String name) {
        var query = String.format("CREATE DATABASE IF NOT EXISTS %s;", name);
        var result = runQuery(query);
        if (result == null) {
            return false;
        }
        return true;
    }

    private boolean createTable() {
        var query = String.format("CREATE TABLE IF NOT EXISTS `%s`.`inventory` (`id` VARCHAR(255) NOT NULL, `item` VARCHAR(255) NOT NULL,`price` VARCHAR(255) NOT NULL, `sku` VARCHAR(255) NOT NULL);", this.databaseName);
        var result = runQuery(query);
        var inventory = this.repo.FindAll();

        for (Inventory item : inventory) {
            var insertQuery = String.format(
                    "INSERT INTO inventory (id, item, price, sku) VALUES ('%s', '%s', '%s', '%s')",
                    item.getId(), item.getItem(), item.getPrice(), item.getSku());
            runQuery(insertQuery);
        }

        if (result == null) {
            return false;
        }
        return true;
    }

}
