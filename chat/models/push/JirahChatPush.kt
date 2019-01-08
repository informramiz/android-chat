package com.jirah.sitterapp.chat.models.push

import com.google.gson.annotations.SerializedName

data class JirahChatPush(
        @field:SerializedName("app_id")
        val appId: String,
        val audience_type: String,
        val category: String,
        val channel: Channel,
        @field:SerializedName("channel_type")
        val channelType: String,
        val created_at: Long,
        @field:SerializedName("custom_type")
        val customType: String,
        @field:SerializedName("data")
        val ddata: String,
        val files: List<Any>,
        val mentioned_users: List<Any>,
        val message: String,
        val message_id: Int,
        val push_alert: String,
        val push_sound: String,
        val recipient: Recipient,
        val sender: Sender,
        val translations: Translations,
        val type: String,
        val unread_message_count: Int
)