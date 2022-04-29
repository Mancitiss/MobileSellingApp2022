package org.duckdns.mancitiss.buyfoodserver;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;
import javax.net.ssl.SSLSocket;
import javax.swing.ImageIcon;

public class Receive_from_socket_not_logged_in implements Runnable {

    SSLSocket client;

    public Receive_from_socket_not_logged_in(SSLSocket client) {
        this.client = client;
    }

    private static final SecureRandom secureRandom = new SecureRandom(); //threadsafe
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder(); //threadsafe

    public static String generateNewToken() {
        byte[] randomBytes = new byte[24];
        secureRandom.nextBytes(randomBytes);
        return base64Encoder.encodeToString(randomBytes);
    }

    private String Send_new_token(DataOutputStream DOS) {
        // create new token that is not in database
        String new_token = generateNewToken();
        while (true) {
            try (PreparedStatement stmt = ServerMain.sql.prepareStatement("SELECT * FROM TOKENS WHERE token = ?");)
            {
                stmt.setString(1, new_token);
                try(ResultSet rs = stmt.executeQuery();)
                {
                    if (!rs.next()) {
                        try(PreparedStatement stmt2 = ServerMain.sql.prepareStatement("INSERT INTO TOKEN (token, username, expirationDate) VALUES (?, NULL, ?)");)
                        {
                            stmt2.setString(1, new_token);
                            stmt2.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis() + ServerMain.token_expiration_time));
                            stmt2.executeUpdate();
                            break;
                        }
                    }
                }
            } 
            catch (Exception e) {
                e.printStackTrace();
            }
            new_token = generateNewToken();
        }
        try {
            DOS.write(Tools.combine("0002".getBytes(StandardCharsets.UTF_16LE), new_token.getBytes(StandardCharsets.US_ASCII)));
            DOS.flush();
            System.out.println("Sent 0002 to client: "+ new_token + " " + new_token.length());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new_token;
    }

    @Override
    public void run() {
        DataInputStream DIS = null;
        DataOutputStream DOS = null;
        try {
            // create streams from client
            DIS = new DataInputStream(client.getInputStream());
            DOS = new DataOutputStream(client.getOutputStream());
            //
            String data = Tools.receive_unicode(DIS, 8);
            //
            if (data != null && !data.isBlank()) {
                String instruction = data;
                System.out.println("Received instruction: " + instruction);
                
                if (instruction.equals("0012")) {
                    Thread.sleep(100);
                    data = Tools.receive_ASCII(DIS, 32);
                    System.out.println(data);
                    try{
                        DIS.close();
                    } catch (Exception e) {}
                    try{
                        DOS.close();
                    } catch (Exception e) {}
                    try{
                        client.close();
                    } catch (Exception e) {}
                    
                    boolean pass = false;
                    if (ServerMain.sessions.containsKey(data) && ServerMain.sessions.get(data).is_waited.getAndSet(1) == 1) {
                        pass = true;
                    }
                    int h = 0;
                    while (!pass && h++ < 20 && !ServerMain.sessions.containsKey(data)) {
                        Thread.sleep(1000);
                    }
                    
                    if (!pass && ServerMain.sessions.containsKey(data)){
                        try{
                            boolean do_work = false;
                            while(ServerMain.sessions.get(data).is_locked.getAndSet(1) == 1){
                                Thread.sleep(1000);
                            }
                            
                            if (ServerMain.sessions.get(data).client.isConnected()){
                                    try{
                                        do_work = true;
                                        ServerMain.executor.execute(new Receive_message(data));
                                    } catch (Exception e){
                                        ServerMain.handleException(data, e.toString());
                                        e.printStackTrace();
                                    }
                            } 
                            else {
                                ServerMain.shutdown(data);
                            }
                            if (!do_work) ServerMain.sessions.get(data).is_locked.set(0);
                        } 
                        catch (Exception clientquit){
                            ServerMain.shutdown(data);
                        }
                        finally{
                            try{
                                ServerMain.sessions.get(data).is_waited.set(0);
                            } catch (Exception e){}
                        }
                    }
                }
                else if (instruction.equals("1412")){
                    String ID = Tools.receive_ASCII_Automatically(DIS);
                    try{
                        // check if ID is in database table PRODUCT
                        try(PreparedStatement stmt = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM PRODUCTS WHERE ID = ?");)
                        {
                            stmt.setString(1, ID);
                            try(ResultSet rs = stmt.executeQuery();)
                            {
                                if (rs.next()) {
                                    String filename = ID + "_1.jpg";
                                    String filepath = ServerMain.img_path + filename;
                                    System.out.println("Reading file: " + filename);
                                    // check if filepath exists
                                    File file = new File(filepath);
                                    if (file.exists()) {
                                        //file = file.listFiles()[0];
                                        // read all bytes of file to byte array
                                        System.out.println("File exists, sending");
                                        //byte[] file_bytes = Files.readAllBytes(file.toPath());
                                        // send file to client
                                        DOS.write(Tools.data_with_ASCII_byte(Tools.ImageToBASE64(filepath)).getBytes(StandardCharsets.US_ASCII));
                                    }
                                }
                            }
                        }
                    } catch (Exception e){
                        ServerMain.handleException(data, e.toString());
                        e.printStackTrace();
                    }
                }
                else if (instruction.equals("0001")) {
                    Thread.sleep(100);
                    data = Tools.receive_ASCII(DIS, 32);
                    System.out.println(data);
                    try (PreparedStatement cmd = ServerMain.sql.prepareStatement("select top 1 token, username from TOKENS where token=?");){
                        cmd.setString(1, data);
                        try (ResultSet rs = cmd.executeQuery()){
                            if (rs.next()){
                                // token is valid, send username to client
                                DOS.write(("0001").getBytes(StandardCharsets.UTF_16LE));
                                System.out.println("sent 0001 to client");
                                // after finishing all the initializing, put the client into the session
                                Client client = new Client();
                                client.client = this.client;
                                client.stream = DOS;
                                client.DIS = DIS;
                                client.is_locked.set(0);
                                client.token = data;
                                ServerMain.sessions.put(data, client);
                            } 
                            else {
                                String token = Send_new_token(DOS);
                                Client client = new Client();
                                client.client = this.client;
                                client.stream = DOS;
                                client.DIS = DIS;
                                client.is_locked.set(0);
                                client.token = token;
                                ServerMain.sessions.put(token, client);
                            }
                        }
                    } catch(Exception e){
                        e.printStackTrace();
                        try{
                            DIS.close();
                        } catch (Exception e1){
                        }
                        try{
                            DOS.close();
                        } catch (Exception e2){
                        }
                        try{
                            client.close();
                        } catch (Exception e3){
                        }
                    }
                } else if (instruction.equals("0002")){
                    try{
                        String token = Send_new_token(DOS);
                        Client client = new Client();
                        client.client = this.client;
                        client.stream = DOS;
                        client.DIS = DIS;
                        client.is_locked.set(0);
                        client.token = token;
                        ServerMain.sessions.put(token, client);
                    } catch (Exception e){
                        e.printStackTrace();
                    } finally {
                        // close all possible resource with try catch
                        try{
                            try{
                                DIS.close();
                            } catch (Exception e){
                            }
                            try{
                                DOS.close();
                            } catch (Exception e){
                            }
                            try{
                                client.close();
                            } catch (Exception e){
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // close all possible resouces with try catch
            try {
                DIS.close();
            } catch (Exception e1) {
            }
            try {
                DOS.close();
            } catch (Exception e1) {
            }
            try {
                client.close();
            } catch (Exception e1) {
            }
        }
        finally{
            
            System.out.flush();
        }
    }
}
