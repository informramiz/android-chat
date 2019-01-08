package com.jirah.sitterapp.chat.models


/**
 * Created by Ramiz Raja on 23/12/2018.
 */
enum class ChatMessageSentStatus(val value: Int) {
    PENDING(0),
    SENT(1),
    FAILED(2)
}