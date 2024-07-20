package com.example.chatapplication

import android.net.Uri

data class User(
    var name: String,
    var email: String,
    var uId: String,
    var font: Font,
    var ImageUrl: String
){
    constructor(): this("", "", "", Font(0, ""), "")
}

data class Font(
    var font_size: Long,
    val font_style: String
) {
    constructor(): this(0, "")
}