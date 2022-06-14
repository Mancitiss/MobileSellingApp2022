package org.duckdns.mancitiss.testapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ChiTietAdapter extends RecyclerView.Adapter<ChiTietAdapter.MyViewHolder> {
    Context context;
    AppCompatActivity activity;
    List<Item> itemList;
    String orderID;
    private Object Glide;

    public ChiTietAdapter(Context context, AppCompatActivity activity, String orderID, List<Item> itemList) {
        this.context = context;
        this.itemList = itemList;
        this.activity = activity;
        this.orderID = orderID;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chitiet,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int localPosition = new Integer(position);
        Item item = itemList.get(localPosition);
        holder.txtten.setText(item.getTensp() + "");
        holder.txtsoluong.setText("Số lượng: "+item.getSoluong());
        holder.txtgiatien.setText("Giá tiền: "+item.getGia());
        holder.txttinhtrang.setText("Tình trạng: "+item.getTinhtrang());
        //Load hình ảnh.
        if (item.getHinhanh() != null) {
            holder.imagechitiet.setImageBitmap(item.getHinhanh());
        }
        else {
            holder.imagechitiet.setImageResource(R.drawable.food);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // rate only when user didn't rate this item
                if (item.rate == 0) {
                    RateUsDialog rateUsDialog = new RateUsDialog(context, activity, orderID, item.idsp, item);
                    rateUsDialog.setCancelable(false);
                    rateUsDialog.show();
                }
            }
        });
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
