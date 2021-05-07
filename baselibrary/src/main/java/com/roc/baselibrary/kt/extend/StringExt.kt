@file:Suppress("NOTHING_TO_INLINE")

package com.roc.baselibrary.kt.extend

/**
 * Create by roc
 * 2019-10-17 09:44
 */

/**
 * 如果空或空字符串则返回默认值
 */
inline infix fun String?.or(default: String): String =
        if (this.isNullOrEmpty()) {
            default
        } else {
            this
        }

/**
 * 如果空或空字符串则返回默认值
 */
inline fun String?.encryptIdCard(): String =
        when {
            this.isNullOrEmpty() -> ""
            this.length != 18 -> this
            else -> this.replaceRange(3, 14, "***********")
        }



