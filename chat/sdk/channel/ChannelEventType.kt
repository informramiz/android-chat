package com.jirah.sitterapp.chat.sdk.channel


/**
 * Created by Ramiz Raja on 24/12/2018.
 */
enum class ChannelEventType(val value: Int) {
    MESSAGE_RECEIVED(0),
    MESSAGE_UPDATED(1),
    MESSAGE_DELETED(2),
    READ_RECEIPT_UPDATED(3),
    TYPING_STATUS_UPDATED(4)
}