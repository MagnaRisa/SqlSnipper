package com.magnarisa.SqlSnipper.Database;

import java.sql.*;

public abstract class Database
{
    protected Connection mConnection;
    /**************************************************************************
     * Constructor: Database
     *
     * Description: The primary constructor for a Database connection
     *
     * Parameters:  None
     *
     * Return:      None
     *************************************************************************/
    public Database ()
    { }

    /**************************************************************************
     * Method:      dbConnect
     *
     * Description: The common method that all extensions of this Database
     *              abstract object will need to implement.
     *
     * Parameters:  None
     *
     * Return:      The Specific connection of the implemented database.
     *************************************************************************/
    public abstract Connection dbConnect ();

    /**************************************************************************
     * Method:      dbClose
     *
     * Description: The method will close the prep statement and result set
     *              passed into the method. If either of these are already
     *              null, then they will not be closed.
     *
     * Parameters:  None
     *
     * Return:      None
     *************************************************************************/
    public void dbClose ()
    {
        try
        {
            if (mConnection != null)
            {
                mConnection.close();
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace ();
        }
    }

    /**************************************************************************
     * Method: dbCloseResources
     *
     * Description: The method will close the prep statement and result set
     *              passed into the method. If either of these are already
     *              null, then they will not be closed.
     *
     * Parameters:
     * @param stmt - The statement to close
     * @param set - The result set to close
     *
     * Return: None
     *************************************************************************/
    public void dbCloseResources (PreparedStatement stmt, ResultSet set)
    {
        try
        {
            if (stmt != null)
            {
                stmt.close ();
            }
            if (set != null)
            {
                set.close ();
            }
        }
        catch (SQLException exception)
        {
            exception.printStackTrace ();
        }
    }

    /**************************************************************************
     * Method:      executeStream
     *
     * Description: This method executes a stream of SQL statements from the
     *              given SQL Reader object.
     *
     * Exceptions:
     * @throws SQLException - If an error occurs here we are throwing the
     *                        exception to be handled where this method
     *                        is called.
     *
     * Parameters:
     * @param reader - The stream of SQL Statements to read
     *
     * Return: None
     *************************************************************************/
    public void executeStream (SQLReader reader) throws SQLException
    {
        String statement;

        statement = reader.readStatement ();
        while (!statement.equals (SQLReader.EOF))
        {
            executeStatement (statement, mConnection);

            statement = reader.readStatement ();
        }
    }

    /**************************************************************************
     * Method:      executeStatement
     *
     * Description: This method executes sql statements that do not have a
     *              return value.
     *
     * Exceptions:
     * @throws SQLException - If an error occurs here we are throwing the
     *                        exception to be handled where this method
     *                        is called.
     *
     * Parameters:
     * @param sqlStatement - An SQL statement that will be executed.
     * @param conn - The connection to the database.
     *
     * Return: None
     *************************************************************************/
    private void executeStatement (String sqlStatement, Connection conn)
        throws SQLException
    {
        PreparedStatement statement = conn.prepareStatement (sqlStatement);
        statement.execute();
        statement.close ();
    }
}
