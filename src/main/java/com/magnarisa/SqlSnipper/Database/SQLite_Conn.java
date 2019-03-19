package com.magnarisa.SqlSnipper.Database;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.*;
import java.util.logging.Level;

public class SQLite_Conn extends Database
{
    private JavaPlugin mPlugin;

    /*************************************************************************
     * Constructor: SQLite_Conn
     *
     * Description: The primary constructor for an SQLite Connection
     *
     * Parameters:  None
     *
     * Return:      None
     ************************************************************************/
    public SQLite_Conn (JavaPlugin plugin)
    {
        mPlugin = plugin;
    }

    /*************************************************************************
     * Method: dbConnect
     *
     * Description: This method will create and return a MySQL database
     *              connection.
     *
     * Parameters: None
     *
     * Return:
     * @return The connection to the database
     ************************************************************************/
    public Connection dbConnect ()
    {
        File dataFolder;
        String dbPath = mPlugin.getConfig ().getString ("DatabasePath");
        String dbName = mPlugin.getConfig ().getString ("DatabaseName");

        dataFolder = new File (mPlugin.getDataFolder (), dbPath + "\\" + dbName);

        // This bit is Garbage, but I am lazy and want to get this done!
        if (dbPath.equalsIgnoreCase ("none"))
        {
            mPlugin.getLogger().severe ("Default database path found! Please set the file path in config.yml to "
                + "the database file path before using the Plugin. Disabling Plugin!");
            Bukkit.getServer().getPluginManager ().disablePlugin (mPlugin);
            return null;
        }

        if (!dataFolder.exists ())
        {
            mPlugin.getLogger ().severe ("Error! Could not load Database file found in " + dbPath + "\\" + dbName);
        }

        mPlugin.getLogger ().info ("The File Path is: " + dataFolder.getPath ());

        try
        {
            if (mConnection == null || mConnection.isClosed ())
            {
                mConnection = DriverManager.getConnection ("jdbc:sqlite:"
                    + dataFolder);
                mPlugin.getLogger ().info ("Database established connection!");
            }

            return mConnection;
        }
        catch (SQLException e)
        {
            Bukkit.getLogger().log (Level.SEVERE, "SQLite Exception on Initialization" + e);
        }

        return mConnection;
    }
}
