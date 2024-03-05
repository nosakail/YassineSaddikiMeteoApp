package com.example.yassinesaddikimeteoapp

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform