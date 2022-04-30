package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Account2 extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account2);
        imageView = (ImageView) findViewById(R.id.back_change);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(Account2.this, MainActivity.class);
                startActivity(intent);
                Toast.makeText(Account2.this, "you clicked to back", Toast.LENGTH_SHORT).show();

            }
        });

        textView = (TextView) findViewById(R.id.btn_OK);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Account2.this, MainActivity.class);
                startActivity(intent2);
                Toast.makeText(Account2.this, "you clicked OK button", Toast.LENGTH_SHORT).show();
            }
        });

    }
}