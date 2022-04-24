package org.duckdns.mancitiss.buyfoodserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Base64;

import javax.net.ssl.SSLSocket;

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

    private void Send_new_token(DataOutputStream DOS) {
        // create new token that is not in database
        String new_token = generateNewToken();
        while (true) {
            try {
                PreparedStatement stmt = ServerMain.sql.prepareStatement("SELECT * FROM tokens WHERE token = ?");
                stmt.setString(1, new_token);
                ResultSet rs = stmt.executeQuery();
                if (!rs.next()) {
                    break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            new_token = generateNewToken();
        }
        try {
            DOS.write(Tools.combine("0002".getBytes(StandardCharsets.UTF_16LE), new_token.getBytes(StandardCharsets.US_ASCII)));
            DOS.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            if (data != null && !data.isEmpty()) {
                String instruction = data;
                
                if (instruction.equals("0012")) {
                    Thread.sleep(100);
                    data = Tools.receive_ASCII(DIS, 32);
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
                else if (instruction.equals("0001")) {
                    Thread.sleep(100);
                    data = Tools.receive_ASCII(DIS, 32);
                    try (PreparedStatement cmd = ServerMain.sql.prepareStatement("select top 1 token, username from account where token=?");){
                        cmd.setNString(1, data);
                        try (ResultSet rs = cmd.executeQuery()){
                            if (rs.next()){
                                // token is valid, send username to client
                                DOS.write(("0001").getBytes(StandardCharsets.UTF_16LE));
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
                                Send_new_token(DOS);
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
                        Send_new_token(DOS);
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