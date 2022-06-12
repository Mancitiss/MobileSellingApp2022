package org.duckdns.mancitiss.testapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DonHangAdapter extends RecyclerView.Adapter<DonHangAdapter.MyViewHolder> {
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();
    List<DonHang> listdonhang;
    Context context;
    AppCompatActivity activity;

    public DonHangAdapter(List<DonHang> listdonhang, Context context, AppCompatActivity activity) {
        this.listdonhang = listdonhang;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_donhang, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        int localPosition = new Integer(position);
        DonHang donHang = listdonhang.get(localPosition);
        holder.iddonhang.setText("Đơn hàng: "+donHang.getId());
        LinearLayoutManager layoutManager = new LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
        );
        layoutManager.setInitialPrefetchItemCount(donHang.getItemList().size());
        // adapter chitet
        ChiTietAdapter chiTietAdapter = new ChiTietAdapter(context, activity, donHang.id, donHang.getItemList());
        holder.chitietdonhang.setLayoutManager(layoutManager);
        holder.chitietdonhang.setAdapter(chiTietAdapter);
        holder.chitietdonhang.setRecycledViewPool(viewPool);

        String status = donHang.getItemList().get(0).getTinhtrang();
        String displayStatus = "";
        if (status.equals("Pending")) {
            displayStatus = "Đang giao hàng";
        } else if (status.equals("Delivered")) {
            displayStatus = "Đã giao hàng";
        } else if (status.equals("Cancelled")) {
            displayStatus = "Đã hủy";
        }
        holder.status.setText("Tình trạng: "+ displayStatus);

        holder.cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (Connection.Companion.cancelOrderBeforeDelivery(activity, context, donHang.getId())) {
                            for (Item i : donHang.getItemList()){
                                i.setTinhtrang("Cancelled");
                            }
                        }
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                notifyItemChanged(localPosition);
                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return listdonhang.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView iddonhang, status;
        RecyclerView chitietdonhang;
        ImageView cancel;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            iddonhang = (TextView) itemView.findViewById(R.id.iddonhang);
            status = (TextView) itemView.findViewById(R.id.statusdonhang);
            cancel = (ImageView) itemView.findViewById(R.id.item_notification_icon_remove);
            chitietdonhang = (RecyclerView) itemView.findViewById(R.id.chitietdonhang);
        }
    }
}
