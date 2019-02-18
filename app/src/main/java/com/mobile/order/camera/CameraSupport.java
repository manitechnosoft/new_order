package com.mobile.order.camera;

import android.app.Activity;
import android.content.Context;

public interface CameraSupport {
    CameraSupport open(int cameraId, Context ctx, Activity activity);
    int getOrientation(int cameraId);
}
