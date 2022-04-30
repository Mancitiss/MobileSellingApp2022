package org.duckdns.mancitiss.buyfoodserver;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class Receive_message implements Runnable {
    String token;

    public Receive_message(String data) {
        this.token = data;
    }

    @Override
    public void run() {
        try {
            java.io.PushbackInputStream s = new java.io.PushbackInputStream(ServerMain.sessions.get(token).DIS);
            String data;
            boolean keepreading = true;
            do {
                data = Tools.receive_unicode(s, 8);

                if (data != null && !data.isBlank()) {
                    String instruction = data;
                    System.out.println("Received instruction: " + instruction);
                    switch (instruction){
                        case "0003":{
                            String indexStr = Tools.receive_ASCII_Automatically(s);
                            int index = Integer.parseInt(indexStr);
                            try{
                                // initialize a list of products
                                List<Product> products = new ArrayList<>();
                                // read 10 products from database order by ID desc except the first 5 products using window function
                                try(PreparedStatement ps = ServerMain.sql.prepareStatement("SELECT * FROM (select ROW_NUMBER() OVER (ORDER BY ID DESC) AS rownumber, * FROM PRODUCTS) AS foo WHERE rownumber >= ? and rownumber < ?")){
                                    ps.setInt(1, index);
                                    ps.setInt(2, index + ServerMain.loadCount);
                                    try(ResultSet rs = ps.executeQuery();){
                                        while (rs.next()){
                                            products.add(new Product(rs.getString("ID"), rs.getString("name"), rs.getString("short_description"), rs.getString("category"), rs.getBigDecimal("price"), rs.getInt("count")));
                                        }
                                    }
                                }
                                if (!products.isEmpty()){
                                    // create a string that serializes the list of products
                                    // using gson
                                    String json = ServerMain.gson.toJson(products);
                                    // send the string to client
                                    ServerMain.sessions.get(token).Queue_command(Tools.combine("0003".getBytes(StandardCharsets.UTF_16LE), Tools.data_with_unicode_byte(json).getBytes(StandardCharsets.UTF_16LE)));
                                }
                            }
                            catch (Exception e){
                                ServerMain.handleException(token, e.toString());
                                e.printStackTrace();
                            }
                        }
                        break;
                        case "2610":{
                            try{
                                Boolean success = true;
                                String json = Tools.receive_Unicode_Automatically(s);
                                Order newOrder = ServerMain.gson.fromJson(json, Order.class);
                                // insert the order into database
                                while (ServerMain.isSqlWriting.getAndSet(1) == 1){
                                    Thread.sleep(150);
                                }
                                try(PreparedStatement ps = ServerMain.sql.prepareStatement("INSERT INTO ORDERS (ID, username, length, total, status, address, token, receiver, contactNumber) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")){
                                    // create new random long and convert it to string with "OR" at the beginning
                                    String newID = "OR" + ServerMain.rand.nextLong();
                                    // check if newID exists in database
                                    while (true){
                                        try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM ORDERS WHERE ID = ?")){
                                            ps2.setString(1, newID);
                                            try(ResultSet rs = ps2.executeQuery();){
                                                if (!rs.next()){
                                                    break;
                                                }
                                            }
                                        }
                                        newID = "OR" + ServerMain.rand.nextLong();
                                    }
                                    ps.setString(1, newID);
                                    Boolean anonymous = false;
                                    // check if username exists in table TOKENS at column username
                                    try(PreparedStatement ps2 = ServerMain.sql.prepareStatement("SELECT TOP 1 * FROM TOKENS WHERE token = ?")){
                                        ps2.setString(1, token);
                                        try(ResultSet rs = ps2.executeQuery();){
                                            if (rs.next()){
                                                ps.setString(2, rs.getNString("username"));
                                            }
                                            else{
                                                ps.setNull(2, java.sql.Types.NVARCHAR);
                                                anonymous = true;
                                            }
                                        }
                                    }
                                    ps.setInt(3, newOrder.items.size());
                                    BigDecimal total = new BigDecimal(0);
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
                                                        BigDecimal subtotal = rs.getBigDecimal("price").multiply(new BigDecimal(newOrder.items.get(key)));
                                                        total = total.add(subtotal);
                                                        // insert order detail into ORDER_DETAILS table
                                                        try(PreparedStatement ps3 = ServerMain.sql.prepareStatement("INSERT INTO ORDER_DETAILS (orderID, [index], productID, count, subTotal, status) VALUES (?, ?, ?, ?, ?, ?)")){
                                                            ps3.setString(1, newID);
                                                            ps3.setInt(2, index);
                                                            ps3.setString(3, key);
                                                            ps3.setInt(4, newOrder.items.get(key));
                                                            ps3.setBigDecimal(5, subtotal);
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
                                        ServerMain.sessions.get(token).Queue_command("2612".getBytes(StandardCharsets.UTF_16LE));
                                        throw new Exception("Failed to create order");
                                    }
                                    ps.setBigDecimal(4, total);
                                    ps.setString(5, "Pending");
                                    ps.setNString(6, newOrder.address);
                                    if (anonymous = false){
                                        ps.setNull(7, java.sql.Types.NVARCHAR);
                                    }
                                    else{
                                        ps.setString(7, token);
                                    }
                                    ps.setNString(8, newOrder.name);
                                    ps.setNString(9, newOrder.phoneNumber);
                                    if (ps.executeUpdate() > 0){
                                        ServerMain.sessions.get(token).Queue_command(Tools.combine("2611".getBytes(StandardCharsets.UTF_16LE), Tools.data_with_ASCII_byte(newID).getBytes(StandardCharsets.US_ASCII), Tools.data_with_ASCII_byte(total.toString()).getBytes(StandardCharsets.US_ASCII)));
                                    }
                                }
                                catch (Exception e){
                                    e.printStackTrace();
                                }
                                finally{
                                    ServerMain.isSqlWriting.set(0);
                                }
                            }
                            catch (Exception e){
                                ServerMain.handleException(token, e.toString());
                                e.printStackTrace();
                            }
                        }
                        break;
                        case "2612":{
                            try{
                                String orderID = Tools.receive_ASCII_Automatically(s);
                                // order canceled by user
                                ServerMain.isSqlWriting.set(1);
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
                                finally{
                                    ServerMain.isSqlWriting.set(0);
                                }
                            }
                            catch (Exception e){
                                ServerMain.handleException(token, e.toString());
                                e.printStackTrace();
                            }
                        }
                        break;
                        default:{
                            System.out.println("Unknown instruction: " + instruction);
                            break;
                        }
                    }
                } else {
                    keepreading = false;
                    ServerMain.shutdown(token);
                }
                int b = s.read();
                if (b == -1)
                    keepreading = false;
                else
                    s.unread(b);
            } while (keepreading);
        } catch (Exception e) {
            e.printStackTrace();

            try {
                ServerMain.handleException(this.token, e.getMessage());
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } finally {
            try {

                if (ServerMain.sessions.containsKey(this.token)) {
                    ServerMain.sessions.get(this.token).is_locked.set(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            //System.out.flush();
        }
    }
}
