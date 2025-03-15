package com.pettermahlen.sikp13

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform