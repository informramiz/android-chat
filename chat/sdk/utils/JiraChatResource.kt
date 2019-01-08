package com.jirah.sitterapp.chat.sdk.utils

import com.jirah.sitterapp.data.utils.Status
import java.lang.Exception


/**
 * Created by Ramiz Raja on 28/12/2018.
 */

data class JiraChatResource<out T>(val status: Status,
                                   val data: T?,
                                   val message: String?) {
    companion object {
        fun <T> success(data: T?): JiraChatResource<T> {
            return JiraChatResource(Status.SUCCESS, data, null)
        }

        fun <T> error(data: T?, message: String?): JiraChatResource<T> {
            return JiraChatResource(Status.ERROR, data, message)
        }

        fun <T> loading(data: T?): JiraChatResource<T> {
            return JiraChatResource(Status.LOADING, data, null)
        }

        fun <T> create(data: T?, exception: Exception?): JiraChatResource<T> {
            return if (exception == null) {
                success(data)
            } else {
                error(data, exception.localizedMessage)
            }
        }
    }
}