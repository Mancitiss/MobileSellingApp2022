package org.duckdns.mancitiss.testapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    List<DonHang> listdonhang;
    Context context;

    public DonHangAdapter(List<DonHang> listdonhang, Context context) {
        this.listdonhang = listdonhang;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DonHang donHang = listdonhang.get(position);
        holder.iddonhang.setText("Đơn hàng: "+donHang.getId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(donHang.getItemList().size());
        // adapter chitet
        ChiTietAdapter chiTietAdapter = new ChiTietAdapter(context,donHang.getItemList());
        holder.chitietdonhang.setLayoutManager(layoutManager);
        holder.chitietdonhang.setAdapter(chiTietAdapter);
        holder.chitietdonhang.setRecycledViewPool(viewPool);
    }

    @Override
    public int getItemCount() {
        return listdonhang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView iddonhang;
        RecyclerView chitietdonhang;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iddonhang = (TextView) itemView.findViewById(R.id.iddonhang);
            chitietdonhang = (RecyclerView) itemView.findViewById(R.id.chitietdonhang);
        }
    }
}
