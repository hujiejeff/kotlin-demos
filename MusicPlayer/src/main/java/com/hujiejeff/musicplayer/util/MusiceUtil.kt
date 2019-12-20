package com.hujiejeff.musicplayer.util

import com.hujiejeff.musicplayer.entity.Music

fun getMusics(): List<Music> {
    val musicList: MutableList<Music> = mutableListOf()
    val music = Music("a", "b", "c", 1, "d", "e", 20, 20)
    musicList.add(music)
    return musicList
}