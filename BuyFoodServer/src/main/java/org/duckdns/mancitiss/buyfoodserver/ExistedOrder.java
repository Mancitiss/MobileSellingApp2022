package org.duckdns.mancitiss.buyfoodserver;

import java.util.List;

public class ExistedOrder {
    public String id;
    public String name;
    public String phone;
    public String address;
    public long total;
    public List<ExistedItem> itemList;

    public ExistedOrder() {
    }

    public ExistedOrder(String id, String name, String phoneNumber, String address, long total, List<ExistedItem> items) {
        this.id = id;
        this.name = name;
        this.phone = phoneNumber;
        this.address = address;
        this.total = total;
        this.itemList = items;
    }
}
