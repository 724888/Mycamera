package com.example.jiexingxing.mycamera;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jiexingxing.mycamera.camera.scan.CameraActivity;


public class MainActivity extends AppCompatActivity {
    private TextView opentv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        opentv = (TextView) findViewById(R.id.opentv);
        opentv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                CheckCameraPermission();


            }
        });

    }

    private void CheckCameraPermission() {//检查相机权限


       if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//


            if (PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)) {
//
                String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};//申请写入权限
                ActivityCompat.requestPermissions(this, perms, 302);
            } else {
                String[] perms = {"android.permission.CAMERA"};//申请相机权限
                ActivityCompat.requestPermissions(this, perms, 301);
            }


        } else {


            Intent intent = new Intent(this, CameraActivity.class);
            startActivity(intent);
        }



    }


    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 301:
                boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted) {
                    String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};//写入权限
                    ActivityCompat.requestPermissions(this, perms, 302);
                } else {

                    Toast.makeText(this,"请在手机的“设置-应用-好司机日记-权限”选项中，允许好司机日记访问您的相机",Toast.LENGTH_SHORT).show();
                }

                break;
            case 302:
                boolean cameraAccepted1 = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (cameraAccepted1) {
                    Intent intent = new Intent(this, CameraActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(this,"请在手机的“设置-应用-好司机日记-权限”选项中，允许好司机日记访问您的存储",Toast.LENGTH_SHORT).show();
                }

                break;

        }
    }



}
