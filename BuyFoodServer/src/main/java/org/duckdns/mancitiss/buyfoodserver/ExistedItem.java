package org.duckdns.mancitiss.buyfoodserver;

public class ExistedItem {
    public String idsp;
    public int soluong;
    public String tinhtrang;
    public int rate;
    
    public ExistedItem() {
    }

    public ExistedItem(String id, int quantity, String status) {
        this.idsp = id;
        this.soluong = quantity;
        this.tinhtrang = status;
    }

    public ExistedItem(String id, int quantity, String status, int rate) {
        this.idsp = id;
        this.soluong = quantity;
        this.tinhtrang = status;
        this.rate = rate;
    }
}
