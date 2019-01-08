package com.jirah.sitterapp.chat.repository.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.jirah.sitterapp.chat.models.JirahChatDbMessage


/**
 * Created by Ramiz Raja on 30/12/2018.
 */
@Database(entities = [JirahChatDbMessage::class], version = 1)
abstract class JirahChatDatabase : RoomDatabase() {
    companion object {
        const val NAME = "jirah-chat.db"
        @JvmStatic
        private var sInstance: JirahChatDatabase? = null

        @JvmStatic
        fun init(context: Context) {
            if (sInstance == null) {
                sInstance = Room.databaseBuilder(context, JirahChatDatabase::class.java, NAME)
                        .fallbackToDestructiveMigration()
                        .build()
            }
        }

        @JvmStatic
        fun get(): JirahChatDatabase {
            require(sInstance != null) {
                "Database is not initialized"
            }
            return sInstance!!
        }
    }

    abstract fun chatDao(): JirahChatDao
}