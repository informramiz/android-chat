package com.jirah.sitterapp.chat.sdk.utils

import com.sendbird.android.SendBirdException


/**
 * Created by Ramiz Raja on 24/12/2018.
 */
sealed class Response<out T>() {
    companion object {
        fun <T> create(data: T?, exception: SendBirdException?): Response<T> {
            if (exception == null) {
                return SuccessResponse<T>(data)
            } else {
                return ErrorResponse(data, exception.localizedMessage)
            }
        }
    }
}

class SuccessResponse<T>(val data: T? = null) : Response<T>()

class ErrorResponse<T>(val data: T? = null, val errorMessage: String) : Response<T>()