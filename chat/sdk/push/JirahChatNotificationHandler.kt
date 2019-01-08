package com.jirah.sitterapp.chat.sdk.push

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import com.jirah.sitterapp.R
import com.jirah.sitterapp.chat.models.push.JirahChatPush

/**
 * Created by Ramiz Raja on 08/01/2019
 */
class JirahChatNotificationHandler {
    companion object {
        const val CHAT_CHANNEL_ID = "com.jirah.sitter.chat_notification_channel_id"
        const val CHAT_NOTIFICATION_ID = 101
        fun showNotification(context: Context, chatPush: JirahChatPush) {
            createNotificationChannel(context)
            val person = Person.Builder()
                    .setName(chatPush.sender.name)
                    .setKey(chatPush.sender.id)
                    .setImportant(true)
                    .build()

            val notification = NotificationCompat.Builder(context, CHAT_CHANNEL_ID)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.ic_notification_chat)
                    .setCategory(Notification.CATEGORY_MESSAGE)
                    .setContentTitle(chatPush.sender.name)
                    .setContentText(chatPush.message)
                    .setStyle(NotificationCompat.MessagingStyle(person)
                            .addMessage(chatPush.message, chatPush.created_at, person)
                    )
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

            with(NotificationManagerCompat.from(context)) {
                notify(CHAT_NOTIFICATION_ID, notification)
            }
        }

        private fun createNotificationChannel(context: Context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val name = context.getString(R.string.fcm_default_notification_channel_id)
                val descriptionText = context.getString(R.string.fcm_default_notification_channel_description)
                val importance = NotificationManager.IMPORTANCE_HIGH

                val notificationChannel = NotificationChannel(CHAT_CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                }

                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(notificationChannel)
            }
        }

        fun clearChatNotifications(context: Context) {
            with(NotificationManagerCompat.from(context)) {
                cancel(CHAT_NOTIFICATION_ID)
            }
        }
    }
}