package com.mobile.order.activity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import com.mobile.order.R;
import com.mobile.order.camera.CameraModule;
import com.mobile.order.camera.CameraSupport;

public class OnOffCameraFlashlight extends BaseActivity {

    ToggleButton toggleButton;
    Camera camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.turn_on_off_camera_flash_light);

        toggleButton = (ToggleButton) findViewById(R.id.onOffFlashlight);

        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (ContextCompat.checkSelfPermission(OnOffCameraFlashlight.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    //ask for authorisation
                    ActivityCompat.requestPermissions(OnOffCameraFlashlight.this, new String[]{Manifest.permission.CAMERA}, 50);


                CameraModule module =new CameraModule();
                CameraSupport cameraSupport = module.provideCameraSupport(getApplicationContext());
                CameraManager manager = (CameraManager) getApplicationContext().getSystemService(Context.CAMERA_SERVICE);
                CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                String[] cameraIds=null;
                try {
                cameraIds = manager.getCameraIdList();
                } catch (Exception e) {
                    // TODO handle
                }
               // cameraSupport.open(0,getApplicationContext(), OnOffCameraFlashlight.this);
                if (checked) {


                    try {
                        String cameraId = cameraManager.getCameraIdList()[0];
                        cameraManager.setTorchMode(cameraId, true);
                    } catch (Exception e) {
                        // TODO handle
                        e.printStackTrace();
                    }
                    //ToDo something
                   /* camera = Camera.open();
                    Camera.Parameters parameters = camera.getParameters();
                    parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                    camera.setParameters(parameters);
                    camera.startPreview();*/

                } else {

                   /* camera = Camera.open();
                    //Camera.Parameters parameters = camera.getParameters();
                    //parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                    camera.setParameters(parameters);
                    camera.stopPreview();
                    camera.release();*/
                    try {
                    String cameraId =cameraIds[0];
                    cameraManager.setTorchMode(cameraId, false);
                    } catch (Exception e) {
                        // TODO handle
                        e.printStackTrace();
                    }
                   /* if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)) {
                        camera.stopPreview();
                        camera.release();
                        camera = null;
                    }*/

                }
            }
        });

    }
}