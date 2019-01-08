package com.jirah.sitterapp.chat.sdk.connection


/**
 * Created by Ramiz Raja on 24/12/2018.
 */
enum class ConnectionStatus(val value: Int) {
    FAILED(-1),
    STARTED(0),
    CONNECTED(1)
}