package com.hujiejeff.musicplayer.entity

data class Music(
    val artist: String,
    val fileName: String,
    val filePath: String,
    val albumID: Long,
    val album: String,
    val title: String,
    val duration: Long,
    val fileSize: Long
)