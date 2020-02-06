package com.hujiejeff.musicplayer.data.entity

data class Music(
    val id: Long,
    val type: Int,
    val artist: String,
    val fileName: String,
    var filePath: String?,
    val albumID: Long,
    val album: String,
    val title: String,
    var duration: Long,
    var fileSize: Long,
    var coverSrc: String? = null
)