package com.hujiejeff.musicplayer.component

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.constans.EXTRA_NOTIFICATION
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.HomeActivity
import com.hujiejeff.musicplayer.service.PlayService
import com.hujiejeff.musicplayer.util.getCover
import com.hujiejeff.musicplayer.util.getLocalCoverUrl

/**
 * Create by hujie on 2019/12/31
 */
class Notifier {
    companion object {
        private var INSTANCE: Notifier? = null
        private const val NOTIFICATION_ID = 666
        private const val CHANNEL_ID = "channel_1"
        private const val CHANNEL_NAME = "music"
        @JvmStatic
        fun getInstance(): Notifier =
            INSTANCE ?: synchronized(Notifier::class.java) {
                INSTANCE ?: Notifier().also {
                    INSTANCE = it
                }
            }
    }

    private lateinit var mPlayService: PlayService
    private lateinit var notificationManager: NotificationManager

    //初始化
    fun init(playService: PlayService) {
        mPlayService = playService
        notificationManager =
            playService.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                enableVibration(false)
                vibrationPattern = longArrayOf(0L)
                notificationManager.createNotificationChannel(this)
            }
        }
    }

    //发送播放的通知
    fun showPlay(music: Music) {
        mPlayService.startForeground(NOTIFICATION_ID, buildNotification(mPlayService, music, true))
    }

    //停止播放通知
    fun showPause(music: Music) {
        mPlayService.stopForeground(false)
        notificationManager.notify(NOTIFICATION_ID, buildNotification(mPlayService, music, false))
    }

    //构建通知
    private fun buildNotification(
        context: Context,
        music: Music,
        isPlaying: Boolean
    ): Notification {
        val intent = Intent(context, HomeActivity::class.java).apply {
            action = Intent.ACTION_VIEW
            putExtra(EXTRA_NOTIFICATION, true)
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pi = PendingIntent.getActivity(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        return NotificationCompat
            .Builder(context, CHANNEL_ID)
            .setContentIntent(pi)
            .setSmallIcon(R.drawable.ic_notification)
            .setCustomContentView(getRemoteViews(context, music, isPlaying))
            .build()
    }

    //构建remote view
    private fun getRemoteViews(context: Context, music: Music, isPlaying: Boolean): RemoteViews {
        val remoteViews = RemoteViews(mPlayService.packageName, R.layout.notification)
        music.apply {
            remoteViews.apply {
                //UI信息
                val src: String = if (music.type == 1) {
                    coverSrc!!
                } else {
                    getLocalCoverUrl(albumID)
                }
                Glide.with(context).asBitmap().load(coverSrc).into(object : CustomTarget<Bitmap>() {
                    override fun onLoadCleared(placeholder: Drawable?) {
                    }

                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        setImageViewBitmap(R.id.iv_status_bar_cover, resource)
                    }
                })
                setTextViewText(R.id.tv_status_bar_title, title)
                setTextViewText(R.id.tv_status_bar_artist, artist)
                setImageViewResource(R.id.iv_status_bar_play, getPlayIconRes(isPlaying))
                //暂停或播放意图
                buildRemoteViewsClickEvent(
                    context,
                    NotifyBarReceiver.EXTRA_PLAY_PAUSE,
                    0,
                    R.id.iv_status_bar_play
                )
                //下一首意图
                buildRemoteViewsClickEvent(
                    context,
                    NotifyBarReceiver.EXTRA_NEXT,
                    1,
                    R.id.iv_status_bar_next
                )
            }
        }
        return remoteViews
    }

    //获取播放或暂停icon
    private fun getPlayIconRes(isPlaying: Boolean) =
        if (!isPlaying) R.drawable.status_bar_play_selector else R.drawable.status_bar_pause_selector

    private fun RemoteViews.buildRemoteViewsClickEvent(
        context: Context,
        extraAction: String,
        requestCode: Int,
        resId: Int
    ) {
        Intent(NotifyBarReceiver.ACTION_STATUS_BAR).apply {
            putExtra(NotifyBarReceiver.EXTRA, extraAction)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                this,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            setOnClickPendingIntent(resId, pendingIntent)
        }
    }
}