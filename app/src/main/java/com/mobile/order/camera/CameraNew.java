package com.mobile.order.camera;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.mobile.order.activity.OnOffCameraFlashlight;

public class CameraNew implements CameraSupport {

    private CameraDevice camera;
    private CameraManager manager;

    public CameraNew(final Context context) {
        this.manager = (CameraManager) context.getSystemService(Context.CAMERA_SERVICE);
    }

    @Override
    public CameraSupport open(final int cameraId, Context ctx, Activity activity) {
        try {
            String[] cameraIds = manager.getCameraIdList();
            if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
                //ask for authorisation
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, 50);

        }
            manager.openCamera(cameraIds[cameraId], new CameraDevice.StateCallback() {
                public void onOpened(CameraDevice camera) {
                    CameraNew.this.camera = camera;
                }

                @Override
                public void onDisconnected(CameraDevice camera) {
                    CameraNew.this.camera = camera;
                    // TODO handle
                }

                @Override
                public void onError(CameraDevice camera, int error) {
                    CameraNew.this.camera = camera;
                    // TODO handle
                }
            }, null);
        } catch (Exception e) {
            // TODO handle
        }
        return this;
    }

    @Override
    public int getOrientation(final int cameraId) {
        try {
            String[] cameraIds = manager.getCameraIdList();
            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraIds[cameraId]);
            return characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
        } catch (CameraAccessException e) {
            // TODO handle
            return 0;
        }
    }
}
