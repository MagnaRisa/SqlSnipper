package com.magnarisa.SqlSnipper;

import com.magnarisa.SqlSnipper.Database.Database;
import com.magnarisa.SqlSnipper.Database.DatabaseFactory;
import com.magnarisa.SqlSnipper.Database.SQLReader;
import com.magnarisa.SqlSnipper.Commands.CommandSnip;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Level;

public class SqlSnipper extends JavaPlugin
{
    private Database mDatabase;
    private static SQLReader mReader;
    private File mConfigFile;

    @Override
    public void onEnable ()
    {
        // Setup the plugin directory
        if (!getDataFolder ().exists ())
        {
            getDataFolder ().mkdir ();
            getLogger().info ("Creating plugin data folder");
        }

        // Grab the default config
        obtainDefaultConfig ();

        // Build the appropriate database
        mDatabase = DatabaseFactory.buildDatabase (this);

        if (null == mDatabase)
        {
            getLogger ().severe ("Database cannot be NULL! Disabling plugin");
            getServer ().getPluginManager ().disablePlugin (this);
        }
        else
        {
            if (mDatabase.dbConnect () != null)
            {
                // Register the snip command
                this.getCommand ("snip").setExecutor (new CommandSnip (mDatabase, this.getLogger(), this));

                this.getLogger().log (Level.INFO, "Plugin is Enabled!");
            }
        }
    }

    @Override
    public void onDisable ()
    {
        mDatabase.dbClose ();
    }

    public static SQLReader getReader ()
    {
        return mReader;
    }

    private void obtainDefaultConfig ()
    {
        mConfigFile = new File (this.getDataFolder (), "config.yml");

        if (!mConfigFile.exists ())
        {
            getLogger ().info ("config.yml Not Found, Creating File and Loading Defaults!");
            this.saveDefaultConfig ();
        }
    }
}
