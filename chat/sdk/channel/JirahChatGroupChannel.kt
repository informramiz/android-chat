package com.jirah.sitterapp.chat.sdk.channel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.jirah.sitterapp.chat.sdk.utils.JiraChatResource
import com.jirah.sitterapp.chat.sdk.utils.Response
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage
import com.jirah.sitterapp.chat.sdk.message.toJirahChatUserMessage
import com.sendbird.android.BaseChannel
import com.sendbird.android.GroupChannel
import com.sendbird.android.UserMessage
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Created by Ramiz Raja on 26/12/2018
 */
class JirahChatGroupChannel(private val groupChannel: GroupChannel) {
    companion object {
        suspend fun createChannelWithUserIds(userIds: List<String>, distinct: Boolean = true)
                : Response<JirahChatGroupChannel> {
            return suspendCoroutine { continuation ->
                GroupChannel.createChannelWithUserIds(userIds, distinct) { groupChannel, sendBirdException ->
                    continuation.resume(Response.create(groupChannel.toJiraChatGroupChannel(), sendBirdException))
                }
            }
        }

        suspend fun getChannel(channelUrl: String): Response<JirahChatGroupChannel> {
            return suspendCoroutine { continuation ->
                GroupChannel.getChannel(channelUrl) { channel, senBirdException ->
                    continuation.resume(Response.create(channel.toJiraChatGroupChannel(), senBirdException))
                }
            }
        }
    }

    val channelUrl: String
        get() {
            return groupChannel.url
        }

    fun sendUserMessage(message: String): LiveData<JiraChatResource<JirahChatUserMessage>> {
        val sendUserMessageHandler = MutableLiveData<JiraChatResource<JirahChatUserMessage>>()
        val messageBeingSent = groupChannel.sendUserMessage(message) { userMessage, sendBirdException ->
            sendUserMessageHandler.value = JiraChatResource.create(userMessage.toJirahChatUserMessage(), sendBirdException)
        }
        sendUserMessageHandler.value = JiraChatResource.loading(messageBeingSent.toJirahChatUserMessage())
        return sendUserMessageHandler
    }

    fun markAsRead() {
        groupChannel.markAsRead()
    }

    fun getReadReceipt(message: JirahChatUserMessage): Boolean {
        return groupChannel.getReadReceipt(message.toSendBirdUserMessage()) == 0
                && groupChannel.memberCount > 1
    }

    fun isLastMessageRead(): Boolean {
        return groupChannel.getReadReceipt(groupChannel.lastMessage as UserMessage) == 0
    }

    fun getReadStatus(): Boolean {
        return groupChannel.getReadStatus(true).isNotEmpty()
    }

    fun isChannelRead(): Boolean {
        return getReadStatus() || isLastMessageRead()
    }

    suspend fun getPastMessages(timestamp: Long, count: Int): Response<List<JirahChatUserMessage>> {
        return suspendCoroutine { continuation ->
            groupChannel.getPreviousMessagesByTimestamp(timestamp, false, count,
                    false, BaseChannel.MessageTypeFilter.USER, null) { messages, sendBirdException ->
                continuation.resume(Response.create(messages.filterNotNull().map { (it as UserMessage).toJirahChatUserMessage()!! }, sendBirdException))
            }
        }
    }

    suspend fun getFutureMessages(timestamp: Long, count: Int): Response<List<JirahChatUserMessage>> {
        return suspendCoroutine { continuation ->
            groupChannel.getNextMessagesByTimestamp(timestamp, false, count,
                    false, BaseChannel.MessageTypeFilter.USER, null) { messages, sendBirdException ->
                continuation.resume(Response.create(messages.filterNotNull().map { (it as UserMessage).toJirahChatUserMessage()!! }, sendBirdException))
            }
        }
    }

    fun setTypingStatus(isTyping: Boolean) {
        if (isTyping) {
            groupChannel.startTyping()
        } else {
            groupChannel.endTyping()
        }
    }

    fun isTyping(): Boolean {
        return groupChannel.typingMembers.isNotEmpty()
    }
}

internal fun GroupChannel.toJiraChatGroupChannel(): JirahChatGroupChannel {
    return JirahChatGroupChannel(this)
}