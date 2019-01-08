package com.jirah.sitterapp.chat.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.toLiveData
import com.jirah.sitterapp.base.extensions.map
import com.jirah.sitterapp.chat.models.JirahChatDbMessage
import com.jirah.sitterapp.chat.models.toDbMessage
import com.jirah.sitterapp.chat.repository.chatapi.JirahChatApi
import com.jirah.sitterapp.chat.repository.chatapi.JirahChatSendBirdApi
import com.jirah.sitterapp.chat.repository.db.JirahChatDatabase
import com.jirah.sitterapp.chat.repository.paging.MessagePageListing
import com.jirah.sitterapp.chat.repository.paging.MessagePagingState
import com.jirah.sitterapp.chat.repository.paging.MessagesBoundaryCallback
import com.jirah.sitterapp.chat.sdk.utils.SuccessResponse
import com.jirah.sitterapp.chat.sdk.channel.JirahChatGroupChannel
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created by Ramiz Raja on 30/12/2018.
 */
class JirahChatRepository {
    private val database = JirahChatDatabase.get()
    private lateinit var groupChannel: JirahChatGroupChannel
    private val chatApi: JirahChatApi by lazy {
        requireChannel()
        JirahChatSendBirdApi(groupChannel)
    }

    private fun requireChannel() {
        require(this::groupChannel.isInitialized) {
            "Group channel field must be initialized first"
        }
    }

    fun setGroupChannel(groupChannel: JirahChatGroupChannel) {
        this.groupChannel = groupChannel
    }

    fun getUserMessages(): LiveData<List<JirahChatUserMessage>> {
        return database.chatDao().loadAllMessages().map { dbMessages ->
            dbMessages?.map { jirahChatDbMessage -> jirahChatDbMessage.toUserMessage() }
        }
    }

    private suspend fun refreshMessages(stateHandler: MutableLiveData<MessagePagingState>) {
        stateHandler.value = MessagePagingState.LOADING
        val messagesResponse = chatApi.getPastMessages(System.currentTimeMillis())
        if (messagesResponse is SuccessResponse) {
            messagesResponse.data?.let { userMessages ->
                insertMessages(userMessages)
            }
        }
        stateHandler.value = MessagePagingState.LOADED
    }

    fun getUserMessagesWithPagination(uiScope: CoroutineScope,
                                      groupChannel: JirahChatGroupChannel): MessagePageListing<JirahChatUserMessage> {
        this.groupChannel = groupChannel
        requireChannel()

        val messageBoundaryCallback = MessagesBoundaryCallback(uiScope, this.chatApi) { userMessages ->
            insertMessages(userMessages)
        }
        val livePagedList = database.chatDao().loadMessagesDataSourceFactory(groupChannel.channelUrl)
                .map { it.toUserMessage() }
                .toLiveData(pageSize = 30, boundaryCallback = messageBoundaryCallback)

        return MessagePageListing(livePagedList,
                messageBoundaryCallback.loadAfterPagingState,
                messageBoundaryCallback.loadAfterPagingState,
                messageBoundaryCallback.refreshState,
                {
                    uiScope.launch { refreshMessages(messageBoundaryCallback.refreshState) }
                },
                {})
    }

    private suspend fun insertMessages(userMessages: List<JirahChatUserMessage>) {
        withContext(Dispatchers.IO) {
            database.chatDao().insertMessages(userMessages.map {
                it.toDbMessage()
            })
        }
    }

    private fun removeTempMessageIfAny(userMessages: List<JirahChatUserMessage>?): List<JirahChatUserMessage> {
        userMessages ?: return emptyList()
        val tempMessage = userMessages.find { it.messageId == 0L } ?: return userMessages

        val similarMessage = userMessages.firstOrNull {
            it.messageId != 0L
                    && it.requestId == tempMessage.requestId
        } ?: return userMessages

        val filteredList = userMessages.toMutableList()
        filteredList.remove(tempMessage)
        return filteredList
    }

    suspend fun saveMessage(message: JirahChatUserMessage) {
        withContext(Dispatchers.IO) {
            database.chatDao().insertMessage(message.toDbMessage())
        }
    }

    suspend fun deleteMessage(message: JirahChatUserMessage) {
        withContext(Dispatchers.IO) {
            database.chatDao().delete(message.toDbMessage())
        }
    }

    suspend fun updateMessage(message: JirahChatUserMessage) {
        saveMessage(message)
    }

    suspend fun updateTempMessage(message: JirahChatUserMessage) {
        withContext(Dispatchers.IO) {
            database.chatDao().updateTempMessage(message.toDbMessage())
        }
    }

    private fun JirahChatDbMessage.toUserMessage(): JirahChatUserMessage {
        return JirahChatUserMessage.buildFromSerializedData(this@toUserMessage.data)
    }
}