package com.magnarisa.SqlSnipper.Database;

import com.magnarisa.SqlSnipper.SqlSnipper;

import java.util.logging.Level;

public class DatabaseFactory
{
    /**************************************************************************
     * Method:      buildDatabase
     *
     * Description: This method builds the correct database depending on what
     *              the user intends to use.
     *
     * Parameters:
     * @param plugin The plugin to build the DB Object for
     *
     * Return:      Returns the db type the user specifies in the config file.
     *************************************************************************/
    public static Database buildDatabase (SqlSnipper plugin)
    {
        String dbType = plugin.getConfig ().getString ("DatabaseType");
        Database db = null;

        if (dbType.equalsIgnoreCase ("sqlite"))
        {
            plugin.getLogger ().log (Level.INFO, "Constructing SQLite Connection.");
            db = new SQLite_Conn (plugin);
        }
        else if (dbType.equalsIgnoreCase ("mysql"))
        {
            String host = plugin.getConfig ().getString ("MySQL.host");
            String database = plugin.getConfig ().getString ("MySQL.db_name");
            String user = plugin.getConfig ().getString ("MySQL.user");
            String identifier = plugin.getConfig ().getString ("MySQL.password");

            if (host.equalsIgnoreCase ("none") || database.equalsIgnoreCase ("none")
                || user.equalsIgnoreCase ("none") || identifier.equalsIgnoreCase ("none"))
            {
                plugin.getLogger ().log (Level.SEVERE, "MySQL Database info is not setup, disabling plugin!");
                db = null;
            }
            else
            {
                plugin.getLogger().log (Level.INFO,"Constructing MySQL Connection.");
                db = new MySQL_Conn (host, database, user, identifier);
            }
        }
        return db;
    }
}
