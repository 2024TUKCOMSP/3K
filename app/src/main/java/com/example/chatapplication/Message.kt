package com.example.chatapplication

import com.google.android.gms.common.internal.Objects
import com.google.firebase.database.ServerValue

data class Message(
    val message: String?,
    var sendId: String?,
    val timestamp: Any
){
    constructor(): this("", "", ServerValue.TIMESTAMP)
}
