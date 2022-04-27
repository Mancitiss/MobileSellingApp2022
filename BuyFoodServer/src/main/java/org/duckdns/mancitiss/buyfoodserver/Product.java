package org.duckdns.mancitiss.buyfoodserver;

public class Product {
    public String name;
    public String short_description;
    public java.math.BigDecimal price;
    public String category;
    public String id;
    public int quantity;
    public String description;

    public Product(){
    }

    public Product(String id, String name, String short_description, String category, java.math.BigDecimal price, int quantity){
        this.id = id;
        this.name = name;
        this.short_description = short_description;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
    }

}
