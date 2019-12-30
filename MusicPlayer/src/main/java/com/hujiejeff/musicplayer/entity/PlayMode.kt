package com.hujiejeff.musicplayer.entity

enum class PlayMode(val value: Int) {
    SINGLE(0), LOOP(1), SINGLE_LOOP(2), SHUFFLE(3);
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