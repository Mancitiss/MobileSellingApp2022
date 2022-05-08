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
    List<DonHang> listdonghang;
    Context context;

    public DonHangAdapter(List<DonHang> listdonghang, Context context) {
        this.listdonghang = listdonghang;
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
        DonHang donHang = listdonghang.get(position);
        holder.iddonhang.setText("Đơn hàng: "+donHang.getId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                holder .chitietdonhang.getContext(),
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
        return listdonghang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView iddonhang;
        RecyclerView chitietdonhang;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iddonhang = itemView.findViewById(R.id.iddonhang);
            chitietdonhang = itemView.findViewById(R.id.recyclerviewdonhang);
        }
    }
}
