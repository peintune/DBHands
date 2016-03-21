package com.primite.oracle;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class OracleMain
{
    static String root = System.getProperty("user.dir");

    public static void main(String[] args)
    {
        System.out.println("");
        System.out.println(
                "###############       You can input 'help' to help yourself    ########################");
        System.out.println("");
        // StringBuilder sql = new StringBuilder();
        while (true)
        {
            String subsql = new String(readDataFromConsole("Command> "));
            subsql = commandRoute(subsql);
        }
    }

    private static String commandRoute(String subsql)
    {
        String sql = "";
        switch (subsql)
        {
            case "quit":
            case "exit":
                System.exit(0);
                break;
            case "create":
                String hostInfo = createConnection();
                try
                {
                    Runtime.getRuntime()
                            .exec("cmd /c start java -cp " + root
                                    + File.separator
                                    + "executeSqls.jar com.primite.oracle.OracleOpenConnection "
                                    + hostInfo);
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                break;
            case "list":
                HashSet<String> hostSet = readTxtFile("connections.txt");
                Iterator<String> ite = hostSet.iterator();
                System.out.println(
                        "#########################################################################################");
                System.out.println(
                        "############  Please select blow connections to connect(input the id number) : ##########");
                System.out.println(
                        "############  id               url                             username        ##########");
                System.out.println(
                        "############  0 Or other to return                                               ##########");
                int i = 1;
                List connectionList = new ArrayList<String>();
                while (ite.hasNext())
                {
                    String result = ite.next();
                    String url = result.split("##")[0];
                    String username = result.split("##")[1].split(":")[0];
                    String password = result.split("##")[1].split(":")[1];
                    connectionList.add(result);
                    System.out.println("############  " + i + "              "
                            + url + "           " + username + "   ##########");
                    i++;
                }
                String id = readDataFromConsole("connection> ");
                String connectionInfo ="";
                try
                {
                    if (Integer.parseInt(id) == 0)
                        break;
                    connectionInfo = (String) connectionList
                            .get(Integer.parseInt(id) - 1);
                }
                catch (Exception e)
                {
                    break;
                    // TODO: handle exception
                }

                try
                {
                    Runtime.getRuntime()
                            .exec("cmd /c start java -cp " + root
                                    + File.separator
                                    + "executeSqls.jar com.primite.oracle.OracleOpenConnection "+connectionInfo);
                }
                catch (Exception e)
                {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                break;
            case "help":
            case "fuck":
                System.out.println(
                        "#########################################################################################");
                System.out.println(
                        "###############  Please input an sql, sql should end with ';' :  ########################");
                System.out.println("  Commands :  ");
                System.out.println(
                        "            'help/fuck',to get helps and command introduction");
                System.out
                        .println("            'quit/exit',to exit the system");
                System.out.println(
                        "            'create',create and store and connecion to target database");
                System.out.println(
                        "            'list',list a history connections to connect");
                System.out.println(
                        "#########################################################################################");
            default:
                sql = subsql;
                break;

        }

        return sql;
    }

    private static String createConnection()
    {
        System.out.println(
                "#########################################################################################");
        System.out.println(
                "#######################  Start to create an connection   ################################");
        System.out.println(
                "#########################################################################################");
        System.out.println("");
        System.out.println(
                "###############  please input  host name like: 10.38.10.23 or localhost  #############");
        String hostName = readDataFromConsole("hostName:");
        System.out.println(
                "###############         please input  port number like: 8871              ############");
        String portNumber = readDataFromConsole("portNumber:");
        System.out.println(
                "###############      please input  schema/database name like: testdb      ############");
        String dbName = readDataFromConsole("dbName:");
        System.out.println(
                "###############     please input  database userName name like: ra_user    ############");
        String userName = readDataFromConsole("userName:");
        System.out.println(
                "###############     please input  database password name like: *******    ############");
        String password = readDataFromConsole("password:");
        Connection con = openConnection(
                hostName + ":" + portNumber + ":" + dbName, userName, password);
        if (null != con)
        {
            writeTxtFile("connections.txt", hostName + ":" + portNumber + ":"
                    + dbName + "##" + userName + ":" + password);
            return hostName + ":" + portNumber + ":" + dbName + "##" + userName
                    + ":" + password;
        }
        else
        {
            System.out.println(
                    "###############            ERROR:Open connection failed            ############");
            return null;
        }
    }

    public static void writeTxtFile(String filePath, String hostString)
    {
        HashSet<String> hostSet = readTxtFile(filePath);
        if (!hostSet.contains(hostString))
        {
            File f = new File(filePath);
            Writer out = null;
            try
            {
                out = new FileWriter(f, true);
                out.write(hostString + "\n");
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
            finally
            {
                try
                {
                    out.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

        }
    }

    public static HashSet<String> readTxtFile(String filePath)
    {
        HashSet<String> hosts = new HashSet<String>();
        try
        {
            String encoding = "UTF-8";
            File file = new File(filePath);
            if (file.isFile() && file.exists())
            {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt = null;
                while ((lineTxt = bufferedReader.readLine()) != null)
                {
                    if (lineTxt.length() > 1)
                    {
                        hosts.add(lineTxt);
                    }
                }
                read.close();
            }
            else
            {
                System.out.println("Can't found file");
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return hosts;
    }

    private static Connection openConnection(String urlString, String user,
            String password)
    {
        Connection con = null;
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
        return con;
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

}