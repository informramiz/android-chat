package com.jirah.sitterapp.chat.repository.paging

import com.jirah.sitterapp.data.utils.Status

/**
 * Created by Ramiz Raja on 24/10/2018
 */
data class MessagePagingState(val status: Status,
                       val msg: String? = null) {
    companion object {
        val LOADING = MessagePagingState(Status.LOADING)
        val LOADED = MessagePagingState(Status.SUCCESS)
        fun error(msg: String?) = MessagePagingState(Status.ERROR, msg)
    }
}