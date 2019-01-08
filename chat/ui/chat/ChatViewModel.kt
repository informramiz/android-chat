package com.jirah.sitterapp.chat.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagedList
import com.google.firebase.iid.FirebaseInstanceId
import com.jirah.sitterapp.chat.repository.JirahChatRepository
import com.jirah.sitterapp.chat.repository.paging.MessagePageListing
import com.jirah.sitterapp.chat.sdk.utils.ErrorResponse
import com.jirah.sitterapp.chat.sdk.utils.JiraChatResource
import com.jirah.sitterapp.chat.sdk.JirahChat
import com.jirah.sitterapp.chat.sdk.utils.SuccessResponse
import com.jirah.sitterapp.chat.sdk.channel.ChannelEvent
import com.jirah.sitterapp.chat.sdk.channel.ChannelEventHandler
import com.jirah.sitterapp.chat.sdk.channel.JirahChatGroupChannel
import com.jirah.sitterapp.chat.sdk.connection.ConnectionHandler
import com.jirah.sitterapp.chat.sdk.connection.ConnectionStatus
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage
import com.jirah.sitterapp.ui.notification.push.fcm.getPushToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

class ChatViewModel @Inject constructor() : ViewModel() {
    companion object {
        const val CHANNEL_EVENT_HANDLER_ID = "channel_event_handler"
        const val CONNECTION_HANDLER_ID = "connection_handler"
    }

    private val job = Job()
    private val uiScope = CoroutineScope(Dispatchers.Main + job)
    private val chatRepository = JirahChatRepository()

    private lateinit var groupChannel: JirahChatGroupChannel
    private val initChatResponse = MutableLiveData<JiraChatResource<JirahChatGroupChannel>>()
    private lateinit var connectionHandler: ConnectionHandler
    private lateinit var channelEventHandler: ChannelEventHandler

    //paged list
    private lateinit var pagedUserMessages: MessagePageListing<JirahChatUserMessage>

    override fun onCleared() {
        super.onCleared()
        job.cancel()
    }

    fun initChat(thisUserId: String, otherUserId: String): LiveData<JiraChatResource<JirahChatGroupChannel>> {
        uiScope.launch {
            val connectResponse = JirahChat.connect(thisUserId)
            if (connectResponse is SuccessResponse) {
                createChannelWithUserId(thisUserId, otherUserId)
                registerPushToken()
            } else {
                initChatResponse.value = JiraChatResource.error(null, (connectResponse as ErrorResponse).errorMessage)
            }
        }

        initChatResponse.value = JiraChatResource.loading(null)
        return initChatResponse
    }

    fun initChatWithUrl(thisUserId: String, channelUrl: String): LiveData<JiraChatResource<JirahChatGroupChannel>> {
        uiScope.launch {
            val connectResponse = JirahChat.connect(thisUserId)
            if (connectResponse is SuccessResponse) {
                createChannelWithUrl(thisUserId, channelUrl)
                registerPushToken()
            } else {
                initChatResponse.value = JiraChatResource.error(null, (connectResponse as ErrorResponse).errorMessage)
            }
        }

        initChatResponse.value = JiraChatResource.loading(null)
        return initChatResponse
    }

    private suspend fun createChannelWithUserId(thisUserId: String, otherUserId: String) {
        val response = JirahChatGroupChannel.createChannelWithUserIds(listOf(thisUserId, otherUserId), true)
        Timber.d("Create channel response: $response")
        when (response) {
            is SuccessResponse -> {
                initializeChat(response.data!!, thisUserId)
            }
            is ErrorResponse -> { initChatResponse.value = JiraChatResource.error(null, response.errorMessage) }
        }
    }

    private suspend fun createChannelWithUrl(thisUserId: String, channelUrl: String) {
        val response = JirahChatGroupChannel.getChannel(channelUrl)
        Timber.d("Create channel response: $response")
        when (response) {
            is SuccessResponse -> {
                initializeChat(response.data!!, thisUserId)
            }
            is ErrorResponse -> { initChatResponse.value = JiraChatResource.error(null, response.errorMessage) }
        }
    }

    private fun initializeChat(channel: JirahChatGroupChannel, thisUserId: String) {
        groupChannel = channel
        connectionHandler = JirahChat.addConnectionHandler(thisUserId, CONNECTION_HANDLER_ID)
        channelEventHandler = JirahChat.addChannelEventHandler(CHANNEL_EVENT_HANDLER_ID)
        pagedUserMessages = chatRepository.getUserMessagesWithPagination(uiScope, groupChannel)
        initChatResponse.value = JiraChatResource.success(groupChannel)
    }

    private suspend fun registerPushToken() {
        val taskResult = FirebaseInstanceId.getInstance().getPushToken()
        Timber.d("FirebaseInstanceId.getInstance().getPushToken() result: $taskResult")
        if (taskResult.isSuccessful) {
            taskResult.result?.token?.let { token ->
                Timber.d("FCM token: $token")
                val response = JirahChat.registerPushToken(token)
                Timber.d("Update JiraChat push token: $response")
            }
        }
    }

    fun sendUserMessage(message: String): LiveData<JiraChatResource<JirahChatUserMessage>> {
        requireConnected()
        return groupChannel.sendUserMessage(message)
    }

    fun getConnectionHandler(): LiveData<ConnectionStatus> {
        requireConnected()
        return connectionHandler
    }

    private fun requireConnected() {
        require(isChannelConnected()) {
            "Jirah chat is not initialized"
        }
    }

    fun getChannelHandler(): LiveData<ChannelEvent> {
        requireConnected()
        return channelEventHandler
    }

    fun getPagedMessages(): LiveData<PagedList<JirahChatUserMessage>> {
        return pagedUserMessages.pagedList
    }

    fun saveMessage(chatUserMessage: JirahChatUserMessage) {
        uiScope.launch {
            chatRepository.saveMessage(chatUserMessage)
        }
    }

    fun updateMessage(chatUserMessage: JirahChatUserMessage) {
        uiScope.launch {
            chatRepository.updateMessage(chatUserMessage)
        }
    }

    fun updateTempMessage(chatUserMessage: JirahChatUserMessage) {
        uiScope.launch {
            chatRepository.updateTempMessage(chatUserMessage)
        }
    }

    fun deleteMessage(chatUserMessage: JirahChatUserMessage) {
        uiScope.launch { chatRepository.deleteMessage(chatUserMessage) }
    }

    fun markChannelAsRead() {
        if (isChannelConnected()) {
            groupChannel.markAsRead()
        }
    }

    fun refresh() {
        pagedUserMessages.refresh()
    }

    private fun isChannelConnected() = this::groupChannel.isInitialized

    private fun requireChannelConnected() = require(isChannelConnected()) {
        "Channel must be initialized"
    }

    fun setTypingStatus(isTyping: Boolean) {
        if (isChannelConnected()) {
            groupChannel.setTypingStatus(isTyping)
        }
    }
}
