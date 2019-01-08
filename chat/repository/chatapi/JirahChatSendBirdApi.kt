package com.jirah.sitterapp.chat.repository.chatapi

import com.jirah.sitterapp.chat.models.JirahChatDbMessage
import com.jirah.sitterapp.chat.models.toDbMessage
import com.jirah.sitterapp.chat.sdk.utils.ErrorResponse
import com.jirah.sitterapp.chat.sdk.utils.Response
import com.jirah.sitterapp.chat.sdk.utils.SuccessResponse
import com.jirah.sitterapp.chat.sdk.channel.JirahChatGroupChannel
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage

/**
 * Created by Ramiz Raja on 01/01/2019
 */
class JirahChatSendBirdApi(private val jirahChatGroupChannel: JirahChatGroupChannel) : JirahChatApi {
    override suspend fun getPastMessages(timestamp: Long, count: Int): Response<List<JirahChatUserMessage>> {
        return jirahChatGroupChannel.getPastMessages(timestamp, count)
    }

    override suspend fun getFutureMessages(timestamp: Long, count: Int): Response<List<JirahChatUserMessage>> {
        return jirahChatGroupChannel.getFutureMessages(timestamp, count)
    }

    override suspend fun getInitialItems(count: Int): Response<List<JirahChatUserMessage>> {
        return jirahChatGroupChannel.getPastMessages(System.currentTimeMillis(), count)
    }

    private fun Response<List<JirahChatUserMessage>>.toJirahChatDbMessageResponse(): Response<List<JirahChatDbMessage>> {
        return when (this) {
            is SuccessResponse -> { Response.create(data!!.map { it.toDbMessage() }, null) }
            is ErrorResponse -> ErrorResponse(null, errorMessage)
        }
    }
}