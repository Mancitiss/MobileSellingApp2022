package org.duckdns.mancitiss.buyfoodserver;

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
                                try(PreparedStatement ps = ServerMain.sql.prepareStatement("SELECT * FROM (select ROW_NUMBER() OVER (ORDER BY ID DESC) AS rownumber, * FROM PRODUCT) AS foo WHERE rownumber >= ? and rownumber < ?")){
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
                                    ServerMain.sessions.get(token).Queue_command(Tools.combine("0003".getBytes(StandardCharsets.UTF_16LE), json.getBytes(StandardCharsets.UTF_16LE)));
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
