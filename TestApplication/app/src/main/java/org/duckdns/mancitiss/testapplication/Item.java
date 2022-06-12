package org.duckdns.mancitiss.testapplication;

import android.graphics.Bitmap;

public class Item {
    public String idsp;
    public int soluong;
    public String tinhtrang;

    public Item(){}

    public Item(String idsp, int soluong, String tinhtrang) {
        this.idsp = idsp;
        this.soluong = soluong;
        this.tinhtrang = tinhtrang;
    }

    public String getIdsp(){ return idsp; }
    public void setIdsp(String idsp) {this.idsp=idsp;}

    public int getSoluong(){ return soluong; }
    public void setSoluong(int soluong) {this.soluong=soluong;}

    public String getTinhtrang(){return tinhtrang;}
    public void setTinhtrang(String tinhtrang){this.tinhtrang=tinhtrang;}

    public String getTensp(){return Models.getInstance().getKnownProducts().get(idsp).getName();}

    public Bitmap getHinhanh(){return Models.getInstance().getKnownProducts().get(idsp).getImage();}

    public long getGia(){return Models.getInstance().getKnownProducts().get(idsp).getPrice();}


}
