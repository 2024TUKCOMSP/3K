package com.example.chatapplication

data class User(
    var name: String,
    var email: String,
    var uId: String
){
    constructor(): this("", "", "")
}