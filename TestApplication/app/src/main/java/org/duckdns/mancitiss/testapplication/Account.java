package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Account extends AppCompatActivity {

    TextView textView;
    TextView textView2;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = getSharedPreferences("preferences", Context.MODE_PRIVATE);
        String name = pref.getString("name", "");
        String email = pref.getString("email", "");
        Log.d("connecting", pref.getString("token", "null token"));
        Log.d("connecting", "email: " + email);
        ((TextView) findViewById(R.id.name)).setText(name);
        ((TextView) findViewById(R.id.name_Account)).setText(name);
        ((TextView) findViewById(R.id.phone_Account)).setText(pref.getString("phone", ""));
        ((TextView) findViewById(R.id.email_Account)).setText(email);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account);

        TextView logoutButton = (TextView) findViewById(R.id.logoutAccount);
        logoutButton.setOnClickListener(
                new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        (new Thread(){public void run(){
                            Connection.Companion.Logout(Account.this, Account.this);
                        }}).start();
                        Intent intent = new Intent(Account.this, Login.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }
        );

        ImageView backButton = (ImageView) findViewById(R.id.backAccount);
        backButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(Account.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
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