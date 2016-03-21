package com.primite.oracle;

import java.io.Console;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

public class OracleOpenConnection
{
    private static Connection con;

    String hostInfo = "";

    public static void main(String[] args)
    {
        System.out.println("");
        OracleOpenConnection oc = new OracleOpenConnection(args[0]);
        oc.run();
    }

    OracleOpenConnection(String hostInfo)
    {
        this.hostInfo = hostInfo;
    }

    private void openConnection(String urlString, String user, String password)
    {
        try
        {
            Class.forName("oracle.jdbc.driver.OracleDriver");
            String url = "jdbc:oracle:" + "thin:@" + urlString;
            System.out.println("try to connection DB--->   " + url);
            System.out.println(
                    "..................................................................................................");
            con = DriverManager.getConnection(url, user, password);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        String url = hostInfo.split("##")[0];
        String username = hostInfo.split("##")[1].split(":")[0];
        String password = hostInfo.split("##")[1].split(":")[1];
        openConnection(url, username, password);
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
            if (subsql.trim().equals("quit") || subsql.trim().equals("exit"))
                System.exit(0);
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
                    System.out.println("");
                    while (rs.next())
                    {

                        for (int i = 0; i < columnCount; i++)
                        {
                            System.out.print(rs.getString(i + 1) + "\t");
                        }
                        System.out.print("\n");
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                    // TODO: handle exception
                }
            }
            else
            {
                System.out.println("The total execute results is "
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
            System.out.println("");
            System.out.println("Connect success.");
            System.out.println("");
            DatabaseMetaData metadata = con.getMetaData();
            System.out.println(
                    "*************************************************************************************");
            System.out.println(
                    "****************************** **  get DB metadata   ********************************");
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