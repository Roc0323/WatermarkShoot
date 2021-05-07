package com.roc.baselibrary.util

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import com.roc.baselibrary.R

import java.util.*

/**
 * Created by Roc
 * 2020-02-21
 */

/**
 * dp、sp 转换为 px 的工具类
 */
class DisplayUtils {

    companion object{
        /**
         * 将px值转换为dip或dp值，保证尺寸大小不变（有精度损失）
         */
        fun px2dip(context: Context, pxValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (pxValue / scale + 0.5f).toInt()
        }

        /**
         * 将px值转换为dip或dp值，保证尺寸大小不变（无精度损失）
         */
        fun px2dipByFloat(context: Context, pxValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return pxValue / scale
        }

        /**
         * 将dip或dp值转换为px值，保证尺寸大小不变（有精度损失），类似Context.getDimensionPixelSize方法（四舍五入）
         */
        fun dip2px(context: Context, dipValue: Float): Int {
            val scale = context.resources.displayMetrics.density
            return (dipValue * scale + 0.5f).toInt()
        }

        /**
         * 将dip或dp值转换为px值，保证尺寸大小不变（无精度损失），类似Context.getDimension方法
         */
        fun dip2pxByFloat(context: Context, dipValue: Float): Float {
            val scale = context.resources.displayMetrics.density
            return dipValue * scale
        }

        /**
         * 将px值转换为sp值，保证文字大小不变
         */
        fun px2sp(context: Context, pxValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (pxValue / fontScale + 0.5f).toInt()
        }

        /**
         * 将sp值转换为px值，保证文字大小不变
         */
        fun sp2px(context: Context, spValue: Float): Int {
            val fontScale = context.resources.displayMetrics.scaledDensity
            return (spValue * fontScale + 0.5f).toInt()
        }

        /**
         * 屏幕宽度
         */
        fun getScreenWidthPixels(context: Context): Int {
            return context.resources.displayMetrics.widthPixels
        }

        /**
         * 屏幕高度
         */
        fun getScreenHeightPixels(context: Context): Int {
            return context.resources.displayMetrics.heightPixels
        }

        fun getDisplayInfo(context: Context): String {
            val infoFormat = "宽:%s,高:%s 宽Dip:%s,高Dip:%s\ndpi:%s,1dip=%sPixels"
            val screenWidthPixels = DisplayUtils.getScreenWidthPixels(context)
            val screenHeightPixels = DisplayUtils.getScreenHeightPixels(context)
            val density = context.resources.displayMetrics.density
            val infoFormatValue = arrayOf<Any>(
                screenWidthPixels,
                screenHeightPixels,
                (screenWidthPixels / density).toInt(),
                (screenHeightPixels / density).toInt(),
                context.resources.displayMetrics.densityDpi,
                density
            )
            return String.format(Locale.getDefault(), infoFormat, *infoFormatValue)
        }

        /**
         * 设置状态栏颜色
         */
        fun setStatusBarColor(activity: Activity, colorResId: Int) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                val window = activity.window
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                window.statusBarColor = ContextCompat.getColor(activity,colorResId)
            }

        }

        /**
         * 使状态栏透明
         */
        @TargetApi(Build.VERSION_CODES.KITKAT)
        private fun transparentStatusBar(activity:Activity) {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                activity.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                activity.window.statusBarColor = Color.TRANSPARENT
            } else {
                activity.window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            }
        }



        /**
         * 设置状态栏全屏透明（状态栏字体颜色为默认）
         */
        fun setStatusBarFullTranslucent(act: Activity) {
            //设置全屏透明状态栏
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                act.window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
                act.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                act.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                act.window.statusBarColor = Color.TRANSPARENT

            } else {
                setStatusBarColor(act, R.color.black)
            }
        }


        fun setStatusBarFullTranslucentWithBlackFont(activity:Activity) {
            transparentStatusBar(activity)

            setMIUIStatusBarDarkIcon(activity, true)
            setMeizuStatusBarDarkIcon(activity, true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            }
        }


        /**
         * 修改 MIUI V6  以上状态栏颜色
         */
        private fun setMIUIStatusBarDarkIcon(activity: Activity, darkIcon: Boolean) {
            val clazz = activity.window.javaClass
            try {
                val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
                val field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
                val darkModeFlag = field.getInt(layoutParams)
                val extraFlagField = clazz.getMethod(
                    "setExtraFlags",
                    Int::class.javaPrimitiveType,
                    Int::class.javaPrimitiveType
                )
                extraFlagField.invoke(
                    activity.window,
                    if (darkIcon) darkModeFlag else 0,
                    darkModeFlag
                )
            } catch (e: Exception) {
                //e.printStackTrace();
            }

        }

        /**
         * 修改魅族状态栏字体颜色 Flyme 4.0
         */
        private fun setMeizuStatusBarDarkIcon(activity: Activity, darkIcon: Boolean) {
            try {
                val lp = activity.window.attributes
                val darkFlag =
                    WindowManager.LayoutParams::class.java.getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags =
                    WindowManager.LayoutParams::class.java.getDeclaredField("meizuFlags")
                darkFlag.isAccessible = true
                meizuFlags.isAccessible = true
                val bit = darkFlag.getInt(null)
                var value = meizuFlags.getInt(lp)
                if (darkIcon) {
                    value = value or bit
                } else {
                    value = value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                activity.window.attributes = lp
            } catch (e: Exception) {
                //e.printStackTrace();
            }

        }


        /**
         * 获取状态栏的高度
         * @param context
         * @return
         */
        fun getStatusBarHeight(context: Context): Int {
            return context.resources.getDimensionPixelSize(
                context.resources.getIdentifier(
                    "status_bar_height",
                    "dimen",
                    "android"
                )
            )
        }

        /**
         * 获取ActionBar的高度
         * @param context
         * @return
         */
        fun getActionBarHeight(context: Context): Int {
            val actionbarSizeTypedArray =
                context.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))

            return actionbarSizeTypedArray.getDimension(0, 0f).toInt()
        }

    }


}
