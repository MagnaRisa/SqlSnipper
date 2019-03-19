package com.magnarisa.SqlSnipper.Commands;

import com.magnarisa.SqlSnipper.Database.Database;
import com.magnarisa.SqlSnipper.Database.SQLReader;
import com.magnarisa.SqlSnipper.SqlSnipper;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandSnip implements CommandExecutor
{
    private final int ARG_SIZE = 1;

    private SqlSnipper mPlugin;
    private Database mDatabase;
    private Logger mLogger;

    private boolean mbStaged = false;
    private String mStagedFile = null;
    private String mCurrentUser = null;

    /**************************************************************************
     * Constructor:     CommandSnip
     *
     * Description:     Opens a file for reading, specifically in this case an
     *                      SQL setup file using a string resource.
     *
     * Parameters:
     * @param db     - The Database to retrieve and send information to.
     * @param logger - The logger to log information to.
     * @param plugin - The plugin to obtain information and state from.
     *
     * Return: None
     *************************************************************************/
    public CommandSnip (Database db, Logger logger, SqlSnipper plugin)
    {
        mPlugin = plugin;
        mDatabase = db;
        mLogger = logger;
    }

    /**************************************************************************
     * Method:          onCommand
     *
     * Description:     Defines the snip command implementation. This method
     *                      fires when the command alias within the game is
     *                      called via a command.
     *
     * Parameters:
     * @param sender  - The User who issued the command.
     * @param command - The command to be executed.
     * @param label   - The name of the command being issued.
     *
     * Return:
     * @return - True on a successful execution
     *           False on a failed execution
     *************************************************************************/
    @Override
    public boolean onCommand (CommandSender sender, Command command, String label, String[] args)
    {
        // set this within checks. all of these returns are garbage
        boolean bFinished;

        if (!sender.isOp () || !(sender instanceof Player))
        {
            sender.sendMessage ("NOT OPED");
            return false;
        }

        if (args.length != 0)
        {
            if (args[0].equalsIgnoreCase ("list"))
            {
                printFiles (sender);
                return true;
            }
            else if (args[0].equalsIgnoreCase ("help"))
            {
                printHelpMenu (sender);
                return true;
            }
        }
        else
        {
            sender.sendMessage (ChatColor.RED + "Too few Arguments! Use /snip help");
            return false;
        }

        if (mbStaged)
        {
            if (!((Player) sender).getDisplayName ().equalsIgnoreCase (mCurrentUser))
            {
                sender.sendMessage (ChatColor.DARK_AQUA + "Another User is currently snipping, please wait...");
                return false;
            }

            if (args[0].equalsIgnoreCase ("confirm"))
            {
                sender.sendMessage (ChatColor.GREEN + "Command Confirmed!");

                if (!processCommand (mStagedFile, sender))
                {
                    resetState ();
                    return false;
                }
                resetState ();
            }
            else if (args[0].equalsIgnoreCase ("cancel"))
            {
                resetState ();
                sender.sendMessage (ChatColor.GOLD + "Command Canceled!");
            }
        }
        else
        {
            if (args.length != ARG_SIZE)
            {
                sender.sendMessage ("USAGE: /snip <filename>");
                return false;
            }

            if (null == mDatabase)
            {
                mLogger.log (Level.SEVERE, "Database is Null, Cannot Execute SQL Commands!");
                return false;
            }

            mbStaged = true;
            mStagedFile = args[0];
            mCurrentUser = ((Player) sender).getDisplayName ();

            sender.sendMessage (ChatColor.GREEN + "Enter /snip confirm to confirm changes or /snip cancel to quit");
        }
        return true;
    }

    /**************************************************************************
     * Method:          processCommand
     *
     * Description:     This private method actually executes the command
     *                      once the user correctly pass the protections of
     *                      the plugin.
     *
     * Parameters:
     * @param resource - The resource file to process.
     * @param sender   - The sender of the command, used for information.
     *
     * Return:
     * @return True - If processing is successful
     *         False - If processing failed
     *************************************************************************/
    private boolean processCommand (String resource, CommandSender sender)
    {
        InputStream sqlStream;
        boolean success = true;
        SQLReader reader = SqlSnipper.getReader ();

        // Finish the path, get the dir path from the config file, and concat to the path
        String Path = "../";
        System.setProperty ("user.dir", Path);

        // Test this!
        sender.sendMessage (System.getProperty ("user.dir"));

        try
        {
            sqlStream = new FileInputStream (new File (mPlugin.getDataFolder (), resource));

            sender.sendMessage (mPlugin.getDataFolder ().toString());

            reader.openReader (sqlStream);

            mDatabase.executeStream (reader);

            reader.closeReader ();
        }
        catch (IOException e)
        {
            sender.sendMessage (ChatColor.DARK_RED + "No file found with that name!");
            success = false;
        }
        catch (SQLException e)
        {
            sender.sendMessage (ChatColor.DARK_RED + "Error occurred while executing SQL Statements from " + mStagedFile);
            success = false;
        }

        return success;
    }

    /**************************************************************************
     * Method:          resetState
     *
     * Description:     This method will reset the commands current state.
     *                      This way there is no concurrency or work around
     *                      that will need to take place to get the command to
     *                      work properly.
     *
     * Parameters:      None
     *
     * Return:          None
     *************************************************************************/
    private void resetState ()
    {
        mbStaged = false;
        mStagedFile = null;
        mCurrentUser = null;
    }

    /**************************************************************************
     * Method:          printFiles
     *
     * Description:     Prints the list of available SQL files to be used
     *                  on the given database defined in the config file.
     *
     * Parameters:
     * @param sender - The sender of the command, used to print the info to.
     *
     * Return:         None
     *************************************************************************/
    private void printFiles (CommandSender sender)
    {
        File directory = mPlugin.getDataFolder ();
        File[] fileDir = directory.listFiles ();
        sender.sendMessage (directory.toString ());
        try
        {
            for (File file : fileDir)
            {
                if (file.isFile ())
                {
                    sender.sendMessage (file.getName ());
                }
            }
        }
        catch (NullPointerException e)
        {
            mLogger.severe ("Null Ptr Exception: " + e.getMessage ());
        }
    }

    /**************************************************************************
     * Method:          printHelpMenu
     *
     * Description:     Prints the help menu of the plugin
     *
     * Parameters:
     * @param sender - The sender of the command, used to print the help info to.
     *
     * Return:         None
     *************************************************************************/
    private void printHelpMenu (CommandSender sender)
    {
        sender.sendMessage (ChatColor.GREEN + "Welcome to the SQLSnipper Help Menu!");
        sender.sendMessage (ChatColor.GRAY + "/snip help -- Displays this help menu.");
        sender.sendMessage (ChatColor.GRAY + "/snip list -- Lists the file that can be snipped.");
        sender.sendMessage (ChatColor.AQUA + "/snip [filename] -- Stages the file specified to be snipped.");
        sender.sendMessage (ChatColor.GREEN + "/snip confirm -- Confirms a staged snip file to be snipped and snips it.");
        sender.sendMessage (ChatColor.GOLD + "/snip cancel -- Cancels the staged file to be snipped.");
    }

    // Print the contents of a given file [DEBUG]
    private void printFileContents (SQLReader reader, CommandSender sender)
    {
        String statement;

        statement = reader.readStatement ();
        while (!statement.equals (SQLReader.EOF))
        {
            sender.sendMessage (statement);
            statement = reader.readStatement ();
        }
    }
}
