package com.hujiejeff.musicplayer.data

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager

object Preference {
    private const val PLAY_MODE = "play_mode"
    private const val PLAY_POSITION = "play_position"
    private const val PLAY_PROGRESS = "play_progress"
    private const val COOKIES = "cookies"
    private const val SEARCH_HISTORY = "search_history"
    private lateinit var sharedPreferences: SharedPreferences
    fun init(context: Context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
    }

    var play_mode
        set(value) = setInt(
            PLAY_MODE,
            value
        )
        get() = getInt(PLAY_MODE)
    var play_position
        set(value) = setInt(
            PLAY_POSITION,
            value
        )
        get() = getInt(PLAY_POSITION)
    var play_progress
        set(value) = setInt(
            PLAY_PROGRESS,
            value
        )
        get() = getInt(PLAY_PROGRESS)

    var cookies: Set<String>
        set(value) = setStringSet(COOKIES, value)
        get() = getStringSet(COOKIES, setOf())

    var searchHistory: Set<String>
        set(value) = setStringSet(SEARCH_HISTORY, value)
        get() = getStringSet(SEARCH_HISTORY, setOf())

    private fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    private fun getString(key: String, defValue: String = "empty") =
        sharedPreferences.getString(key, defValue)

    private fun setInt(key: String, value: Int) {
        sharedPreferences.edit().putInt(key, value).apply()
    }

    private fun getInt(key: String, defValue: Int = 0) = sharedPreferences.getInt(key, defValue)

    private fun setLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

    private fun getLong(key: String, defValue: Long = 0) = sharedPreferences.getLong(key, defValue)

    private fun setStringSet(key: String, value: Set<String>) {
        sharedPreferences.edit().putStringSet(key, value).apply()
    }
    private fun getStringSet(key: String, defValue: Set<String>): Set<String>  =
        sharedPreferences.getStringSet(key, defValue)!!
}
