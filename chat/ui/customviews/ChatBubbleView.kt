package com.jirah.sitterapp.chat.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.google.android.flexbox.FlexboxLayout
import com.jirah.sitterapp.R
import com.jirah.sitterapp.base.extensions.getFormattedTime12HourShort
import com.jirah.sitterapp.base.extensions.unicodeWrap
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage
import com.jirah.sitterapp.ui.extensions.viewextensions.updateVisibility
import java.util.*

/**
 * Created by Ramiz Raja on 22/12/2018
 */
class ChatBubbleView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.chatBubbleStyle
) : FlexboxLayout(context, attrs, defStyleAttr) {

    private val msgTextView by lazy { findViewById<TextView>(R.id.textView_msg) }
    private val msgTimeTextView by lazy { findViewById<TextView>(R.id.textView_msg_time) }
    private val msgSentStatusImageView by lazy { findViewById<ImageView>(R.id.ic_msg_sent_status) }

    private lateinit var msgData: JirahChatUserMessage
    var chatBubbleType = ChatBubbleType.SENT
        set(value) {
            field = value
            msgSentStatusImageView?.updateVisibility(value == ChatBubbleType.SENT,
                    makeItGone = true)
        }

    init {
        setBackgroundResource(R.drawable.ic_sent_msg_bubble)
        LayoutInflater.from(context).inflate(R.layout.chat_bubble_view, this, true)
    }

    fun setMessageData(chatMessage: JirahChatUserMessage) {
        msgData = chatMessage
        updateViews()
    }

    private fun updateViews() {
        msgTextView.text = msgData.message.unicodeWrap()
        msgTimeTextView.text = getFormattedTime().unicodeWrap()
        updateMessageStatus()
    }

    private fun updateMessageStatus() {
        if (chatBubbleType != ChatBubbleType.SENT) {
            return
        }

        val iconResId = when {
            msgData.isRead -> R.drawable.ic_seen_chat
            msgData.isSent -> R.drawable.ic_msg_status_sent
            else -> 0
        }

        if (iconResId != 0) {
            msgSentStatusImageView.visibility = View.VISIBLE
            msgSentStatusImageView.setImageResource(iconResId)
        } else {
            msgSentStatusImageView.visibility = View.GONE
        }
    }

    private fun getFormattedTime() =
            Calendar.getInstance().apply { timeInMillis = msgData.createdAt }.getFormattedTime12HourShort()
}