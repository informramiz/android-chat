package com.jirah.sitterapp.chat.ui.chat

import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData

/**
 * Created by Ramiz Raja on 05/01/2019
 */
class TypingMonitor : TextWatcher, LiveData<Boolean>() {
    companion object {
        private const val setUserNotTypingRunnableToken = "setUserNotTypingRunnableToken"
    }

    private val handler = Handler()
    private var isCallbackPending = false
    private val setUserNotTypingRunnable = {
        isCallbackPending = false
        if (isTyping()) {
            setTypingStatus(false)
        }
    }

    override fun onActive() {
        super.onActive()
    }

    override fun onInactive() {
        super.onInactive()
        setTypingStatus(false)
    }

    override fun afterTextChanged(s: Editable?) {
        if (isTyping() && !isCallbackPending) {
            HandlerCompat.postDelayed(handler, setUserNotTypingRunnable, setUserNotTypingRunnableToken,5000)
            isCallbackPending = true
        }
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        if (!isTyping()) {
            setTypingStatus(true)
        }

        if (s.isEmpty()) {
            setTypingStatus(false)
        }
    }

    private fun isTyping(): Boolean {
        return value ?: false
    }

    private fun setTypingStatus(isTyping: Boolean) {
        value = isTyping
    }
}