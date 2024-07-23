package com.example.chatapplication

data class User(
    var name: String,
    var phone: String,
    var email: String,
    var uId: String,
    var font: Font
){
    constructor(): this("", "", "", "", Font(0, ""))
}

data class Font(
    var font_size: Long,
    val font_style: String
) {
    constructor(): this(0, "")
}