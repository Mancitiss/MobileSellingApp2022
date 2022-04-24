package org.duckdns.mancitiss.buyfoodserver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class ServerMain {
    // main 
    static Connection sql;
    static String cnurl;
    
    static ExecutorService executor = Executors.newCachedThreadPool();

    static ConcurrentHashMap<String, Client> sessions = new ConcurrentHashMap<String, Client>();
    public static void main(String[] args) {
        try {
            String connectionUrl = "jdbc:sqlserver://" + System.getenv("DBServer") + ";"
                    + "databaseName=" + System.getenv("DBicatalog_BuyFood") + ";"
                    + "user=" + System.getenv("DBusername") + ";"
                    + "password=" + System.getenv("DBpassword") + ";"
                    + "loginTimeout=10;encrypt=true;trustServerCertificate=true";
            cnurl = connectionUrl;
            System.setProperty("javax.net.ssl.keyStore", "F:/Python Learning/web_cert2022/server.pfx");
            System.setProperty("javax.net.ssl.keyStorePassword", "RoRo");
            try (Connection sqlr = DriverManager.getConnection(connectionUrl)) {
                sql = sqlr;
                ExecuteServer();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.toString());
        }
    }

    private static void ExecuteServer() throws IOException {
        SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        try (SSLServerSocket ss = (SSLServerSocket) ssf.createServerSocket(11111)) {
            // translate below line of code from C#
            // Console.WriteLine("Server at: {0}", IPAddress.Any);
            System.out.println("Server at: " + ss.getInetAddress());
            try {
                while (true) {
                    SSLSocket client = (SSLSocket) ss.accept();
                    
                    try {
                        // translate below line of code from C#
                        // ThreadPool.QueueUserWorkItem(Receive_from_socket_not_logged_in, client);
                        executor.execute(new Receive_from_socket_not_logged_in(client));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    client = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static void shutdown(String token) {
        
        try {
            sessions.get(token).stream.close();
        } catch (Exception e) {

        }
        try {
            sessions.get(token).DIS.close();
        } catch (Exception e) {

        }
        try {
            sessions.get(token).client.close();
        } catch (Exception e) {

        }
        try {
            sessions.remove(token);
            System.out.println(token + " quitted");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void handleException(String token, String se) {
        try {
            if (se.contains("open and available Connection")) {
                sql = DriverManager.getConnection(cnurl);
            } else if (se.contains("Execution Timeout Expired")) {
                sql = DriverManager.getConnection(cnurl);
            } else if (se.contains("was forcibly closed")) {
                shutdown(token);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
