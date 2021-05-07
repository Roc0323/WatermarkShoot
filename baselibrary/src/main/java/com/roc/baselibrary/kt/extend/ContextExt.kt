package com.roc.baselibrary.kt.extend

import android.content.Context
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

/**
 * Create by roc
 * 2019-11-06 14:56
 */

/**
 * 获取颜色
 */
@ColorInt
fun Context.getColorInt(@ColorRes id: Int): Int {
    return ContextCompat.getColor(this, id)
}


/**
 * 获取颜色
 */
@ColorInt
fun Fragment.getColorInt(@ColorRes id: Int): Int {
    val context = this.context

    return if (context != null) {
        ContextCompat.getColor(context, id)
    } else {
        IllegalStateException("当前Fragment的Context为空！！！").printStackTrace()
        0
    }
}

/**
 * 获取 drawable
 */
fun Context.getCompatDrawable(@DrawableRes id: Int) = ContextCompat.getDrawable(this, id)
