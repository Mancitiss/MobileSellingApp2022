package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentMainActivity2 extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity_main2);
        imageView = (ImageView) findViewById(R.id.cash_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PaymentMainActivity2.this, PaymentMainActivity.class);
                startActivity(intent);
                Toast.makeText(PaymentMainActivity2.this, "you clicked to back", Toast.LENGTH_SHORT).show();

            }
        });

        textView = (TextView) findViewById(R.id.payment_btn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentMainActivity2.this, PaymentMainActivity4.class);
                startActivity(intent);
                Toast.makeText(PaymentMainActivity2.this, "you clicked to back", Toast.LENGTH_SHORT).show();

            }
        });


    }
}