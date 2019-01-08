package com.jirah.sitterapp.chat.repository.db

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.*
import com.jirah.sitterapp.chat.models.JirahChatDbMessage


/**
 * Created by Ramiz Raja on 30/12/2018.
 */
@Dao
abstract class JirahChatDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMessage(message: JirahChatDbMessage)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertMessages(messages: List<JirahChatDbMessage>)

    @Transaction
    open fun updateMessages(messages: List<JirahChatDbMessage>) {
        deleteAllMessages()
        insertMessages(messages)
    }

    @Delete
    abstract fun delete(message: JirahChatDbMessage)

    @Query("DELETE FROM JirahChatDbMessage WHERE messageId = 0")
    abstract fun deleteTempMessage()

    @Transaction
    open fun updateTempMessage(message: JirahChatDbMessage) {
        deleteTempMessage()
        insertMessage(message)
    }

    @Transaction
    @Query("DELETE FROM JirahChatDbMessage")
    abstract fun deleteAll()

    @Delete
    abstract fun deleteAllMessages(vararg messages: JirahChatDbMessage)

    @Query("SELECT * FROM JirahChatDbMessage ORDER BY createdAt LIMIT 1")
    abstract fun loadOldestMessage(): LiveData<JirahChatDbMessage>

    @Query("SELECT * FROM JirahChatDbMessage ORDER BY createdAt")
    abstract fun loadAllMessages(): LiveData<List<JirahChatDbMessage>>

    @Query("SELECT * FROM JirahChatDbMessage ORDER BY createdAt LIMIT :count")
    abstract fun loadRecentMessages(count: Int = 30): LiveData<List<JirahChatDbMessage>>

    @Query("SELECT * FROM JirahChatDbMessage WHERE channelUrl = :channelUrl ORDER BY createdAt")
    abstract fun loadMessagesDataSourceFactory(channelUrl: String): DataSource.Factory<Int, JirahChatDbMessage>
}