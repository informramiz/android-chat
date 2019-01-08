package com.jirah.sitterapp.chat.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jirah.sitterapp.R
import com.jirah.sitterapp.chat.ui.customviews.SentChatBubbleView
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage


/**
 * Created by Ramiz Raja on 23/12/2018.
 */
class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val sentChatBubbleView = itemView.findViewById(R.id.bubble_view) as SentChatBubbleView

    fun bind(chatMessage: JirahChatUserMessage) {
        sentChatBubbleView.bubbleView.setMessageData(chatMessage)
    }

    companion object {
        fun create(parent: ViewGroup): SentMessageViewHolder {
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_sent, parent,
                    false).apply {
                return SentMessageViewHolder(this)
            }
        }
    }
}