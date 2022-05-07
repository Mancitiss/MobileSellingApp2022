package org.duckdns.mancitiss.testapplication;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

//import com.theartofdev.edmodo.cropper.CropImage;
//import com.theartofdev.edmodo.cropper.CropImageView;


public class Account3 extends AppCompatActivity  {

    ImageView imageView;
    TextView textView;

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
                Toast.makeText(Account3.this, "you clicked to back", Toast.LENGTH_SHORT).show();
            }
        });

        textView = (TextView) findViewById(R.id.btn_Update);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(Account3.this, MainActivity.class);
                startActivity(intent2);
                Toast.makeText(Account3.this, "you clicked to Update", Toast.LENGTH_SHORT).show();
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