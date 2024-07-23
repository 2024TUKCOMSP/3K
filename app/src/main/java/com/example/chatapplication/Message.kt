package com.example.chatapplication

import com.google.android.gms.common.internal.Objects
import com.google.firebase.Timestamp

data class Message(
    val message: String?,
    var sendId: String?,
    var timestamp: String?
){
    constructor(): this("", "", "")
}
