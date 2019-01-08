package com.jirah.sitterapp.chat.sdk.prefs

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.jirah.sitterapp.R

/**
 * Created by Ramiz Raja on 08/01/2019
 */
internal class PreferencesHelper private constructor(private val sharedPreferences: SharedPreferences) {
    companion object {
        private const val PREF_NAME = "jirah-chat-prefs"
        private const val PUSH_TOKEN_PREF_KEY = "jirah-chat-fcm-push-token"

        private var sInstance: PreferencesHelper? = null

        fun init(context: Context) {
            if (sInstance == null) {
                sInstance = PreferencesHelper(context.getSharedPreferences(
                        PREF_NAME, Context.MODE_PRIVATE))
            }
        }

        fun get(): PreferencesHelper {
            require(sInstance != null) {
                "PreferencesHelper is not initialized yet"
            }
            return sInstance!!
        }
    }

    fun setPushToken(token: String) {
        sharedPreferences.edit {
            putString(PUSH_TOKEN_PREF_KEY, token)
        }
    }

    fun getPushToken(): String? {
        return sharedPreferences.getString(
                PUSH_TOKEN_PREF_KEY,
                null)
    }

    fun clear() {
        sharedPreferences.edit {
            clear()
        }
    }
}