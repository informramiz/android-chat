package com.jirah.sitterapp.chat.repository.chatapi

import androidx.annotation.MainThread
import com.jirah.sitterapp.chat.sdk.utils.Response
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage

/**
 * Created by Ramiz Raja on 31/12/2018
 */
interface JirahChatApi {
    @MainThread
    suspend fun getPastMessages(timestamp: Long, count: Int = 30): Response<List<JirahChatUserMessage>>
    @MainThread
    suspend fun getFutureMessages(timestamp: Long, count: Int = 30): Response<List<JirahChatUserMessage>>
    @MainThread
    suspend fun getInitialItems(count: Int = 50): Response<List<JirahChatUserMessage>>
}