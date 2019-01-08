package com.jirah.sitterapp.chat.sdk.push

import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.jirah.sitterapp.chat.models.push.JirahChatPush

/**
 * Created by Ramiz Raja on 08/01/2019
 */
private const val PAYLOAD_KEY = "sendbird"
private const val CHANNEL_KEY = "channel"
private const val CHANNEL_URL_KEY = "channel_url"


fun RemoteMessage.isJirahChatPush(): Boolean {
    return data.containsKey(PAYLOAD_KEY)
}

private fun RemoteMessage.requireJirahChatPushType() {
    require(isJirahChatPush()) {
        "Provided remote message is not of Jirah Chat type"
    }
}

fun RemoteMessage.getJirahChatPayload(): JirahChatPush {
    requireJirahChatPushType()
    val payload = data[PAYLOAD_KEY]!!
    return Gson().fromJson(payload, JirahChatPush::class.java)
}