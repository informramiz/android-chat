package com.jirah.sitterapp.chat.sdk.connection

import androidx.lifecycle.*
import com.sendbird.android.SendBird
import timber.log.Timber


/**
 * Created by Ramiz Raja on 24/12/2018.
 */
class ConnectionHandler(
        private val userId: String,
        private val handlerId: String) : LiveData<ConnectionStatus>(), SendBird.ConnectionHandler {

    override fun onReconnectStarted() {
        this.value = ConnectionStatus.STARTED
    }

    override fun onReconnectSucceeded() {
        this.value = ConnectionStatus.CONNECTED
    }

    override fun onReconnectFailed() {
        this.value = ConnectionStatus.FAILED
    }

    override fun onActive() {
        SendBird.addConnectionHandler(handlerId, this)
        verifyConnection()
        Timber.d("Add sendbird connection handler")
    }

    override fun onInactive() {
        SendBird.disconnect {  }
        SendBird.removeConnectionHandler(handlerId)
        Timber.d("Remove sendbird connection handler")
    }

    private fun verifyConnection() {
        when (SendBird.getConnectionState()) {
            SendBird.ConnectionState.OPEN -> this.value = ConnectionStatus.CONNECTED
            SendBird.ConnectionState.CLOSED -> SendBird.connect(userId) { user, sendBirdException ->
                if (sendBirdException == null) {
                    this.value = ConnectionStatus.CONNECTED
                }
            }
        }
    }
}