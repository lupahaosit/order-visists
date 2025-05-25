package com.example.schoolmarksproject.Models

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class Visit {
    var name: String = ""
    var email: String = ""
    var visit by mutableStateOf("")

    constructor(name: String, visit: String, email: String) {
        this.name = name
        this.visit = visit
        this.email = email
    }

    constructor()
}