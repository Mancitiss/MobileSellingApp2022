package org.duckdns.mancitiss.buyfoodserver;

public class Product {
    public String name;
    public long price;
    public String category;
    public String id;
    public int quantity;
    public String description;
    public long created;
    public long stars;
    public long ratingCount;

    public String avatar;

    public Product(){
    }

    // initialize
    public Product(String id, String name, long price, String category, int quantity, String description, long created, long stars, long ratingCount) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.description = description;
        this.created = created;
        this.stars = stars;
        this.ratingCount = ratingCount;
    }

    public Product(String id, String name, long price, String category, int quantity, String description, long created, long stars, long ratingCount, String avatar) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.category = category;
        this.quantity = quantity;
        this.description = description;
        this.created = created;
        this.avatar = avatar;
        this.stars = stars;
        this.ratingCount = ratingCount;
    }

}
