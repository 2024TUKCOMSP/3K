package com.example.chatapplication

import com.google.android.gms.common.internal.Objects
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ServerValue

data class Message(
    val message: String?,
    var sendId: String?,
    val timestamp: Any,
    var readIndicator: Int
){
    constructor(): this("", "", ServerValue.TIMESTAMP, 1)
}
