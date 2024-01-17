package com.example.trackwalletapp.model

import com.google.firebase.database.PropertyName

data class User(
    @get:PropertyName("email")
    val email: String? = "",

    @get:PropertyName("name")
    val name: String? = "",

    @get:PropertyName("password")
    val password: String? = "",

    @get:PropertyName("username")
    val username: String? = ""
) {
    // Add a no-argument constructor
    constructor() : this("", "", "", "")
}

