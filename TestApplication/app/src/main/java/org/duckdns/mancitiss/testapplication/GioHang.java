package org.duckdns.mancitiss.testapplication;

import android.graphics.Bitmap;

public class GioHang {
    String idsp;
    String tensp;
    long giasp;
    Bitmap hinhsp;
    int soluong;
    //long gia;

    public GioHang(String idsp, String tensp, long giasp, Bitmap hinhsp, int soluong) {
        this.idsp = idsp;
        this.tensp = tensp;
        this.giasp = giasp;
        this.hinhsp = hinhsp;
        this.soluong = soluong;
        //this.gia = gia;
    }

    public GioHang(){}

    public GioHang(String idsp, int soLuong){
        this.idsp = idsp;
        this.soluong = soLuong;
        this.tensp = Models.getInstance().getKnownProducts().get(idsp).getName();
        this.giasp = Models.getInstance().getKnownProducts().get(idsp).getPrice();
        this.hinhsp = Models.getInstance().getKnownProducts().get(idsp).getImage();
    }

    public String getIdsp(){return idsp;}
    public void setIdsp(String idsp) {this.idsp=idsp;}

    public String getTensp(){return tensp;}
    public void setTensp(String tensp) {this.tensp=tensp;}

    public long getGiasp(){return giasp;}
    public void setGiasp(long giasp) {this.giasp=giasp;}

    public Bitmap getHinhsp(){return hinhsp;}
    public void setHinhsp(Bitmap hinhsp) {this.hinhsp=hinhsp;}

    public int getSoluong(){return soluong;}
    public void setSoluong(int soluong) {this.soluong=soluong;}

    public long getGia(){return giasp;}
    public void setGia(long gia) {this.giasp=gia;}

}
