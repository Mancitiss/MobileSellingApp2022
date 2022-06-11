package org.duckdns.mancitiss.buyfoodserver;

public class ExistedItem {
    public String idsp;
    public int soluong;
    public String tinhtrang;
    
    public ExistedItem() {
    }

    public ExistedItem(String id, int quantity, String status) {
        this.idsp = id;
        this.soluong = quantity;
        this.tinhtrang = status;
    }
}
