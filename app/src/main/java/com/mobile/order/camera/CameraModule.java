package com.mobile.order.camera;

import android.app.Activity;
import android.content.Context;
import android.os.Build;

public class CameraModule {

    public CameraSupport provideCameraSupport(Context ctx){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return new CameraNew(ctx);
        } /*else {
            return new CameraOld();
        }*/
        return null;
    }
}