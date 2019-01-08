package com.jirah.sitterapp.chat.sdk.channel

import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage
import com.sendbird.android.BaseChannel
import com.sendbird.android.BaseMessage


/**
 * Created by Ramiz Raja on 24/12/2018.
 */
data class ChannelEventInfo(val groupChannel: JirahChatGroupChannel,
                            val message: JirahChatUserMessage? = null,
                            val msgId: Long? = null)