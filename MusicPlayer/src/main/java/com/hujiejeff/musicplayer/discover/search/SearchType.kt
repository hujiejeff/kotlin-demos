package com.hujiejeff.musicplayer.discover.search

/**
 * Create by hujie on 2020/3/9
 */
enum class SearchType(private val _value: Int) {
    SONG(1), VIDEO(1004), ARTIST(100), ALBUM(10), PLAYLIST(1000), USER(1002);

    val value: Int get() = _value
    companion object {
        fun valueOf(value: Int) = when (value) {
            1 -> SONG
            1004 -> VIDEO
            100 -> ARTIST
            10 -> ALBUM
            1000 -> PLAYLIST
            1002 -> USER
            else -> SONG
        }
    }
}

fun Int.SearchType() = SearchType.valueOf(this)