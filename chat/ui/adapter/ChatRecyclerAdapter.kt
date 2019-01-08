package com.jirah.sitterapp.chat.ui.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jirah.sitterapp.chat.models.JirahChatMessageType
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage


/**
 * Created by Ramiz Raja on 23/12/2018.
 */
class ChatRecyclerAdapter
    : ListAdapter<JirahChatUserMessage, RecyclerView.ViewHolder>(
        JirahChatUserMessage.DIFF_ITEM_CALLBACK
) {
    companion object {
        private val VIEW_TYPE_SENT = 0
        private val VIEW_TYPE_RECEIVED = 1
    }
    override fun getItemViewType(position: Int): Int {
        return when(getItem(position).messageType) {
            JirahChatMessageType.SENT -> VIEW_TYPE_SENT
            JirahChatMessageType.RECEIVED -> VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> SentMessageViewHolder.create(parent)
            else -> ReceivedMessageViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        when (holder) {
            is SentMessageViewHolder -> holder.bind(item)
            is ReceivedMessageViewHolder -> holder.bind(item)
        }
    }
}