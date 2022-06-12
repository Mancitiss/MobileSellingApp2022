package org.duckdns.mancitiss.testapplication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DonHang {
    public String id;
    public String address;
    public String phone;
    public String name;
    public long total;
    public Item[] itemList;

    public DonHang(){}

    public DonHang(String id, String address, String phone, String name, long total, Item[] itemList) {
        this.id = id;
        this.address = address;
        this.phone = phone;
        this.name = name;
        this.total = total;
        this.itemList = itemList;
    }

    public String getId(){return id;}
    public void setId(String id) {this.id=id;}

    public String getName(){return name;}
    public void setName(String name) {this.name=name;}

    public String getAddress(){return address;}
    public void setAddress(String address){this.address = address;}

    public String getSdt(){return phone;}
    public void setSdt(String phone){this.phone = phone;}

    public long getTotal(){return total;}
    public void setTotal(long total){this.total = total;}

    public List<Item> getItemList() {
        return Arrays.asList(itemList);
    }
}
