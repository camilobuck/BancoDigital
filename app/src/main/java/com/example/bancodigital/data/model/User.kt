package com.example.bancodigital.data.model

import com.google.firebase.database.Exclude

data class User(
    val id: String = "",
    var name: String = "",
    val email: String = "",
    var phone: String = "",
    var image: String = "",
    @get:Exclude
    val password: String = ""

)
