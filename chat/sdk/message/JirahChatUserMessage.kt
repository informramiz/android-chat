package com.jirah.sitterapp.chat.sdk.message

import androidx.recyclerview.widget.DiffUtil
import com.jirah.sitterapp.chat.models.JirahChatMessageType
import com.jirah.sitterapp.chat.sdk.JirahChat
import com.sendbird.android.Sender
import com.sendbird.android.UserMessage


/**
 * Created by Ramiz Raja on 28/12/2018.
 */
data class JirahChatUserMessage(private var userMessage: UserMessage) {
    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<JirahChatUserMessage>() {
            override fun areItemsTheSame(oldItem: JirahChatUserMessage, newItem: JirahChatUserMessage): Boolean {
                return oldItem.requestId == newItem.requestId || oldItem.messageId == newItem.messageId
            }

            override fun areContentsTheSame(oldItem: JirahChatUserMessage, newItem: JirahChatUserMessage): Boolean {
                return oldItem.areContentsTheSame(newItem)
            }
        }

        fun buildFromSerializedData(byteArray: ByteArray): JirahChatUserMessage {
            return JirahChatUserMessage(UserMessage.buildFromSerializedData(byteArray) as UserMessage)
        }
    }

    val message: String
        get() {
            return userMessage.message
        }

    val requestId: String
        get() {
            return userMessage.requestId
        }

    val createdAt: Long
        get() {
            return userMessage.createdAt
        }

    val updatedAt: Long
        get() {
            return userMessage.updatedAt
        }

    val messageId: Long
        get() {
            return userMessage.messageId
        }

    val sender: Sender?
        get() = userMessage.sender

    val messageType: JirahChatMessageType
        get() {
            return if (userMessage.sender.userId == JirahChat.getCurrentUserId()) {
                JirahChatMessageType.SENT
            } else {
                JirahChatMessageType.RECEIVED
            }
        }

    val isSending: Boolean
        get() {
            return messageId == 0L
        }

    val isSent: Boolean
        get() {
            return messageId > 0L
        }

    var isRead: Boolean = false

    val channelUrl: String
        get() {
            return userMessage.channelUrl
        }

    fun serialize(): ByteArray {
        return userMessage.serialize()
    }

//    override fun equals(other: Any?): Boolean {
//        return when (other) {
//            null -> super.equals(other)
//            is JirahChatUserMessage -> this.areContentsTheSame(other)
//            else -> super.equals(other)
//        }
//    }
//
//    override fun hashCode(): Int {
//        return (createdAt % 10000L).toInt()
//    }

//    override fun compareTo(other: JirahChatUserMessage): Int {
//        return when(areContentsTheSame(other)) {
//            true -> 0
//            else -> 1
//        }
//    }

    fun areContentsTheSame(other: JirahChatUserMessage): Boolean {
        return this.requestId == other.requestId
                && this.updatedAt == other.updatedAt
                && this.createdAt == other.createdAt
                && this.message == other.message
    }

    fun toSendBirdUserMessage(): UserMessage {
        return userMessage
    }
}

fun UserMessage?.toJirahChatUserMessage(): JirahChatUserMessage? {
    return when(this) {
        null -> null
        else -> JirahChatUserMessage(this)
    }
}