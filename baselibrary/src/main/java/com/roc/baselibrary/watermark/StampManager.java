package com.roc.baselibrary.watermark;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

/**
 * Created by roc on 4/12/21 3:25 PM.
 * Desc:This is.. Stamper support draw iamge and text
 */
public class StampManager {

    private Context mContext;
    private StampWatcher mStampWatcher;

    public StampManager(Context context, StampWatcher watcher) {
        mContext = context;
        mStampWatcher = watcher;
    }

    /**
     * draw image
     *
     * @param masterBitmap
     * @param watermark
     * @param padding
     */
    public void stampImage(Bitmap masterBitmap, Bitmap watermark, StampPadding padding, int requestId) {

        int width = masterBitmap.getWidth();
        int height = masterBitmap.getHeight();

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        Bitmap newBitmap = null;
        Canvas canvas = null;

        try {
            newBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            canvas = new Canvas(newBitmap);
            canvas.drawBitmap(masterBitmap, 0, 0, paint);

            canvas.drawBitmap(watermark, padding.left, padding.top-watermark.getHeight(), paint);

//            canvas.save(Canvas.ALL_SAVE_FLAG);
            canvas.save();
            canvas.restore();

            if (mStampWatcher != null) {
                mStampWatcher.onSuccess(newBitmap,requestId);
            }
        } catch (Exception e) {

            if (mStampWatcher != null) {
                mStampWatcher.onError(e.getMessage(),requestId);
            }
        }
    }

    /**
     * draw text
     *
     * @param masterBitmap
     * @param label
     * @param labelSize
     * @param labelColor
     * @param padding
     */
    public void stampText(Bitmap masterBitmap, String label, int labelSize, int labelColor, StampPadding padding, int requestId) {
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(labelColor);
        paint.setTextSize(labelSize);

        Bitmap newBitmap = null;
        Canvas canvas = null;
        try {
            Bitmap.Config config = masterBitmap.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            newBitmap = masterBitmap.copy(config, true);
            canvas = new Canvas(newBitmap);
            canvas.drawText(label, padding.left, padding.top, paint);
//            canvas.save(Canvas.ALL_SAVE_FLAG); 在androidP上会报错，修改为canvas.save()
            canvas.save();
            canvas.restore();
            if (mStampWatcher != null) {
                mStampWatcher.onSuccess(newBitmap,requestId);
            }
        } catch (Exception e) {

            if (mStampWatcher != null) {
                mStampWatcher.onError(e.getMessage(),requestId);
            }
        }
    }


    /**
     * draw text and Image
     *
     * @param masterBitmap
     * @param label1
     * @param labelSize
     * @param labelColor
     * @param padding
     */
    public void stampTextAndImage(Bitmap masterBitmap, Bitmap watermark, String label1, String label2, int labelSize, int labelColor, StampPadding padding, int requestId) {
        Paint paint = new Paint();
        paint.setFilterBitmap(true);
        paint.setDither(true);
        paint.setColor(labelColor);
        paint.setTextSize(labelSize);

        Bitmap newBitmap = null;
        Canvas canvas = null;
        try {
            Bitmap.Config config = masterBitmap.getConfig();
            if (config == null) {
                config = Bitmap.Config.ARGB_8888;
            }
            newBitmap = masterBitmap.copy(config, true);
            canvas = new Canvas(newBitmap);
            canvas.drawBitmap(watermark, padding.left, padding.top-70, paint);
            canvas.drawText(label2, padding.left, padding.top, paint);
            TextPaint tp = new TextPaint();
            tp.setColor(labelColor);
            tp.setStyle(Paint.Style.FILL);
            tp.setTextSize(labelSize);

            canvas.translate(padding.left+40,padding.top-70);
            StaticLayout myStaticLayout = new StaticLayout(label1, tp, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
            myStaticLayout.draw(canvas);


           // canvas.drawText(label1, padding.left+40, padding.top-40, paint);




//            canvas.save(Canvas.ALL_SAVE_FLAG); 在androidP上会报错，修改为canvas.save()
            canvas.save();
            canvas.restore();
            if (mStampWatcher != null) {
                mStampWatcher.onSuccess(newBitmap,requestId);
            }
        } catch (Exception e) {

            if (mStampWatcher != null) {
                mStampWatcher.onError(e.getMessage(),requestId);
            }
        }
    }
}
