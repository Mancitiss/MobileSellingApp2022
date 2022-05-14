package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

public class PaymentMainActivity4 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_activity_main4);

        //show rating dialog
        RateUsDialog rateUsDialog = new RateUsDialog(PaymentMainActivity4.this);
        rateUsDialog.setCancelable(false);
        rateUsDialog.show();
    }
}