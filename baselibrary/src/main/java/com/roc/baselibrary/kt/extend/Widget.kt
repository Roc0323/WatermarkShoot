package com.roc.baselibrary.kt.extend

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.roc.baselibrary.R

/**
 * Created by roc
 *  文件描述：
 *  修改记录：
 */
fun TextView.afterTextChanged(afterTextChanged: (Editable) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable) {
            afterTextChanged.invoke(editable)
        }
    })
}

fun EditText.focusChangeListener(focusChangeListener: (Boolean) -> Unit){
    this.setOnFocusChangeListener { v, hasFocus ->
        focusChangeListener.invoke(hasFocus)
    }
}

fun TextView.content(): String = this.text.toString().trim()

private var <T : View> T.triggerDelay: Long
    get() = if (getTag(R.id.triggerDelay) != null) getTag(R.id.triggerDelay) as Long else -1
    set(value) {
        setTag(R.id.triggerDelay, value)
    }

private var <T : View> T.triggerLastTime: Long
    get() = if (getTag(R.id.triggerLastTime) != null) getTag(R.id.triggerLastTime) as Long else 0
    set(value) {
        setTag(R.id.triggerLastTime, value)
    }

/***
 * 带延迟过滤的点击事件View扩展
 * @param delay Long 延迟时间，默认600毫秒
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.clickWithTrigger(time: Long = 600, block: (T) -> Unit) {
    triggerDelay = time
    setOnClickListener {
        if (clickEnable()) {
            block(it as T)
        }
    }
}

private fun <T : View> T.clickEnable(): Boolean {
    var flag = false
    val currentClickTime = System.currentTimeMillis()
    if (currentClickTime - triggerLastTime >= triggerDelay) {
        flag = true
    }
    triggerLastTime = currentClickTime
    return flag
}


/***
 * 点击事件的View扩展
 * @param block: (T) -> Unit 函数
 * @return Unit
 */
fun <T : View> T.click(block: (T) -> Unit) = setOnClickListener {
    @Suppress("UNCHECKED_CAST")
    block(it as T)
}


/**
 * VISIBLE
 */
fun View?.setVisible() {
    this?.let {
        if (visibility != View.VISIBLE)
            it.visibility = View.VISIBLE
    }
}

fun View?.setGone() {
    this?.let {
        if (visibility != View.GONE)
            it.visibility = View.GONE
    }
}

fun View?.setInvisible() {
    this?.let {
        if (visibility != View.INVISIBLE)
            it.visibility = View.INVISIBLE
    }
}


fun View?.setVisible(enable: Boolean) {
    if (enable) {
        setVisible()
    }else{
        setGone()
    }
}

fun View?.setGone(enable: Boolean) {
    if (enable) {
        setGone()
    }else{
        setVisible()
    }
}

fun View?.setInVisible(enable: Boolean) {
    if (enable) {
        setVisible()
    }else{
        setInvisible()
    }
}
val View.isVisible: Boolean get() = visibility == View.VISIBLE
