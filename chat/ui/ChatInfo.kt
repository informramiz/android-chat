package com.jirah.sitterapp.chat.ui

import android.os.Parcel
import android.os.Parcelable


/**
 * Created by Ramiz Raja on 01/01/2019.
 */
data class ChatInfo(var senderId: String,
                    var receiverId: String,
                    var receiverName: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(senderId)
        parcel.writeString(receiverId)
        parcel.writeString(receiverName)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ChatInfo> {
        override fun createFromParcel(parcel: Parcel): ChatInfo {
            return ChatInfo(parcel)
        }

        override fun newArray(size: Int): Array<ChatInfo?> {
            return arrayOfNulls(size)
        }
    }
}