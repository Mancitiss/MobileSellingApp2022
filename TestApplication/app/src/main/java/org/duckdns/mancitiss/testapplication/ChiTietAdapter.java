package org.duckdns.mancitiss.testapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChiTietAdapter extends RecyclerView.Adapter<ChiTietAdapter.MyViewHolder> {
    Context context;
    List<Item> itemList;
    private Object Glide;

    public ChiTietAdapter(Context context, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chitiet,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Item item = itemList.get(position);
        holder.txtten.setText(item.getTensp() + "");
        holder.txtsoluong.setText("Số lượng: "+item.getSoluong());
        holder.txtgiatien.setText("Giá tiền: "+item.getGia());
        holder.txttinhtrang.setText("Tình trạng: "+item.getTinhtrang());
        //Load hình ảnh.
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imagechitiet;
        TextView txtten,txtsoluong,txtgiatien,txttinhtrang;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            imagechitiet = itemView.findViewById(R.id.item_imageviewchitiet);
            txtten = itemView.findViewById(R.id.item_tensanpham);
            txtsoluong = itemView.findViewById(R.id.item_soluong);
            txtgiatien = itemView.findViewById(R.id.item_giatien);
            txttinhtrang = itemView.findViewById(R.id.item_tinhtrang);
        }
    }
}
