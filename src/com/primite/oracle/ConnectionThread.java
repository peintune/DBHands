package com.primite.oracle;

import java.io.Console;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

class connectionThread extends Thread
{
    Connection con = null;

    connectionThread(Connection con)
    {
        this.con = con;
    }

    public void run()
    {
        Process p;
        try
        {
          //  p = Runtime.getRuntime().exec("cmd /c start cmd.exe");
            p= Runtime.getRuntime().exec("cmd /C START C:\\Users\\i322353\\Downloads\\cmder_mini\\Cmder.EXe");
            p.waitFor();
        }
        catch (Exception e1)
        {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        getMetaData(con);
        System.out.println("");
        System.out.println(
                "###############  Please input an sql, sql should end with ';'  ########################");
        StringBuilder sql = new StringBuilder();
        while (true)
        {
            String subsql = new String(readDataFromConsole("SQL> "));
            if (subsql.trim().length() < 1)
                continue;
            if (subsql.trim().endsWith(";"))
            {
                sql.append(subsql);
                try
                {
                    if (sql.length() > 1)
                        executeSql(con,
                                sql.toString().substring(0, sql.length() - 1));
                    sql.setLength(0);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                sql.append(subsql);
                sql.append(" ");
                continue;
            }

        }
    }

    private static String readDataFromConsole(String prompt)
    {
        Console console = System.console();
        if (console == null)
        {
            throw new IllegalStateException("Console is not available!");
        }
        return console.readLine(prompt);
    }

    private static void executeSql(Connection con, String sql) throws Exception
    {
        try
        {
            Statement stmt = con.createStatement();
            boolean hasResultSet = stmt.execute(sql);

            if (hasResultSet)
            {
                try (ResultSet rs = stmt.getResultSet())
                {
                    ResultSetMetaData rsmd = rs.getMetaData();
                    int columnCount = rsmd.getColumnCount();

                    while (rs.next())
                    {

                        for (int i = 0; i < columnCount; i++)
                        {
                            System.out.print(rs.getString(i + 1) + "\t");
                        }
                        System.out.print("\n");
                    }
                }
            }
            else
            {
                System.out.println("The total execute results is"
                        + stmt.getUpdateCount() + " rows");
            }

        }
        catch (Exception e)
        {
            // TODO: handle exception
            e.printStackTrace();
            System.out.println(
                    "ERROR: The sql :" + sql + " execution failed!!!!!!!!!!");
        }

    }

    private static void getMetaData(Connection con)
    {
        try
        {
            System.out.println("Connect success.");
            System.out.println("");
            System.out.println("");
            DatabaseMetaData metadata = con.getMetaData();
            System.out.println(
                    "*************************************************************************************");
            System.out.println(
                    "*************************get DB metadata********************************");
            System.out.println("user name: " + metadata.getUserName());

            System.out.println("DB url: " + metadata.getURL());
            System.out.println(
                    "DB version:" + metadata.getDatabaseProductVersion());
            System.out.println("driver name:" + metadata.getDriverName());
            System.out
                    .println("drviver version:" + metadata.getDriverVersion());
            System.out.println(
                    "*************************************************************************************");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}