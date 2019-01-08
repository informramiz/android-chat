package com.jirah.sitterapp.chat.ui.customviews

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.widget.LinearLayout
import androidx.appcompat.widget.LinearLayoutCompat
import com.jirah.sitterapp.R


/**
 * Created by Ramiz Raja on 23/12/2018.
 */
class ReceivedChatBubbleView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : LinearLayoutCompat(context, attrs, defStyleAttr) {

    val bubbleView = ChatBubbleView(context).apply {
        setBackgroundResource(R.drawable.ic_received_msg_bubble)
        chatBubbleType = ChatBubbleType.RECEIVED
    }

    init {
        orientation = HORIZONTAL
        gravity = Gravity.START
        addBubbleView()
    }

    private fun addBubbleView() {
        val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT)
        addView(bubbleView, layoutParams)
    }
}