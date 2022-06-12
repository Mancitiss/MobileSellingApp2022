package org.duckdns.mancitiss.testapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;

public class GioHangAdapter extends RecyclerView.Adapter<GioHangAdapter.MyViewHolder>{
    Context context;
    CartActivity cartActivity;
    List<GioHang> gioHangList;
    long totalPrice = 0;

    public GioHangAdapter(CartActivity activity, Context context, List<GioHang> gioHangList) {
        this.cartActivity = activity;
        this.context = context;
        this.gioHangList = gioHangList;

        for (GioHang gioHang : gioHangList) {
            totalPrice += gioHang.giasp * gioHang.soluong;
        }
        cartActivity.tongtien.setText(new DecimalFormat("#.###").format(totalPrice) + " đ");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_giohang, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        GioHang gioHang = gioHangList.get(position);
        holder.item_giohang_tensp.setText(gioHang.getTensp());
        holder.item_giohang_soluong.setText(gioHang.getSoluong()+"");
        DecimalFormat decimalFormat = new DecimalFormat("#.###");
        holder.item_giohang_gia.setText("Giá: "+decimalFormat.format((gioHang.getGiasp()))+"Đ");
        long gia = gioHang.getGiasp() * gioHang.getSoluong();
        holder.item_giohang_giasp2.setText(decimalFormat.format(gia));
        //Load hinh anh.
        //holder.item_giohang_image.setImageBitmap(gioHang.getHinhsp());

        Bitmap hinhsp = gioHang.getHinhsp();
        if (hinhsp != null) {
            holder.item_giohang_image.setImageBitmap(hinhsp);
        }
        else {
            holder.item_giohang_image.setImageResource(R.drawable.food);
        }

        holder.item_giohang_xoa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    GioHang gioHang = gioHangList.get(position);
                    Models.getInstance().getShoppingCart().remove(gioHang.idsp);
                    gioHangList.remove(position);
                    notifyItemRemoved(position);
                    totalPrice -= gioHang.giasp * gioHang.soluong;
                }
            }
        });

        holder.item_giohang_tru.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long soluong = Long.parseLong(holder.item_giohang_soluong.getText().toString());
                if(soluong > 1){
                    soluong--;
                    holder.item_giohang_soluong.setText(soluong.toString());
                    holder.item_giohang_giasp2.setText((soluong*gioHang.giasp) + " đ");
                    totalPrice -= gioHang.giasp;
                    cartActivity.tongtien.setText(new DecimalFormat("#.###").format(totalPrice) + " đ");
                }
            }
        });

        holder.item_giohang_cong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long soluong = Long.parseLong(holder.item_giohang_soluong.getText().toString());
                soluong++;
                holder.item_giohang_soluong.setText(soluong.toString());
                holder.item_giohang_giasp2.setText((soluong*gioHang.giasp) + " đ");
                totalPrice += gioHang.giasp;
                cartActivity.tongtien.setText(new DecimalFormat("#.###").format(totalPrice) + " đ");
            }
        });
    }

    @Override
    public int getItemCount() {
        return gioHangList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView item_giohang_image, item_giohang_tru, item_giohang_cong, item_giohang_xoa;
        TextView item_giohang_tensp,item_giohang_gia,item_giohang_soluong,item_giohang_giasp2;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            item_giohang_image = itemView.findViewById(R.id.item_giohang_image);
            item_giohang_tensp = itemView.findViewById(R.id.item_giohang_tensp);
            item_giohang_gia = itemView.findViewById(R.id.item_giohang_gia);
            item_giohang_soluong = itemView.findViewById(R.id.item_giohang_soluong);
            item_giohang_giasp2 = itemView.findViewById(R.id.item_giohang_giasp2);
            item_giohang_tru = itemView.findViewById(R.id.item_giohang_tru); // done
            item_giohang_cong = itemView.findViewById(R.id.item_giohang_cong); // done
            item_giohang_xoa = itemView.findViewById(R.id.item_giohang_icon_remove); // done
        }
    }
}
