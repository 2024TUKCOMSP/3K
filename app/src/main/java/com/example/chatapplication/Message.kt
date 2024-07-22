package com.example.chatapplication

data class Message(
    val message: String?,
    var sendId: String?
){
    constructor(): this("", "")
}
