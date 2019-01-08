package com.jirah.sitterapp.chat.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jirah.sitterapp.R
import com.jirah.sitterapp.chat.ui.customviews.ReceivedChatBubbleView
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage


/**
 * Created by Ramiz Raja on 23/12/2018.
 */
class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val receivedChatBubbleView = itemView.findViewById(R.id.bubble_view) as ReceivedChatBubbleView

    fun bind(userMessage: JirahChatUserMessage) {
        receivedChatBubbleView.bubbleView.setMessageData(userMessage)
    }

    companion object {
        fun create(parent: ViewGroup): ReceivedMessageViewHolder {
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat_received, parent,
                    false).apply {
                return ReceivedMessageViewHolder(this)
            }
        }
    }
}