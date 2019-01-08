package com.jirah.sitterapp.chat.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.jirah.sitterapp.R

/**
 * Created by Ramiz Raja on 06/01/2019
 */
class TypingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    companion object {
        fun create(parent: ViewGroup): TypingViewHolder {
            LayoutInflater.from(parent.context)
                    .inflate(R.layout.list_item_chat_typing, parent, false)
                    .apply { return TypingViewHolder(this) }
        }
    }
}