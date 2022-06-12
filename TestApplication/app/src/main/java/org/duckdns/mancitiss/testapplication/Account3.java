package org.duckdns.mancitiss.testapplication;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.duckdns.mancitiss.testapplication.entities.UserInfo;

//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;


public class Account3 extends AppCompatActivity  {

    ImageView imageView;
    TextView textViewUpdate;

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
        setContentView(R.layout.account3);

        imageView = (ImageView) findViewById(R.id.back_profile);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent3 = new Intent(Account3.this, MainActivity.class);
                startActivity(intent3);
                //Toast.makeText(Account3.this, "quay lại", Toast.LENGTH_SHORT).show();
            }
        });
        textViewUpdate = (TextView) findViewById(R.id.btn_Update);
        textViewUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                (new Thread(){
                    public void run(){
                        /*
                        Intent intent2 = new Intent(Account3.this, MainActivity.class);
                        startActivity(intent2);
                        Toast.makeText(Account3.this, "you clicked to Update", Toast.LENGTH_SHORT).show();
                         */
                        UserInfo userInfo = new UserInfo(((TextView) findViewById(R.id.name_Account)).getText().toString(), ((TextView) findViewById(R.id.phone_Account)).getText().toString(), ((TextView) findViewById(R.id.email_Account)).getText().toString());
                        if (Connection.Companion.ChangeInfo(Account3.this, Account3.this, userInfo)) {
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run() {
                                    Intent intent = new Intent(Account3.this, Account.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    Toast.makeText(Account3.this, "Lưu thông tin thành công", Toast.LENGTH_LONG).show();
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable(){
                                @Override
                                public void run(){
                                    Toast.makeText(Account3.this, "Lưu thông tin không thành công", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }).start();
            }
        });

        /*
        cameraPermission = new String[]{Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagePermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};
        pick = (ImageView) findViewById(R.id.avatar_change);
        pick.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                int picd = 0;
                if(picd == 0)
                {
                    if(!checkCameraPermission())
                    {
                        requestCameraPermission();
                    }
                    else
                    {
                        pickFromGallery();
                    }
                }
                else
                {
                    if(picd == 1)
                    {
                        if(!checkStoragePermission())
                        {
                            requestStoragePermission();
                        }
                        else
                        {
                            pickFromGallery();
                        }
                    }
                }
            }
        });

         */


    }

    public void saveInfo(View view) {
        //SessionInfo.name = ((EditText)findViewById(R.id.newName)).getText().toString();
    }

    /*
    private void pickFromGallery() {
        CropImage.Activity();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST);
    }

    private boolean checkCameraPermission() {
        boolean result = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == (PackageManager.PERMISSION_GRANTED);
        boolean result1 = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == (PackageManager.PERMISSION_GRANTED);
        return result && result1;
    }


    public ImageView pick;
    public static final int CAMERA_REQUEST=100;
    public static final int STORAGE_REQUEST=101;
    String cameraPermission[];
    String storagePermission[];
    */


}