package com.roc.baselibrary.kt.extend

/**
 * Created by roc
 * Desc:
 */
sealed class BooleanExt<out T>

object OtherWise : BooleanExt<Nothing>()
class WhitData<T>(val data: T) : BooleanExt<T>()

inline fun <T> Boolean.yes(block: () -> T) =
        when {
            this -> WhitData(block())
            else -> OtherWise
        }

inline fun <T> Boolean.no(block: () -> T) =
        when {
            this -> OtherWise
            else -> WhitData(block())
        }

inline fun <T> BooleanExt<T>.otherWise(block: () -> T) =
        when (this) {
            is OtherWise -> block()
            is WhitData -> this.data
        }