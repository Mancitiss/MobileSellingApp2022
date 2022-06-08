package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class CartActivity extends AppCompatActivity {
    TextView giohangtrong, tongtien;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button btnmuahang;
    Button btnmuahangBack;
    GioHangAdapter adapter;
    List<GioHang> gioHangList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);
        initView();
        initControl();
    }

    private void initControl() {
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        if (Models.getInstance().getShoppingCart().size() == 0){
            giohangtrong.setVisibility(View.VISIBLE);
            btnmuahang.setVisibility(View.GONE);
        }
        else {
            gioHangList = new ArrayList<GioHang>();
            ConcurrentHashMap<String, Integer> card = Models.getInstance().getShoppingCart();
            for(String key : card.keySet()){
                Integer value = card.get(key);
                if (value > 0){
                    GioHang gioHang = new GioHang(key, value);
                    gioHangList.add(gioHang);
                }
            }
            adapter = new GioHangAdapter(this, this, gioHangList);
            recyclerView.setAdapter(adapter);
        }

        btnmuahangBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnmuahang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreference = getSharedPreferences("preferences", MODE_PRIVATE);
                setContentView(R.layout.giohang2);
                EditText name = findViewById(R.id.nameEditText);
                EditText phone = findViewById(R.id.phoneEditText);
                EditText address = findViewById(R.id.addressEditText);
                Button choosePayment = findViewById(R.id.choosePaymentButton);
                Button back = findViewById(R.id.infoBackButton);
                AppCompatActivity activity = CartActivity.this;
                Context context = CartActivity.this;
                (new Thread(() -> {
                    Connection.Companion.isLoggedIn(activity, context);
                    runOnUiThread(() -> {
                        name.setText(sharedPreference.getString("name", ""));
                        phone.setText(sharedPreference.getString("phone", ""));
                        address.setText(sharedPreference.getString("address", ""));
                        choosePayment.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // check if name or phone or address is empty
                                if (name.getText().toString().isEmpty() || phone.getText().toString().isEmpty() || address.getText().toString().isEmpty()){
                                    name.setError("Không được để trống");
                                    phone.setError("Không được để trống");
                                    address.setError("Không được để trống");
                                }
                                else {
                                    Models.getInstance().currentname = name.getText().toString();
                                    Models.getInstance().currentphone = phone.getText().toString();
                                    Models.getInstance().currentaddress = address.getText().toString();
                                    Intent intent = new Intent(CartActivity.this, PaymentMainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
                        back.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                setContentView(R.layout.cart);
                                initView();
                                initControl();
                            }
                        });
                    });
                })).run();
            }
        });
    }

    private void initView(){
        giohangtrong = findViewById(R.id.txtgiohang);
        tongtien = findViewById(R.id.txttongtien);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerviewgiohang);
        btnmuahang = findViewById(R.id.buttonmuahang);
        btnmuahangBack = findViewById(R.id.buttonmuahangback);
    }
}