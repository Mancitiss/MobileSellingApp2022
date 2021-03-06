package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class Account2 extends AppCompatActivity {

    ImageView imageView;
    TextView textView;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account2);
        imageView = (ImageView) findViewById(R.id.back_change);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent intent = new Intent(Account2.this, Account.class);
                startActivity(intent);
                Toast.makeText(Account2.this, "you clicked to back", Toast.LENGTH_SHORT).show();
                */
                finish();
            }

        });

        textView = (TextView) findViewById(R.id.btn_OK);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                Intent intent2 = new Intent(Account2.this, MainActivity.class);
                startActivity(intent2);
                Toast.makeText(Account2.this, "you clicked OK button", Toast.LENGTH_SHORT).show();
                */
                String opw = ((EditText)findViewById(R.id.etx_pass)).getText().toString();
                String npw = ((EditText)findViewById(R.id.etx_repass)).getText().toString();
                String rnpw = ((EditText)findViewById(R.id.etx_confirm)).getText().toString();
                if (!opw.isEmpty() && !npw.isEmpty() && !rnpw.isEmpty() && npw.equals(rnpw) && Connection.Companion.ChangePassword(Account2.this, Account2.this, opw, npw)){
                    Toast.makeText(Account2.this, "M???t kh???u thay ?????i th??nh c??ng", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(Account2.this, Account.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                }
                else {
                    if (opw.isEmpty())
                        Toast.makeText(Account2.this, "Vui l??ng nh???p m???t kh???u hi???n t???i", Toast.LENGTH_SHORT).show();
                    else if (npw.isEmpty())
                        Toast.makeText(Account2.this, "Vui l??ng nh???p m???t kh???u m???i", Toast.LENGTH_SHORT).show();
                    else if (rnpw.isEmpty())
                        Toast.makeText(Account2.this, "Vui l??ng x??c nh???n m???t kh???u m???i", Toast.LENGTH_SHORT).show();
                    else if (!npw.equals(rnpw))
                        Toast.makeText(Account2.this, "M???t kh???u x??c nh???n kh??ng kh???p v???i m???t kh???u m???i", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(Account2.this, "M???t kh???u hi???n t???i kh??ng ????ng", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}