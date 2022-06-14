package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReceiptActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Button back;
    Toolbar toolbar;
    DonHangAdapter adapter;
    List<DonHang> donHangList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.receipt);
        initView();
        initControl();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerviewdonhang);
        back = findViewById(R.id.notificationBackButton);
        toolbar = findViewById(R.id.toolbar2);
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        donHangList = new ArrayList<DonHang>();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                if (Connection.Companion.isLoggedIn(ReceiptActivity.this, ReceiptActivity.this)){
                    Connection.Companion.getKnownOrders(ReceiptActivity.this, ReceiptActivity.this);
                    for(String key : Models.getInstance().getKnownOrders().keySet()){
                        DonHang donHang = Models.getInstance().getKnownOrders().get(key);
                        donHangList.add(donHang);
                    }
                    // quick sort donHangList by mDate
                    Collections.sort(donHangList, new Comparator<DonHang>() {
                        @Override
                        public int compare(DonHang o1, DonHang o2) {
                            // compare 2 mDate (long)
                            if (o2.mDate - o1.mDate > 0){
                                return 1;
                            }
                            else if (o2.mDate - o1.mDate < 0){
                                return -1;
                            }
                            else {
                                return 0;
                            }
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new DonHangAdapter(donHangList, ReceiptActivity.this, ReceiptActivity.this);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
                else {
                    Connection.Companion.getCachedOrders(ReceiptActivity.this, ReceiptActivity.this);
                    for(String key : Models.getInstance().getKnownOrders().keySet()){
                        DonHang donHang = Models.getInstance().getKnownOrders().get(key);
                        donHangList.add(donHang);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter = new DonHangAdapter(donHangList, ReceiptActivity.this, ReceiptActivity.this);
                            recyclerView.setAdapter(adapter);
                        }
                    });
                }
            }
        });
        thread.start();
    }
}