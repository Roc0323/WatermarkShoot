package com.roc.baselibrary.listener;

import android.graphics.Bitmap;

/**
 * Create by roc
 * 2019-10-17
 */
public interface JCameraListener {

    void captureSuccess(Bitmap bitmap);

    void recordSuccess(String url, Bitmap firstFrame);

}
