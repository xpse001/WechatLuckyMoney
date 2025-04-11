package com.example.myapplication.utils

import android.content.SharedPreferences

// ConfigManager.kt
class ConfigManager(private val sharedPreferences: SharedPreferences) {


    fun getConfig(key: String) {
            

    }

    // 延迟时间（单位：毫秒）
    var delayMs: Int
        get() = sharedPreferences.getInt("delayMs", 1000) // 默认值1000ms
        set(value) = sharedPreferences.edit().putInt("delayMs", value).apply()

    // 红包开关
    var isRedPacketEnabled: Boolean
        get() = sharedPreferences.getBoolean("redPacketEnabled", true) // 默认关闭
        set(value) = sharedPreferences.edit().putBoolean("redPacketEnabled", value).apply()
}