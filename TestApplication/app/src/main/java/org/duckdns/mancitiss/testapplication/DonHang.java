package org.duckdns.mancitiss.testapplication;

import java.util.List;

public class DonHang {
    int id;
    int iduser;
    String diachi;
    String sdt;
    String tongtien;
    List<Item> itemList;
    public int getId(){return id;}
    public void setId(int id) {this.id=id;}

    public int getIduser(){return iduser;}
    public void setIduser(int iduser) {this.iduser=iduser;}

    public String getDiachi(){return diachi;}
    public void setDiachi(String diachi){this.diachi = diachi;}

    public String getSdt(){return sdt;}
    public void setSdt(String sdt){this.sdt = sdt;}

    public String getTongtien(){return tongtien;}
    public void setTongtien(String tongtien){this.sdt = tongtien;}

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
