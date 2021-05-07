@file:Suppress("NOTHING_TO_INLINE")

package com.roc.baselibrary.kt.extend

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * Create by roc
 * 2019-10-17 09:51
 */

inline infix fun <T : Number> T?.otherwise(num: T): T {
    return this ?: num
}

inline val Short?.orZero: Short
    get() = this ?: 0

inline val Int?.orZero: Int
    get() = this ?: 0

inline val Long?.orZero: Long
    get() = this ?: 0

inline val Float?.orZero: Float
    get() = this ?: 0F

inline val Double?.orZero: Double
    get() = this ?: 0.0


/**
 * 保留两位小数
 */
inline fun Number.formatKeepTwo(): String = BigDecimal(DecimalFormat("#.00").format(this)).toPlainString()
