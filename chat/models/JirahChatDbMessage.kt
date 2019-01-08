package com.jirah.sitterapp.chat.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jirah.sitterapp.chat.sdk.message.JirahChatUserMessage

/**
 * Created by Ramiz Raja on 22/12/2018
 */
@Entity
data class JirahChatDbMessage(
        @PrimaryKey
        var messageId: Long,
        var channelUrl: String,
        var createdAt: Long,
        var data: ByteArray
)

fun JirahChatUserMessage.toDbMessage(): JirahChatDbMessage {
    return JirahChatDbMessage(messageId, channelUrl, createdAt, serialize())
}