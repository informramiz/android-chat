package com.jirah.sitterapp.chat.sdk

import android.content.Context
import com.jirah.sitterapp.chat.repository.db.JirahChatDatabase
import com.jirah.sitterapp.chat.sdk.channel.ChannelEventHandler
import com.jirah.sitterapp.chat.sdk.connection.ConnectionHandler
import com.jirah.sitterapp.chat.sdk.prefs.PreferencesHelper
import com.jirah.sitterapp.chat.sdk.push.JirahChatNotificationHandler
import com.jirah.sitterapp.chat.sdk.utils.ErrorResponse
import com.jirah.sitterapp.chat.sdk.utils.Response
import com.jirah.sitterapp.chat.sdk.utils.SuccessResponse
import com.sendbird.android.SendBird
import com.sendbird.android.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Created by Ramiz Raja on 24/12/2018.
 */
class JirahChat {
    companion object {
        private var currentUserId: String? = null
        private lateinit var appContext: Context

        fun init(context: Context, appId: String) {
            appContext = context
            SendBird.init(appId, context)
            JirahChatDatabase.init(context)
            PreferencesHelper.init(context)
        }

        suspend fun connect(userId: String): Response<User> {
            currentUserId = userId
            return suspendCoroutine { continuation ->
                SendBird.connect(userId) { user, sendBirdException ->
                    continuation.resume(Response.create(user, sendBirdException))
                }
            }
        }

        suspend fun disconnect(): Response<Unit> {
            return suspendCoroutine { continuation ->
                SendBird.disconnect {
                    continuation.resume(SuccessResponse())
                }
            }
        }

        fun addConnectionHandler(userId: String,
                                 handlerId: String): ConnectionHandler {
            currentUserId = userId
            return ConnectionHandler(userId, handlerId)
        }

        fun addChannelEventHandler(channelHandlerId: String): ChannelEventHandler {
            return ChannelEventHandler(channelHandlerId)
        }

        fun isConnected() = SendBird.getCurrentUser() != null

        fun getCurrentUserId(): String {
            return currentUserId ?: SendBird.getCurrentUser().userId
        }

        fun registerPushToken(token: String, callback: (Response<Boolean>) -> Unit) {
            SendBird.registerPushTokenForCurrentUser(token) { status, sendBirdException ->
                if (sendBirdException == null) {
                    PreferencesHelper.get().setPushToken(token)
                    callback(SuccessResponse(true))
                } else {
                    callback(ErrorResponse(false, sendBirdException.localizedMessage))
                }
            }
        }

        suspend fun registerPushToken(token: String): Response<Boolean> {
            return suspendCoroutine { continuation ->
                registerPushToken(token) { response ->
                    continuation.resume(response)
                }
            }
        }

        fun unregisterPushToken(token: String, callback: (Response<Boolean>) -> Unit) {
            SendBird.unregisterPushTokenForCurrentUser(token) { sendBirdException ->
                if (sendBirdException == null) {
                    callback(SuccessResponse(true))
                } else {
                    callback(ErrorResponse(false, sendBirdException.localizedMessage))
                }
            }
        }

        suspend fun unregisterPushToken(token: String): Response<Boolean> {
            return suspendCoroutine { continuation ->
                unregisterPushToken(token) { response ->
                    continuation.resume(response)
                }
            }
        }

        suspend fun updateCurrentUserInfo(name: String, profileUrl: String?): Response<Boolean> {
            return suspendCoroutine { continuation ->
                SendBird.updateCurrentUserInfo(name, profileUrl) {sendBirdException ->
                    continuation.resume(Response.create(null, sendBirdException))
                }
            }
        }

        fun logout() {
            CoroutineScope(Dispatchers.IO).launch {
                JirahChatDatabase.get().chatDao().deleteAll()
                JirahChat.connect(currentUserId!!)
                PreferencesHelper.get().getPushToken()?.let {
                    JirahChat.unregisterPushToken(it)
                }
                JirahChat.disconnect()
                PreferencesHelper.get().clear()
                JirahChatNotificationHandler.clearChatNotifications(appContext)
                currentUserId = null
            }
        }
    }
}