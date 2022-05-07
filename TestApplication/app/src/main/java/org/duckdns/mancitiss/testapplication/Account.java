package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Account extends AppCompatActivity {

    TextView textView;
    TextView textView2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        textView2 = (TextView) findViewById(R.id.txt_change);
        textView = (TextView) findViewById(R.id.txt_View1);

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Account.this,Account3.class);
                startActivity(intent);
                Toast.makeText(Account.this, "you clicked to edit", Toast.LENGTH_SHORT).show();

            }
        });

        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Account.this,Account2.class);
                startActivity(intent2);
                Toast.makeText(Account.this, "you clicked to change password", Toast.LENGTH_SHORT).show();
            }
        });

        //((TextView)findViewById(R.id.name)).setText(SessionInfo.name);

    }
}