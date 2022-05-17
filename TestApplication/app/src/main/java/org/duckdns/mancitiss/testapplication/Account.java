package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

        SharedPreferences pref = getPreferences(Context.MODE_PRIVATE);
        String name = pref.getString("name", "");
        ((TextView)findViewById(R.id.name)).setText(name);
        ((TextView)findViewById(R.id.name_Account)).setText(name);
        ((TextView)findViewById(R.id.phone_Account)).setText(pref.getString("phone", ""));
        ((TextView)findViewById(R.id.email_Account)).setText(pref.getString("email", ""));


        ImageView backButton = (ImageView) findViewById(R.id.backAccount);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Account.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

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