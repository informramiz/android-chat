package com.jirah.sitterapp.chat.repository.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.jirah.sitterapp.chat.repository.chatapi.JirahChatApi
import com.jirah.sitterapp.chat.sdk.utils.ErrorResponse
import com.jirah.sitterapp.chat.sdk.utils.Response
import com.jirah.sitterapp.chat.sdk.utils.SuccessResponse
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * Created by Ramiz Raja on 31/12/2018
 */
class MessagesBoundaryCallback(private val uiScope: CoroutineScope,
                               private val jirahChatApi: JirahChatApi,
                               private val handleResponse: suspend (List<JirahChatUserMessage>) -> Unit) : PagedList.BoundaryCallback<JirahChatUserMessage>() {
    val refreshState = MutableLiveData<MessagePagingState>()
    val loadBeforePagingState = MutableLiveData<MessagePagingState>()
    val loadAfterPagingState = MutableLiveData<MessagePagingState>()

    override fun onZeroItemsLoaded() {
        Timber.d("onZeroItemsLoaded called")
        refreshState.value = MessagePagingState.LOADING
        uiScope.launch(context = Dispatchers.IO) {
            val response = jirahChatApi.getInitialItems()
            Timber.d("getInitialItems response $response")
            handleResponse(response, refreshState)
        }
    }

    override fun onItemAtEndLoaded(itemAtEnd: JirahChatUserMessage) {
        Timber.d("onItemAtEndLoaded called")
        loadAfterPagingState.value = MessagePagingState.LOADING
        uiScope.launch(context = Dispatchers.IO) {
            val response = jirahChatApi.getFutureMessages(itemAtEnd.createdAt)
            Timber.d("getFutureMessages: $response")
            handleResponse(response, loadAfterPagingState)
        }
    }

    override fun onItemAtFrontLoaded(itemAtFront: JirahChatUserMessage) {
        Timber.d("onItemAtFrontLoaded called")
        loadBeforePagingState.value = MessagePagingState.LOADING
        uiScope.launch(context = Dispatchers.IO) {
            val response = jirahChatApi.getPastMessages(itemAtFront.createdAt)
            Timber.d("getPastMessages: $response")
            handleResponse(response, loadAfterPagingState)
        }
    }

    private suspend fun handleResponse(response: Response<List<JirahChatUserMessage>>,
                                       pagingStateHandler: MutableLiveData<MessagePagingState>) {
        when (response) {
            is SuccessResponse -> {
                handleResponse(response.data!!)
                pagingStateHandler.postValue(MessagePagingState.LOADED)
            }
            is ErrorResponse -> {
                pagingStateHandler.postValue(MessagePagingState.error(response.errorMessage))
            }
        }
    }
}