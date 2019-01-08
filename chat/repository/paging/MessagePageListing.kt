package com.jirah.sitterapp.chat.repository.paging

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

/**
 * Created by Ramiz Raja on 24/10/2018
 */
data class MessagePageListing<T>(
        val pagedList: LiveData<PagedList<T>>,
        val loadAfterState: LiveData<MessagePagingState>,
        val loadBeforeState: LiveData<MessagePagingState>,
        val refreshState: LiveData<MessagePagingState>,
        val refresh: () -> Unit,
        val retry: () -> Unit)