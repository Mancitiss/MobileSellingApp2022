package org.duckdns.mancitiss.buyfoodserver;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

import com.google.gson.Gson;

public class ServerMain {
    // main 
    static Connection sql;
    static String cnurl;
    static final String img_path = "F:\\App\\BuyFoodServer\\Products\\images\\";
    
    static ExecutorService executor = Executors.newCachedThreadPool();

    public static Gson gson = new Gson();
    public static java.util.SplittableRandom rand = new java.util.SplittableRandom();
    public static final long token_expiration_time = 1000 * 60 * 60 * 24 * 1; // 1 day
    public static final int loadCount = 5;
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
            System.out.println("Server at: " + ss.getInetAddress());
            try {
                while (true) {
                    SSLSocket client = (SSLSocket) ss.accept();
                    System.out.println("Connected");
                    try {
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

    public static void handleException(String se) {
        try {
            if (se.contains("open and available Connection")) {
                sql = DriverManager.getConnection(cnurl);
            } 
            else if (se.contains("Execution Timeout Expired")) {
                sql = DriverManager.getConnection(cnurl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
