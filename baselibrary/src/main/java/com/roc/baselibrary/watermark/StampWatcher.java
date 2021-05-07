package com.roc.baselibrary.watermark;

import android.graphics.Bitmap;

/**
 * Created by roc on 4/12/21 3:25 PM.
 * Desc:This is.. Stamper support draw iamge and text
 */

public abstract class StampWatcher {


    protected void onSuccess(Bitmap bitmap, int requestId) {


    }

    protected void onError(String error, int requestId) {
    }
}
