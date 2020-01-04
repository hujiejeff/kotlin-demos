package com.hujiejeff.musicplayer.data.entity

enum class PlayMode(private val _value: Int) {
    SINGLE(0), LOOP(1), SINGLE_LOOP(2), SHUFFLE(3);

    val value: Int get() = _value

    companion object {
        fun valueOf(value: Int) = when (value) {
            0 -> SINGLE
            1 -> LOOP
            2 -> SINGLE_LOOP
            3 -> SHUFFLE
            else -> LOOP
        }
    }
}

fun Int.toPlayMode() = PlayMode.valueOf(this)