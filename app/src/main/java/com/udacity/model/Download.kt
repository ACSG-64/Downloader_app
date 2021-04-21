package com.udacity.model

data class Download(
    val name : String = "NONE",
    var fullName : String = "NONE",
    val url: String = "NONE",
    var status : Boolean = false
)
