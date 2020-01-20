package com.hujiejeff.musicplayer.util

import android.content.ContentUris
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.BaseColumns
import android.provider.MediaStore
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.data.entity.Album
import com.hujiejeff.musicplayer.data.entity.Artist
import com.hujiejeff.musicplayer.data.entity.Music
import java.lang.Exception

private val musicUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
private val albumUri = MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI
private val artistUri = MediaStore.Audio.Artists.EXTERNAL_CONTENT_URI


private const val _ID = BaseColumns._ID
private const val IS_MUSIC = MediaStore.Audio.AudioColumns.IS_MUSIC
private const val TITLE = MediaStore.Audio.AudioColumns.TITLE
private const val ARTIST = MediaStore.Audio.AudioColumns.ARTIST
private const val ALBUM = MediaStore.Audio.AudioColumns.ALBUM
private const val ALBUM_ID = MediaStore.Audio.AudioColumns.ALBUM_ID
private const val DATA = MediaStore.Audio.AudioColumns.DATA
private const val DISPLAY_NAME = MediaStore.Audio.AudioColumns.DISPLAY_NAME
private const val DURATION = MediaStore.Audio.AudioColumns.DURATION
private const val SIZE = MediaStore.Audio.AudioColumns.SIZE
private const val SORT_ORDER = MediaStore.Audio.Media.DEFAULT_SORT_ORDER

fun getMusicList(): MutableList<Music> {
    val musicList = mutableListOf<Music>()
    val cursor = queryAll(
        musicUri, arrayOf(
            _ID,
            IS_MUSIC,
            TITLE,
            ARTIST,
            ALBUM,
            ALBUM_ID,
            DATA,
            DISPLAY_NAME,
            DURATION, SIZE
        )
    ) ?: return musicList
    while (cursor.moveToNext()) {
        cursor.apply {
            val title = getString(cursor.getColumnIndex(TITLE))
            val artist = getString(cursor.getColumnIndex(ARTIST))
            val album = getString(cursor.getColumnIndex(ALBUM))
            val path = getString(cursor.getColumnIndex(DATA))
            val displayName = getString(cursor.getColumnIndex(DISPLAY_NAME))
            val albumId = getLong(cursor.getColumnIndex(ALBUM_ID))
            val duration = getLong(cursor.getColumnIndex(DURATION))
            val size = getLong(cursor.getColumnIndex(SIZE))
            musicList.add(Music(artist, displayName, path, albumId, album, title, duration, size))
        }
    }
    cursor.close()
    logD("getMusicList$musicList")
    return musicList
}


fun getAlbumList(): MutableList<Album> {
    val albumList = mutableListOf<Album>()
    val cursor = queryAll(albumUri, arrayOf(ALBUM, _ID, ARTIST)) ?: return albumList
    while (cursor.moveToNext()) {
        cursor.apply {
            val albumId = getLong(cursor.getColumnIndex(_ID))
            val albumTitle = getString(cursor.getColumnIndex(ALBUM))
            val albumArtist = getString(cursor.getColumnIndex(ARTIST))
            albumList.add(Album(albumId, albumTitle, albumArtist))
        }
    }
    cursor.close()
    return albumList
}

fun getArtistList(): MutableList<Artist> {
    val artistList = mutableListOf<Artist>()
    val cursor = queryAll(artistUri, arrayOf(ARTIST, _ID)) ?: return artistList
    while (cursor.moveToNext()) {
        cursor.apply {
            val name = getString(cursor.getColumnIndex(ARTIST))
            val id = getLong(cursor.getColumnIndex(_ID))
            artistList.add(Artist(name, id))
        }
    }
    cursor.close()
    return artistList
}

fun ImageView.loadCover(albumID: Long) {
    val artUri = Uri.parse("content://media/external/audio/albumart")
    val uri = ContentUris.withAppendedId(artUri, albumID)
    Glide.with(context)
        .load(uri)
        .placeholder(R.drawable.default_cover)
        .error(R.drawable.default_cover)
        .into(this)
}

fun ImageView.loadPlayListCover(url: String) {
    Glide.with(context)
        .load(url)
        .placeholder(R.drawable.default_cover)
        .error(R.drawable.default_cover)
        .into(this)
}

fun getCover(albumID: Long): Bitmap {
    val artUri = Uri.parse("content://media/external/audio/albumart")
    val uri = ContentUris.withAppendedId(artUri, albumID)
        try {
        val bitmap =
            BitmapFactory.decodeStream(App.appContext.contentResolver.openInputStream(uri))//也许没封面
        if (bitmap != null) {
            return bitmap
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return BitmapFactory.decodeResource(App.appContext.resources, R.drawable.default_cover)
}

fun getArtistCover(): Bitmap =
    BitmapFactory.decodeResource(App.appContext.resources, R.drawable.default_artist_art)


fun queryAll(uri: Uri, projection: Array<String>): Cursor? = App.appContext.contentResolver.query(
    uri, projection, null, null, null
)


fun getBitmap() {

}