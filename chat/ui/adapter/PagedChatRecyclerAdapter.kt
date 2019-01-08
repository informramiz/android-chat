package com.jirah.sitterapp.chat.ui.adapter

import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.jirah.sitterapp.chat.models.JirahChatMessageType
import com.jirah.sitterapp.chat.sdk.channel.JirahChatGroupChannel
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage


/**
 * Created by Ramiz Raja on 23/12/2018.
 */
class PagedChatRecyclerAdapter
    : PagedListAdapter<JirahChatUserMessage, RecyclerView.ViewHolder>(
        JirahChatUserMessage.DIFF_ITEM_CALLBACK
) {
    companion object {
        private val VIEW_TYPE_SENT = 0
        private val VIEW_TYPE_RECEIVED = 1
        private val VIEW_TYPE_TYPING = 2
    }

    lateinit var jiraChatGroupChannel: JirahChatGroupChannel
    private var isTyping = false

    override fun getItemViewType(position: Int): Int {
        if (isTyping && isLast(position)) return VIEW_TYPE_TYPING

        val item = getItem(position)
        item ?: return VIEW_TYPE_SENT
        return when(item.messageType) {
            JirahChatMessageType.SENT -> VIEW_TYPE_SENT
            JirahChatMessageType.RECEIVED -> VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_SENT -> SentMessageViewHolder.create(parent)
            VIEW_TYPE_TYPING -> TypingViewHolder.create(parent)
            else -> ReceivedMessageViewHolder.create(parent)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_TYPING) return

        val item = getItem(position)
        item ?: return

        when (holder) {
            is SentMessageViewHolder -> holder.bind(item.apply {
                if (this@PagedChatRecyclerAdapter::jiraChatGroupChannel.isInitialized) {
                    isRead = jiraChatGroupChannel.getReadReceipt(this)
                }
            })
            is ReceivedMessageViewHolder -> holder.bind(item)
        }
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (isTyping) 1 else 0
    }

    private fun isLast(position: Int) = position == itemCount - 1

    fun onReadReceiptUpdated(updatedChannel: JirahChatGroupChannel) {
        this.jiraChatGroupChannel = updatedChannel
        notifyItemRangeChanged(itemCount - 15, itemCount)
    }

    fun onTypingStatusUpdated(isTyping: Boolean) {
        val wasTyping = this.isTyping
        this.isTyping = isTyping

        if (isTyping != wasTyping) {
            if (isTyping) {
                notifyItemInserted(super.getItemCount())
            } else if (wasTyping) {
                notifyItemRemoved(super.getItemCount())
            }
        }
    }
}