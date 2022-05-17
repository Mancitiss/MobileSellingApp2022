package org.duckdns.mancitiss.testapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
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
                    Toast.makeText(Account2.this, "Mật khẩu thay đổi thành công", Toast.LENGTH_SHORT).show();
                    Intent intent2 = new Intent(Account2.this, Account.class);
                    intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent2);
                }
                else {
                    if (opw.isEmpty())
                        Toast.makeText(Account2.this, "Vui lòng nhập mật khẩu hiện tại", Toast.LENGTH_SHORT).show();
                    else if (npw.isEmpty())
                        Toast.makeText(Account2.this, "Vui lòng nhập mật khẩu mới", Toast.LENGTH_SHORT).show();
                    else if (rnpw.isEmpty())
                        Toast.makeText(Account2.this, "Vui lòng xác nhận mật khẩu mới", Toast.LENGTH_SHORT).show();
                    else if (!npw.equals(rnpw))
                        Toast.makeText(Account2.this, "Mật khẩu xác nhận không khớp với mật khẩu mới", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(Account2.this, "Mật khẩu hiện tại không đúng", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}