package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PaymentMainActivity3 extends AppCompatActivity {
    ImageView imageView;
    EditText e1, e2, e3, e4, e5, e6, e7, e8;
    TextView t1, t2, t3, t4, t5, btn;

    boolean clicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity_main3);

        imageView = (ImageView) findViewById(R.id.visa_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PaymentMainActivity3.this, PaymentMainActivity.class);
                startActivity(intent);
                Toast.makeText(PaymentMainActivity3.this, "You clicked to back", Toast.LENGTH_SHORT).show();
            }
        });

        e1 = (EditText) findViewById(R.id.code1);
        e2 = (EditText) findViewById(R.id.code2);
        e3 = (EditText) findViewById(R.id.code3);
        e4 = (EditText) findViewById(R.id.code4);
        e5 = (EditText) findViewById(R.id.Name);
        e6 = (EditText) findViewById(R.id.Month);
        e7 = (EditText) findViewById(R.id.Year);
        e8 = (EditText) findViewById(R.id.CCV);


        t1 = (TextView) findViewById(R.id.code);
        t2 = (TextView) findViewById(R.id.cardholder);
        t3 = (TextView) findViewById(R.id.month);
        t4 = (TextView) findViewById(R.id.year);
        t5 = (TextView) findViewById(R.id.ccv);
        btn = (TextView) findViewById(R.id.payment_visa);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clicked) return;
                clicked = true;
                Animation animation = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.push_down);
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.rl1);
                linearLayout.setAnimation(animation);


                String c1, c2, c3, c4;
                c1 = e1.getText().toString();
                c2 = e2.getText().toString();
                c3 = e3.getText().toString();
                c4 = e4.getText().toString();
                t1.setText(c1 + " \t " + c2 + " \t " + c3 + " \t " + c4);
                String code = c1 + c2 + c3 + c4;
                String name1, month1, year1, ccv1;
                name1 = e5.getText().toString();
                t2.setText(name1);
                month1 = e6.getText().toString();
                t3.setText(month1);
                year1 = e7.getText().toString();
                t4.setText(year1);
                ccv1 = e8.getText().toString();
                t5.setText(ccv1);
                String cvv = ccv1;

                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (Connection.Companion.CheckVisa(PaymentMainActivity3.this, PaymentMainActivity3.this, code, cvv)){
                            if(Connection.Companion.PlaceOrderWithoutAccount(PaymentMainActivity3.this, PaymentMainActivity3.this)){
                                Intent intent = new Intent(PaymentMainActivity3.this, MainActivity.class);
                                // clear all activities in stack
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PaymentMainActivity3.this, "Order placed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                            else {
                                // show cart activity
                                Intent intent = new Intent(PaymentMainActivity3.this, CartActivity.class);
                                // clear all activities in stack
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(PaymentMainActivity3.this, "Order failed", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                        else {
                            clicked = false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(PaymentMainActivity3.this, "Invalid card", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
                thread.start();
            }
        });
    }
}