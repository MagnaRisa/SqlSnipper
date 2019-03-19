package com.magnarisa.SqlSnipper.Database;

import java.sql.*;

public class MySQL_Conn extends Database
{
    private String mHost;
    private String mDatabase;
    private String mDBUser;
    private String mDBIdentifier;

    /**************************************************************************
     * Constructor: MySQL_Conn
     *
     * Description: The primary constructor for a MySQL Connection
     *
     * Parameters:
     * @param hostName - The hostname of the Database you would like to
     *                      connect to
     * @param db - The database name to use from the host
     * @param user - The user in which to login with in order to read/write
     *             from the database
     * @param identifier - The database user's password
     *
     * Return: None
     *************************************************************************/
    public MySQL_Conn (String hostName, String db, String user, String identifier)
    {
        mHost = hostName;
        mDatabase = db;
        mDBUser = user;
        mDBIdentifier = identifier;
    }

    /**************************************************************************
     * Method: dbConnect
     *
     * Description: Creates a MySQL connection to the constructed MySQL_Conn
     *              Object. This method uses the information from the
     *              constructed object to make the connection to the database.
     *
     * @return The connection to the Database
     *************************************************************************/
    public Connection dbConnect ()
    {
        try
        {
            if (mConnection == null || mConnection.isClosed ())
            {
                mConnection = DriverManager.getConnection ("jdbc:mysql://"
                    + mHost + "/" + mDatabase, mDBUser, mDBIdentifier);
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace ();
        }

        return mConnection;
    }
}