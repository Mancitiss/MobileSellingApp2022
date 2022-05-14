package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentMainActivity extends AppCompatActivity {
    TextView textView;
    TextView textView1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity_main);

        textView = (TextView) findViewById(R.id.cash_btn);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(PaymentMainActivity.this, PaymentMainActivity2.class);
                startActivity(intent);
                Toast.makeText(PaymentMainActivity.this, "you clicked to cash payment", Toast.LENGTH_SHORT).show();

            }
        });

        textView1 = (TextView) findViewById(R.id.visa_payment);
        textView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentMainActivity.this, PaymentMainActivity3.class);
                startActivity(intent);
                Toast.makeText(PaymentMainActivity.this, "you clicked to visa payment", Toast.LENGTH_SHORT).show();
            }
        });




    }
}