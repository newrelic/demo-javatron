package com.newrelic.api.Behaviors;

import java.sql.SQLException;

import com.newrelic.lib.Inventory.MySQLRepository;

public class InvalidQueryBehavior extends Behavior
{
    public static final String Name = "INVALID-QUERY";

    private MySQLRepository repo;

    public InvalidQueryBehavior(MySQLRepository repo)
    {
        this.setName(InvalidQueryBehavior.Name);
        this.repo = repo;
    }

    public void Execute() throws SQLException, Exception
    {
        super.Execute();
        if (this.repo != null && this.repo.isConnected())
        {
            repo.queryInvalidTable();
        }
    }

}
