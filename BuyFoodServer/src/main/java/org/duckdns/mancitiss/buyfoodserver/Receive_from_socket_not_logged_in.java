package org.duckdns.mancitiss.buyfoodserver;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

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

    private String Send_new_token(DataOutputStream DOS, String oldToken, boolean clear) throws Exception{
        // create new token that is not in database and send it to client
        // this method guarantees that the created token is new and not in database
        // this method will throw an exception if the old token is not in the database (which will cause an infinite loop if we ignore the exception)
        // if oldToken == null then insert the created token
        // if oldToken != null then update the old token with the newly created one and clear the old token's information if clear is true
        // return the created token
        String new_token = generateNewToken();
        while (true) {
            try (PreparedStatement stmt = ServerMain.sql.prepareStatement("SELECT * FROM TOKENS WHERE token = ?"))
            {
                stmt.setString(1, new_token);
                try(ResultSet rs = stmt.executeQuery();)
                {
                    if (!rs.next()) {
                        if (oldToken == null){
                            try(PreparedStatement stmt2 = ServerMain.sql.prepareStatement("INSERT INTO TOKENS (token, username, expirationDate) VALUES (?, NULL, ?)");)
                            {
                                stmt2.setString(1, new_token);
                                stmt2.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis() + ServerMain.token_expiration_time));
                                stmt2.executeUpdate();
                                DOS.write(Tools.combine("0404".getBytes(StandardCharsets.UTF_16LE), new_token.getBytes(StandardCharsets.US_ASCII)));
                                DOS.flush();
                                System.out.println("Sent 0404 to client: "+ new_token + " " + new_token.length());
                                break;
                            }
                        }
                        else if (clear){
                            try(PreparedStatement stmt2 = ServerMain.sql.prepareStatement("UPDATE TOKENS SET token = ?, username = NULL, expirationDate = ? WHERE token = ?");)
                            {
                                stmt2.setString(1, new_token);
                                stmt2.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis() + ServerMain.token_expiration_time));
                                stmt2.setString(3, oldToken);
                                stmt2.executeUpdate();
                                DOS.write(Tools.combine("0004".getBytes(StandardCharsets.UTF_16LE), new_token.getBytes(StandardCharsets.US_ASCII)));
                                DOS.flush();
                                System.out.println("Sent 0004 to client: "+ new_token + " " + new_token.length());
                                break;
                            }
                        }
                        else {
                            try(PreparedStatement stmt2 = ServerMain.sql.prepareStatement("UPDATE TOKENS SET token = ?, expirationDate = ? WHERE token = ?");)
                            {
                                stmt2.setString(1, new_token);
                                stmt2.setTimestamp(2, new java.sql.Timestamp(System.currentTimeMillis() + ServerMain.token_expiration_time));
                                stmt2.setString(3, oldToken);
                                stmt2.executeUpdate();
                                DOS.write(Tools.combine("0002".getBytes(StandardCharsets.UTF_16LE), new_token.getBytes(StandardCharsets.US_ASCII)));
                                DOS.flush();
                                System.out.println("Sent 0002 to client: "+ new_token + " " + new_token.length());
                                break;
                            }
                        }
                    }
                }
            } 
            new_token = generateNewToken();
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
                switch(instruction){
                    // get product list from number Nth
                    // when user request product list, we send them m number of products
                    // m is defined in ServerMain.loadCount
                    // we send them in order of date of creation (newest first)
                    case "0003":{ 
                        String indexStr = Tools.receive_ASCII_Automatically(DIS);
                        int index = Integer.parseInt(indexStr);
                        try{
                            // initialize a list of products
                            List<Product> products = new ArrayList<>();
                            // read 10 products from database order by ID desc except the first 5 products using window function
                            try(PreparedStatement ps = ServerMain.sql.prepareStatement("SELECT * FROM (select ROW_NUMBER() OVER (ORDER BY created DESC) AS rownumber, * FROM PRODUCTS) AS foo WHERE rownumber >= ? and rownumber < ?")){
                                ps.setInt(1, index);
                                ps.setInt(2, index + ServerMain.loadCount);
                                try(ResultSet rs = ps.executeQuery();){
                                    while (rs.next()){
                                        products.add(new Product(rs.getString("ID"), rs.getString("name"), rs.getLong("price"), rs.getString("category"), rs.getInt("count"), rs.getString("description"), rs.getTimestamp("created").getTime(), rs.getLong("stars"), rs.getLong("ratingCount")));
                                    }
                                }
                            }
                            if (!products.isEmpty()){
                                // create a string that serializes the list of products
                                // using gson
                                String json = ServerMain.gson.toJson(products);
                                // send the string to client
                                DOS.write(Tools.combine("0003".getBytes(StandardCharsets.UTF_16LE), Tools.data_with_unicode_byte(json).getBytes(StandardCharsets.UTF_16LE)));
                            }
                        }
                        catch (Exception e){
                            ServerMain.handleException( e.toString());
                            e.printStackTrace();
                        }
                    }
                    break;

                    // send all products without images
                    case "0005":{ 
                        String indexStr = Tools.receive_ASCII_Automatically(DIS);
                        //int index = Integer.parseInt(indexStr);
                        try{
                            // initialize a list of products
                            List<Product> products = new ArrayList<>();
                            // read 10 products from database order by ID desc except the first 5 products using window function
                            try(PreparedStatement ps = ServerMain.sql.prepareStatement("SELECT * FROM PRODUCTS")){
                                try(ResultSet rs = ps.executeQuery();){
                                    while (rs.next()){
                                        String ID = rs.getString("ID");
                                        String filename = ID + "_1.jpg";
                                        //String filepath = ServerMain.img_path + filename;
                                        System.out.println("Reading file: " + filename);
                                        //File file = new File(filepath);

                                        // new file handling method
                                        String avtString = "";
                                        /*
                                        if (file.exists()){
                                            System.out.println("File exists, sending");
                                            avtString = Tools.ImageToBASE64(file.getAbsolutePath());
                                        }
                                        */
                                        products.add(new Product(rs.getString("ID"), rs.getString("name"), rs.getLong("price"), rs.getString("category"), rs.getInt("count"), rs.getString("description"), rs.getTimestamp("created").getTime(), rs.getLong("stars"), rs.getLong("ratingCount"), avtString));
                                    }
                                }
                            }
                            if (!products.isEmpty()){
                                // create a string that serializes the list of products
                                // using gson
                                String json = ServerMain.gson.toJson(products);
                                //System.out.println(json);
                                // send the string to client
                                DOS.write(Tools.combine("5555".getBytes(StandardCharsets.UTF_16LE), Tools.data_with_unicode_byte(json).getBytes(StandardCharsets.UTF_16LE)));
                            }
                        }
                        catch (Exception e){
                            ServerMain.handleException( e.toString());
                            e.printStackTrace();
                        }
                    }
                    break;

                    // the same as 0005 but get all products
                    case "5555":{
                        String indexStr = Tools.receive_ASCII_Automatically(DIS);
                        int index = Integer.parseInt(indexStr);
                        try{
                            // initialize a list of products
                            List<Product> products = new ArrayList<>();
                            // read 10 products from database order by ID desc except the first 5 products using window function
                            try(PreparedStatement ps = ServerMain.sql.prepareStatement("SELECT * FROM PRODUCTS ORDER BY created DESC")){
                                ps.setInt(1, index);
                                ps.setInt(2, index + ServerMain.loadCount);
                                try(ResultSet rs = ps.executeQuery();){
                                    while (rs.next()){
                                        String ID = rs.getString("ID");
                                        String filename = ID + "_1.jpg";
                                        //String filepath = ServerMain.img_path + filename;
                                        System.out.println("Reading file: " + filename);
                                        //File file = new File(filepath);

                                        // new file handling method
                                        
                                        String avtString = "";
                                        /*
                                        if (file.exists()){
                                            System.out.println("File exists, sending");
                                            avtString = Tools.ImageToBASE64(file.getAbsolutePath());
                                        }
                                        */
                                        products.add(new Product(rs.getString("ID"), rs.getString("name"), rs.getLong("price"), rs.getString("category"), rs.getInt("count"), rs.getString("description"), rs.getTimestamp("created").getTime(), rs.getLong("stars"), rs.getLong("ratingCount"), avtString));
                                    }
                                }
                            }
                            if (!products.isEmpty()){
                                // create a string that serializes the list of products
                                // using gson
                                String json = ServerMain.gson.toJson(products);
                                // send the string to client
                                DOS.write(Tools.combine("0003".getBytes(StandardCharsets.UTF_16LE), Tools.data_with_unicode_byte(json).getBytes(StandardCharsets.UTF_16LE)));
                            }
                        }
                        catch (Exception e){
                            ServerMain.handleException( e.toString());
                            e.printStackTrace();
                        }
                    } break;

                    // get product information by ID
                    case "0004":{ 
                        String ID = Tools.receive_ASCII_Automatically(DIS);
                        try{
                            // initialize a product
                            Product product = null;
                            // read product from database
                            try(PreparedStatement ps = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM PRODUCTS WHERE ID = ?")){
                                ps.setString(1, ID);
                                try(ResultSet rs = ps.executeQuery();){
                                    if (rs.next()){
                                        product = new Product(rs.getString("ID"), rs.getString("name"), rs.getLong("price"), rs.getString("category"), rs.getInt("count"), rs.getString("description"), rs.getTimestamp("created").getTime(), rs.getLong("stars"), rs.getLong("ratingCount"));
                                    }
                                }
                            }
                            if (product != null){
                                // create a string that serializes the product
                                // using gson
                                String json = ServerMain.gson.toJson(product);
                                // send the string to client
                                DOS.write(Tools.combine("0004".getBytes(StandardCharsets.UTF_16LE), Tools.data_with_unicode_byte(json).getBytes(StandardCharsets.UTF_16LE)));
                            }
                        }
                        catch (Exception e){
                            ServerMain.handleException( e.toString());
                            e.printStackTrace();
                        }

                    }
                    break;

                    // claim products
                    case "2610":{
                        try{
                            String token = Tools.receive_ASCII(DIS, 32);
                            if (Tools.isTokenRegistered(token)){ 
                                Boolean success = true;
                                String json = Tools.receive_Unicode_Automatically(DIS);
                                Order newOrder = ServerMain.gson.fromJson(json, Order.class);
                                // insert the order into database
                                try(PreparedStatement ps = ServerMain.sql.prepareStatement("INSERT INTO ORDERS (ID, username, length, total, status, address, receiver, contactNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")){
                                    // create new random long and convert it to string with "OR" at the beginning
                                    String newIdnum = ""+ServerMain.rand.nextLong();
                                    while (newIdnum.length() < 19) newIdnum = "0" + newIdnum;
                                    String newId = "OR" + newIdnum;
                                    // check if newID exists in database
                                    while (true){
                                        try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM ORDERS WHERE ID = ?")){
                                            ps2.setString(1, newId);
                                            try(ResultSet rs = ps2.executeQuery();){
                                                if (!rs.next()){
                                                    break;
                                                }
                                            }
                                        }
                                        newIdnum = ""+ServerMain.rand.nextLong();
                                        while (newIdnum.length() < 19) newIdnum = "0" + newIdnum;
                                        newId = "OR" + newIdnum;
                                    }
                                    final String newID = newId;
                                    ps.setString(1, newID);
                                    boolean anonymous = true;
                                    String username = "";
                                    // check if username exists in table TOKENS at column username
                                    try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM TOKENS WHERE token = ?")){
                                        ps2.setString(1, token);
                                        try(ResultSet rs = ps2.executeQuery();){
                                            if (rs.next()){
                                                if (rs.getNString("username") != null){
                                                    ps.setNString(2, rs.getNString("username"));
                                                    anonymous = false;
                                                    username = rs.getNString("username");
                                                }
                                                else{
                                                    ps.setNull(2, java.sql.Types.NVARCHAR);
                                                }
                                            }
                                            else{
                                                ps.setNull(2, java.sql.Types.NVARCHAR);
                                            }
                                        }
                                    }
                                    ps.setInt(3, newOrder.items.size());
                                    long total = 0;
                                    int index = 0;
                                    List<String> productIDs = new ArrayList<>();
                                    for (String key : newOrder.items.keySet()){
                                        productIDs.add(key);
                                        index += 1;
                                        // get price of the product from database
                                        try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM PRODUCTS WHERE ID = ? AND count >= ?")){
                                            ps2.setString(1, key);
                                            ps2.setInt(2, newOrder.items.get(key));
                                            try(ResultSet rs = ps2.executeQuery();){
                                                if (rs.next()){
                                                    // update the count of the product
                                                    Boolean update = false;
                                                    try(PreparedStatement ps3 = ServerMain.sql.prepareStatement("UPDATE PRODUCTS SET count = ? WHERE ID = ?")){
                                                        ps3.setInt(1, rs.getInt("count") - newOrder.items.get(key));
                                                        ps3.setString(2, key);
                                                        if (ps3.executeUpdate() > 0){
                                                            update = true;
                                                        }
                                                    }
                                                    if (update){
                                                        Long subtotal = rs.getLong("price")*newOrder.items.get(key);
                                                        total += subtotal;
                                                        // insert order detail into ORDER_DETAILS table
                                                        try(PreparedStatement ps3 = ServerMain.sql.prepareStatement("INSERT INTO ORDER_DETAILS (orderID, [index], productID, count, subTotal, status) VALUES (?, ?, ?, ?, ?, ?)")){
                                                            ps3.setString(1, newID);
                                                            ps3.setInt(2, index);
                                                            ps3.setString(3, key);
                                                            ps3.setInt(4, newOrder.items.get(key));
                                                            ps3.setLong(5, subtotal);
                                                            ps3.setString(6, "Pending");
                                                            ps3.executeUpdate();
                                                        }
                                                    }
                                                    
                                                }
                                                else {
                                                    success = false;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (!success){
                                        // rollback the order
                                        try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("DELETE TOP (?) FROM ORDER_DETAILS WHERE orderID = ?")){
                                            ps2.setInt(1, productIDs.size());
                                            ps2.setString(2, newID);
                                            ps2.executeUpdate();
                                        }
                                        for(String key : productIDs){
                                            try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("UPDATE TOP (1) PRODUCTS SET count = count + ? WHERE ID = ?")){
                                                ps2.setInt(1, newOrder.items.get(key));
                                                ps2.setString(2, key);
                                                ps2.executeUpdate();
                                            }
                                        }
                                        // send error message to client
                                        DOS.write("2612".getBytes(StandardCharsets.UTF_16LE));
                                        throw new Exception("Failed to create order");
                                    }
                                    ps.setLong(4, total);
                                    ps.setString(5, "Pending");
                                    ps.setNString(6, newOrder.address);
                                    ps.setNString(7, newOrder.name);
                                    ps.setNString(8, newOrder.phoneNumber);
                                    try{
                                        if(anonymous == false){
                                            // check if this account has a default address
                                            // if not set the default address to the new address
                                            try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM ACCOUNTS WHERE username = ?")){
                                                ps2.setString(1, username);
                                                try(ResultSet rs = ps2.executeQuery();){
                                                    if (rs.next()){
                                                        if (rs.getNString("address") == null){
                                                            try(PreparedStatement ps3 = ServerMain.sql.prepareStatement("UPDATE ACCOUNTS SET address = ? WHERE username = ?")){
                                                                ps3.setString(1, newOrder.address);
                                                                ps3.setString(2, username);
                                                                ps3.executeUpdate();
                                                            }
                                                        }
                                                        if (rs.getNString("name") == null){
                                                            try(PreparedStatement ps3 = ServerMain.sql.prepareStatement("UPDATE ACCOUNTS SET name = ? WHERE username = ?")){
                                                                ps3.setString(1, newOrder.name);
                                                                ps3.setString(2, username);
                                                                ps3.executeUpdate();
                                                            }
                                                        }
                                                        if (rs.getNString("phoneNumber") == null){
                                                            try(PreparedStatement ps3 = ServerMain.sql.prepareStatement("UPDATE ACCOUNTS SET phoneNumber = ? WHERE username = ?")){
                                                                ps3.setString(1, newOrder.phoneNumber);
                                                                ps3.setString(2, username);
                                                                ps3.executeUpdate();
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    catch(Exception e){
                                        e.printStackTrace();
                                    }
                                    if (ps.executeUpdate() > 0){
                                        Timer timer = new Timer();
                                        TimerTask timerTask = new TimerTask(){
                                            @Override
                                            public void run(){
                                                try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("UPDATE ORDER_DETAILS SET status = ? WHERE orderID = ?")){
                                                    ps2.setString(1, "Delivered");
                                                    ps2.setString(2, newID);
                                                    ps2.executeUpdate();
                                                }
                                                catch(Exception e){
                                                    e.printStackTrace();
                                                }
                                                try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("UPDATE ORDERS SET status = ? WHERE ID = ?")){
                                                    ps2.setString(1, "Delivered");
                                                    ps2.setString(2, newID);
                                                    ps2.executeUpdate();
                                                }
                                                catch(Exception e){
                                                    e.printStackTrace();
                                                }
                                            }
                                        };
                                        timer.schedule(timerTask, 1000*60*5);
                                        DOS.write(Tools.combine("2611".getBytes(StandardCharsets.UTF_16LE), (newID).getBytes(StandardCharsets.US_ASCII)));
                                        
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            else{
                                System.out.println("session doesn't exist");
                            }
                        }
                        catch (Exception e){
                            ServerMain.handleException( e.toString());
                            e.printStackTrace();
                        }
                    }
                    break;

                    // payment
                    case "2611":{
                        String orderID = Tools.receive_ASCII(DIS, 22);
                        try(PreparedStatement ps = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM ORDERS WHERE ID = ?")){
                            ps.setString(1, orderID);
                            try(ResultSet rs = ps.executeQuery();){
                                if (rs.next()){
                                    if (rs.getString("status").equals("Pending")){
                                        try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("UPDATE ORDERS SET status = ? WHERE ID = ?")){
                                            ps2.setString(1, "Paid");
                                            ps2.setString(2, orderID);
                                            if (ps2.executeUpdate() > 0){
                                                DOS.write("2611".getBytes(StandardCharsets.UTF_16LE));
                                            }
                                        }
                                    }
                                    else{
                                        DOS.write("2612".getBytes(StandardCharsets.UTF_16LE));
                                    }
                                }
                                else{
                                    DOS.write("2612".getBytes(StandardCharsets.UTF_16LE));
                                }
                            }
                        }
                        catch (Exception e){
                            ServerMain.handleException( e.toString());
                            e.printStackTrace();
                        }
                    }
                    break;

                    // cancel order BEFORE delivery
                    case "2612":{
                        try{
                            String token = Tools.receive_ASCII(DIS, 32);
                            String orderID = Tools.receive_ASCII_Automatically(DIS);
                            // find username associated with token
                            try(PreparedStatement ps = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM TOKENS WHERE token = ?")){
                                ps.setString(1, token);
                                try(ResultSet rs = ps.executeQuery();){
                                    if (rs.next()){
                                        String username = rs.getNString("username");
                                        // check if orderID exists in database
                                        try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM ORDERS WHERE ID = ?")){
                                            ps2.setString(1, orderID);
                                            try(ResultSet rs2 = ps2.executeQuery();){
                                                if (rs2.next()){
                                                    if (username.equals(rs2.getNString("username")) || rs2.getNString("username") == null){
                                                        // update order status to Cancelled
                                                        try(PreparedStatement ps3 = ServerMain.sql.prepareStatement("UPDATE ORDERS SET status = ? WHERE ID = ?")){
                                                            ps3.setString(1, "Cancelled");
                                                            ps3.setString(2, orderID);
                                                            if (ps3.executeUpdate() > 0){
                                                                DOS.write("2613".getBytes(StandardCharsets.UTF_16LE));
                                                            }
                                                        }
                                                    }
                                                }
                                                else{
                                                    // send error message to client
                                                    DOS.write("2612".getBytes(StandardCharsets.UTF_16LE));
                                                    throw new Exception("Failed to cancel order");
                                                }
                                            }
                                        }
                                    }
                                    else{
                                        // send error message to client
                                        DOS.write("2612".getBytes(StandardCharsets.UTF_16LE));
                                        throw new Exception("Failed to cancel order");
                                    }
                                }
                            }

                            // order canceled by user
                            int length = 0;
                            try(PreparedStatement ps = ServerMain.sql.prepareStatement("SELECT length FROM ORDERS WHERE orderID = ?")){
                                ps.setString(1, orderID);
                                try(ResultSet rs = ps.executeQuery();){
                                    if (rs.next()){
                                        length = rs.getInt("length");
                                    }
                                }
                            }
                            try(PreparedStatement ps = ServerMain.sql.prepareStatement("DELETE FROM ORDERS WHERE ID = ?")){
                                ps.setString(1, orderID);
                                ps.executeUpdate();
                            }
                            try(PreparedStatement ps = ServerMain.sql.prepareStatement("DELETE TOP (?) FROM ORDER_DETAILS WHERE orderID = ?")){
                                ps.setInt(1, length);
                                ps.setString(2, orderID);
                                ps.executeUpdate();
                            }
                        }
                        catch (Exception e){
                            ServerMain.handleException( e.toString());
                            e.printStackTrace();
                        }
                    }
                    break;

                    // send product's first image
                    case "1412":{
                        String ID = Tools.receive_ASCII_Automatically(DIS);
                        try{
                            // check if ID is in database table PRODUCT
                            try(PreparedStatement stmt = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM PRODUCTS WHERE ID = ?");)
                            {
                                stmt.setString(1, ID);
                                try(ResultSet rs = stmt.executeQuery();)
                                {
                                    if (rs.next()) {
                                        //String filename = ID + "_1.jpg";
                                        String filename = ID + "_1.jpg";
                                        String filepath = ServerMain.img_path + filename;
                                        System.out.println("Reading file: " + filename);
                                        File file = new File(filepath);

                                        // new file handling method
                                        if (file.exists()){
                                            System.out.println("File exists, sending");
                                            DOS.write(Tools.data_with_ASCII_byte(Tools.ImageToBASE64(file.getAbsolutePath())).getBytes(StandardCharsets.US_ASCII));
                                            System.out.println("File Sent");
                                        }
                                        // old file handling method
                                        /*
                                        if (file.exists()) {
                                            System.out.println("File exists, sending");
                                            DOS.write(Tools.data_with_ASCII_byte(Tools.ImageToBASE64(filepath)).getBytes(StandardCharsets.US_ASCII));
                                        }*/
                                    }
                                }
                            }
                        } catch (Exception e){
                            try{
                                DOS.write(Tools.data_with_ASCII_byte(" ").getBytes(StandardCharsets.US_ASCII));
                            }
                            catch(Exception ex){}
                            ServerMain.handleException(e.toString());
                            e.printStackTrace();
                        }
                    }
                    break;
                    // client has token and wants to confirm token is valid
                    case "0001": {
                        data = Tools.receive_ASCII(DIS, 32);
                        System.out.println(data);
                        try (PreparedStatement cmd = ServerMain.sql.prepareStatement("select top 1 * from TOKENS where token=?");){
                            cmd.setString(1, data);
                            try (ResultSet rs = cmd.executeQuery()){
                                if (rs.next()){
                                    long timestamp = rs.getTimestamp("expirationDate").getTime();
                                    long current = System.currentTimeMillis();
                                    if (timestamp < current){
                                        // token expired, clear old token data and send new token to client
                                        Send_new_token(DOS, data, true);
                                    }
                                    else{
                                        // token is valid, send new token to client, no clear
                                        Send_new_token(DOS, data, false);
                                    }
                                } 
                                else {
                                    // old token doesn't exist, send new token to client 
                                    Send_new_token(DOS, null, false);
                                }
                            }
                        } catch(Exception e){
                            ServerMain.handleException( e.toString());
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
                    } 
                    break;

                    // client doesn't have token and wants a new one
                    case "0002":{
                        try{
                            // oldtoken null = insert new token
                            Send_new_token(DOS, null, false);
                        } catch (Exception e){
                            ServerMain.handleException( e.toString());
                            e.printStackTrace();
                        }
                    }
                    break;

                    // client wants to log in
                    case "0010":{
                        String username = Tools.receive_Unicode_Automatically(DIS);
                        String password = Tools.receive_Unicode_Automatically(DIS);
                        try{
                            try(PreparedStatement cmd = ServerMain.sql.prepareStatement("select top 1 * from ACCOUNTS where username=? and pw=?");){
                                cmd.setString(1, username);
                                cmd.setString(2, password);
                                try(ResultSet rs = cmd.executeQuery();){
                                    if (rs.next()){
                                        // deactivate all tokens of this user
                                        try(PreparedStatement ps = ServerMain.sql.prepareStatement("DELETE FROM TOKENS WHERE username = ?")){
                                            ps.setString(1, username);
                                            ps.executeUpdate();
                                        }
                                        catch(Exception e){
                                            ServerMain.handleException(e.toString());
                                            e.printStackTrace();
                                        }
                                        // user exists, send new token to client
                                        String token = Send_new_token(DOS, null, false);
                                        // update token with username
                                        try(PreparedStatement ps = ServerMain.sql.prepareStatement("UPDATE TOKENS SET username = ? WHERE token = ?")){
                                            ps.setString(1, username);
                                            ps.setString(2, token);
                                            ps.executeUpdate();
                                        }
                                        catch(Exception e){
                                            ServerMain.handleException( e.toString());
                                            e.printStackTrace();
                                        }
                                        // send user infomaion to client
                                        String[] user_info = new String[4];
                                        user_info[0] = rs.getString("name");
                                        user_info[1] = rs.getString("email");
                                        user_info[2] = rs.getString("phonenumber");
                                        user_info[3] = rs.getString("address");
                                        DOS.write(Tools.combine("0200".getBytes(StandardCharsets.UTF_16LE), Tools.data_with_unicode_byte(ServerMain.gson.toJson(user_info)).getBytes(StandardCharsets.UTF_16LE)));
                                        System.out.println("Info sent to client");
                                    }
                                    else{
                                        // user doesn't exist, send error message to client
                                        DOS.write("-200".getBytes(StandardCharsets.UTF_16LE));
                                    }
                                }
                            }
                        } catch(Exception e){
                            ServerMain.handleException( e.toString());
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
                    }
                    break;

                    // client wants to sign up
                    case "0011":{
                        String json = Tools.receive_Unicode_Automatically(DIS);
                        SignUpInfo user = ServerMain.gson.fromJson(json, SignUpInfo.class);
                        if (user.username != null && user.email != null && user.pw != null){
                            // add user to database
                            try(PreparedStatement cmd = ServerMain.sql.prepareStatement("insert into ACCOUNTS (username, pw, email, phonenumber) values (?, ?, ?, ?)");){
                                cmd.setString(1, user.username);
                                cmd.setString(2, user.pw);
                                cmd.setString(3, user.email);
                                if (user.phone != null){
                                    cmd.setString(4, user.phone);
                                }
                                else cmd.setNull(4, Types.NVARCHAR);
                                try{
                                    if(cmd.executeUpdate() > 0){
                                        DOS.write("0011".getBytes(StandardCharsets.UTF_16LE));
                                    }
                                    else {
                                        DOS.write("0111".getBytes(StandardCharsets.UTF_16LE));
                                    }
                                }
                                catch(Exception ex){
                                    DOS.write("0111".getBytes(StandardCharsets.UTF_16LE));
                                }
                            } catch(Exception e){
                                ServerMain.handleException( e.toString());
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
                        }
                    }
                    break;

                    // change password
                    case "0013":{
                        String opw = Tools.receive_Unicode_Automatically(DIS);
                        String npw = Tools.receive_Unicode_Automatically(DIS);
                        String username = Tools.receive_ASCII_Automatically(DIS);
                        try{
                            try(PreparedStatement cmd = ServerMain.sql.prepareStatement("select top 1 * from ACCOUNTS where username=? and pw=?");){
                                cmd.setString(1, username);
                                cmd.setString(2, opw);
                                try(ResultSet rs = cmd.executeQuery();){
                                    if (rs.next()){
                                        // user exists, update password
                                        try(PreparedStatement ps = ServerMain.sql.prepareStatement("UPDATE ACCOUNTS SET pw = ? WHERE username = ?")){
                                            ps.setString(1, npw);
                                            ps.setString(2, username);
                                            ps.executeUpdate();
                                        }
                                        // send success message to client
                                        DOS.write("0013".getBytes(StandardCharsets.UTF_16LE));
                                    }
                                    else{
                                        // user doesn't exist, send error message to client
                                        DOS.write("0014".getBytes(StandardCharsets.UTF_16LE));
                                    }
                                }
                            }
                        } catch(Exception e){
                            ServerMain.handleException( e.toString());
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

                    }
                    break;

                    // change user information
                    case "0014":{
                        String token = Tools.receive_ASCII_Automatically(DIS);
                        String username = Tools.receive_Unicode_Automatically(DIS);
                        String json = Tools.receive_Unicode_Automatically(DIS);
                        UserInfo info = ServerMain.gson.fromJson(json, UserInfo.class);
                        // check if token and username is valid
                        try{
                            try(PreparedStatement cmd = ServerMain.sql.prepareStatement("select top 1 * from TOKENS where token=? and username=?");){
                                cmd.setString(1, token);
                                cmd.setString(2, username);
                                try(ResultSet rs = cmd.executeQuery();){
                                    if (rs.next()){
                                        // token and username is valid, update user information
                                        try(PreparedStatement ps = ServerMain.sql.prepareStatement("UPDATE ACCOUNTS SET name = ?, phonenumber = ?, email = ? WHERE username = ?")){
                                            ps.setString(1, info.name);
                                            ps.setString(2, info.phone);
                                            ps.setString(3, info.email);
                                            ps.setString(4, username);
                                            ps.executeUpdate();
                                        }
                                        // send success message to client
                                        DOS.write("0014".getBytes(StandardCharsets.UTF_16LE));
                                    }
                                    else{
                                        // token and username is invalid, send error message to client
                                        DOS.write("0114".getBytes(StandardCharsets.UTF_16LE));
                                    }
                                }
                            }
                        } catch(Exception e){
                            ServerMain.handleException( e.toString());
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
                    }
                    break;



                    default:
                    break;
                }
            }
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
        finally{
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
            System.out.flush();
        }
    }
}
