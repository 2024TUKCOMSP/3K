package com.example.chatapplication

data class User(
    var name: String,
    var email: String,
    var uId: String,
    var font_size: Long,
    val font: String
){
    constructor(): this("", "", "", 0, "")
}