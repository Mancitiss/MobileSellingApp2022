package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toolbar;

public class CartActivity extends AppCompatActivity {
    TextView giohangtrong, tongtien;
    Toolbar toolbar;
    RecyclerView recyclerView;
    Button btnmuahang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cart);
        initView();
    }

    private void initView(){
        giohangtrong = findViewById(R.id.txtgiohang);
        tongtien = findViewById(R.id.txttongtien);
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerviewgiohang);
        btnmuahang = findViewById(R.id.buttonmuahang);
    }
}