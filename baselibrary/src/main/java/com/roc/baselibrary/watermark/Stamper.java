package com.roc.baselibrary.watermark;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;

/**
 * Created by roc on 4/12/21 3:25 PM.
 * Desc:This is.. Stamper support draw iamge and text
 */
public class Stamper {

    private Context mContext;
    private StampManager mStampManager;

    public Stamper(Context context) {
        mContext = context;
    }

    public static Stamper with(Context context) {
        return new Stamper(context);
    }

    private Bitmap mMasterBitmap;
    private Bitmap mWatermark;
    private StampPadding mStampPadding = new StampPadding(10, 10);
    private String mLabel;
    private String mLabel2;
    private int mLabelSize;
    private int mLabelColor;
    private int mRequestId;
    private StampType mStampType;
    private StampWatcher mStampWatcher;

    public Bitmap getMasterBitmap() {
        return mMasterBitmap;
    }

    public Stamper setMasterBitmap(Bitmap masterBitmap) {
        mMasterBitmap = masterBitmap;
        return this;
    }

    public Bitmap getWatermark() {
        return mWatermark;
    }

    public Stamper setWatermark(Bitmap watermark) {
        mWatermark = watermark;
        return this;
    }

    public StampPadding getStampPadding() {
        return mStampPadding;
    }

    public Stamper setStampPadding(StampPadding stampPadding) {
        mStampPadding = stampPadding;
        return this;
    }

    public String getLabel() {
        return mLabel;
    }

    public String getLabel2() {
        return mLabel2;
    }

    public Stamper setLabel(String label) {
        mLabel = label;
        return this;
    }

    public Stamper setLabel2(String label) {
        mLabel2 = label;
        return this;
    }

    public int getLabelSize() {
        return mLabelSize;
    }

    public Stamper setLabelSize(int labelSize) {
        mLabelSize = labelSize;
        return this;
    }

    public int getLabelColor() {
        return mLabelColor;
    }

    public Stamper setLabelColor(int labelColor) {
        mLabelColor = labelColor;
        return this;
    }

    public int getRequestId() {
        return mRequestId;
    }

    public Stamper setRequestId(int requestId) {
        mRequestId = requestId;
        return this;
    }

    public StampWatcher getStampWatcher() {
        return mStampWatcher;
    }

    public Stamper setStampWatcher(StampWatcher stampWatcher) {
        mStampWatcher = stampWatcher;
        return this;
    }

    public StampType getStampType() {
        return mStampType;
    }

    public Stamper setStampType(StampType stampType) {
        mStampType = stampType;
        return this;
    }

    public void build() {

        if (checkParams()) {

            if (mStampManager == null) {
                mStampManager = new StampManager(mContext, mStampWatcher);
            }
            switch (mStampType) {

                case TEXT:
//                    mStampManager.stampText(mMasterBitmap, mLabel, mLabelSize, mLabelColor, mStampPadding,mRequestId);
                    if (mLabel==null){
                        mLabel = "";
                    }
                    if (mLabel2==null){
                        mLabel2 = "";
                    }
                    mStampManager.stampTextAndImage(mMasterBitmap,mWatermark,mLabel,mLabel2,mLabelSize,mLabelColor,mStampPadding,mRequestId);
                    break;
                case IMAGE:
                    mStampManager.stampImage(mMasterBitmap, mWatermark, mStampPadding,mRequestId);
                    break;

            }
        }
    }

    protected boolean checkParams() {

        if (mContext == null) {
            throw new NullPointerException("Context can't be null,please set a value for Context.");
        }

        if (mStampType == null) {
            throw new NullPointerException("StampType can't be null,please set a value for StampType.");
        }

        if (mMasterBitmap == null) {
            throw new NullPointerException("MasterBitmap can't be null,please set a value for MasterBitmap.");
        }

        if (mStampWatcher == null) {
            throw new NullPointerException("StampWatcher can't be null,please set a value for StampWatcher.");
        }

        switch (mStampType) {

            case TEXT:
                if (TextUtils.isEmpty(mLabel)) {
                    throw new NullPointerException("Label can't be null,please set a value for Label.");
                }
                break;

            case IMAGE:


                if (mWatermark == null) {
                    throw new NullPointerException("Watermark can't be null,please set a value for Watermark.");
                }

                break;
        }

        return true;
    }
}
