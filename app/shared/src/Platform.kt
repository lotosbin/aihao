package com.yuanjingtech.aihao

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
