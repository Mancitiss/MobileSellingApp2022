package org.duckdns.mancitiss.buyfoodserver;

public class Product {
    public String name;
    public java.math.BigDecimal price;
    public String category;
    public String id;
    public int quantity;
    public String description;
    public long created;
    public int stars_1;
    public int stars_2;
    public int stars_3;
    public int stars_4;
    public int stars_5;

    public Product(){
    }

    // initialize
    public Product(String id, String name, java.math.BigDecimal price, String category, int quantity, String description, long created, int stars_1, int stars_2, int stars_3, int stars_4, int stars_5) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.description = description;
        this.created = created;
        this.stars_1 = stars_1;
        this.stars_2 = stars_2;
        this.stars_3 = stars_3;
        this.stars_4 = stars_4;
        this.stars_5 = stars_5;
    }

}
