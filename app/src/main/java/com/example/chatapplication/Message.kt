package com.example.chatapplication

import com.google.firebase.Timestamp

data class Message(
    val message: String?,
    var sendId: String?,
    var timestamp: String?
){
    constructor(): this("", "", "")
}
