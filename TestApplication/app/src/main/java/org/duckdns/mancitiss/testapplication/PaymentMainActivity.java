package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentMainActivity extends AppCompatActivity {
    TextView textView;
    TextView textView1;
    ImageView imageViewBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity_main);

        imageViewBack = (ImageView) findViewById(R.id.paymentMainBack);
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Toast.makeText(PaymentMainActivity.this, "you clicked to back", Toast.LENGTH_SHORT).show();
            }
        });


        textView = (TextView) findViewById(R.id.cash_btn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Thread thread = new Thread(new Runnable(){
                    @Override
                    public void run() {
                        if (Connection.Companion.isLoggedIn(PaymentMainActivity.this, PaymentMainActivity.this)) {
                            //Toast.makeText(PaymentMainActivity.this, "Đã chọn thanh toán bằng tiền mặt", Toast.LENGTH_SHORT).show();
                            if (Connection.Companion.PlaceOrderWithAccount(PaymentMainActivity.this, PaymentMainActivity.this)) {
                                //Toast.makeText(PaymentMainActivity.this, "Đặt hàng thành công", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PaymentMainActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                //Toast.makeText(PaymentMainActivity.this, "Đặt hàng thất bại", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(PaymentMainActivity.this, CartActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            //Toast.makeText(PaymentMainActivity.this, "Bạn cần phải đăng nhập", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                thread.start();
            }
        });

        textView1 = (TextView) findViewById(R.id.visa_payment);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentMainActivity.this, PaymentMainActivity3.class);
                startActivity(intent);
                Toast.makeText(PaymentMainActivity.this, "Đã chọn thanh toán online qua VISA", Toast.LENGTH_SHORT).show();
            }
        });




    }
}