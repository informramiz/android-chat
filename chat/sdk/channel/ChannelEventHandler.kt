package com.jirah.sitterapp.chat.sdk.channel

import androidx.lifecycle.*
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage
import com.jirah.sitterapp.chat.sdk.message.toJirahChatUserMessage
import com.sendbird.android.*
import timber.log.Timber


/**
 * Created by Ramiz Raja on 24/12/2018.
 */
class ChannelEventHandler(
        private val channelHandlerId: String
) : LiveData<ChannelEvent>() {

    override fun onActive() {
        SendBird.addChannelHandler(channelHandlerId, object : SendBird.ChannelHandler() {
            override fun onMessageReceived(baseChannel: BaseChannel, baseMessage: BaseMessage) {
                this@ChannelEventHandler.value = ChannelEvent(ChannelEventType.MESSAGE_RECEIVED,
                        ChannelEventInfo(JirahChatGroupChannel(baseChannel as GroupChannel),
                                JirahChatUserMessage(baseMessage as UserMessage)))
            }

            override fun onMessageDeleted(channel: BaseChannel, msgId: Long) {
                this@ChannelEventHandler.value = ChannelEvent(ChannelEventType.MESSAGE_DELETED,
                        ChannelEventInfo((channel as GroupChannel).toJiraChatGroupChannel(), msgId = msgId))
            }

            override fun onMessageUpdated(channel: BaseChannel, message: BaseMessage) {
                this@ChannelEventHandler.value = ChannelEvent(ChannelEventType.MESSAGE_UPDATED,
                        ChannelEventInfo((channel as GroupChannel).toJiraChatGroupChannel(),
                                (message as UserMessage).toJirahChatUserMessage()))
            }

            override fun onReadReceiptUpdated(channel: GroupChannel) {
                this@ChannelEventHandler.value = ChannelEvent(ChannelEventType.READ_RECEIPT_UPDATED,
                        ChannelEventInfo(channel.toJiraChatGroupChannel()))
            }

            override fun onTypingStatusUpdated(channel: GroupChannel) {
                this@ChannelEventHandler.value = ChannelEvent(ChannelEventType.TYPING_STATUS_UPDATED,
                        ChannelEventInfo(channel.toJiraChatGroupChannel()))
            }
        })
        Timber.d("Add sendBird Channel handler")
    }

    override fun onInactive()  {
        SendBird.removeChannelHandler(channelHandlerId)
        Timber.d("Remove sendBird Channel handler")
    }
}