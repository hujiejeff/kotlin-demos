package com.hujiejeff.musicplayer.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.hujiejeff.musicplayer.OnPlayerEventListener
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.entity.Music
import com.hujiejeff.musicplayer.entity.PlayMode
import com.hujiejeff.musicplayer.service.AudioPlayer
import com.hujiejeff.musicplayer.storage.Preference
import com.hujiejeff.musicplayer.util.getCover
import com.hujiejeff.musicplayer.util.getMusicTimeFormatString
import com.hujiejeff.musicplayer.util.logD
import kotlinx.android.synthetic.main.card_album.view.*
import kotlinx.android.synthetic.main.fragment_music_play.*
import kotlinx.android.synthetic.main.fragment_music_play.view.*

class MusicPlayFragment : BaseFragment(), OnPlayerEventListener, SeekBar.OnSeekBarChangeListener {

    private val music
        get() = AudioPlayer.INSTANCE.currentMusic
    private val player
        get() = AudioPlayer.INSTANCE
    private val fragmentList = mutableListOf<Fragment>()

    private var isFirst = true

    override fun getLayoutId() = R.layout.fragment_music_play

    override fun iniView(view: View) {
        view.apply {
            updateUI(music)
            iv_play_mode_loop.setOnClickListener {
                changeLoopMode()
            }
            iv_play_mode_shuffle.setOnClickListener {
                changeShuffleMode()
            }
            iv_play_btn_next.setOnClickListener {
                player.next()
            }
            cv_play_btn_play.setOnClickListener {
                player.playOrPause()
            }
            iv_paly_btn_prev.setOnClickListener {
                player.pre()
            }
            seek_bar.setOnSeekBarChangeListener(this@MusicPlayFragment)
            seek_bar.setOnClickListener {
                //屏蔽点击
            }
            iv_play_btn_close.setOnClickListener {
                activity?.onBackPressed()
            }
            isClickable = true
            //viewpager
            AudioPlayer.INSTANCE.mMusicList.forEach {
                fragmentList.add(AlbumCardFragment(it.albumID))
            }
            play_view_pager.adapter = PlayAlbumPagerAdapter()
            play_view_pager.setPageTransformer(true, ScaleTransformer())
            play_view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {
                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                }

                override fun onPageSelected(position: Int) {
                    //TODO 第一次
                    if (!isFirst) {
                        handler.postDelayed({
                            player.play(position)
                        }, 500)

                    }
                    isFirst = false
                }
            })
            play_view_pager.currentItem = AudioPlayer.INSTANCE.mMusicList.indexOf(music)
        }
    }

    private fun changeShuffleMode() {
        val playMode = PlayMode.valueOf(Preference.play_mode)
        if (playMode != PlayMode.SHUFFLE) {
            player.setMode(PlayMode.SHUFFLE)
        } else {
            player.setMode(PlayMode.SINGLE_LOOP)
        }
    }

    private fun changeLoopMode() {
        val playMode = PlayMode.valueOf(Preference.play_mode)
        if (playMode == PlayMode.SHUFFLE) {
            player.setMode(PlayMode.LOOP)
        } else {
            var m = playMode.value
            m = (++m) % 3
            player.setMode(PlayMode.valueOf(m))
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        player.addOnPlayerEventListener(this)
    }

    private fun View.updateUI(music: Music) {
        music.apply {
            tv_play_title.text = title
            tv_play_artist.text = artist
            seek_bar.max = duration.toInt()
            seek_bar.progress = Preference.play_progress
            tv_current_time.text = getMusicTimeFormatString(Preference.play_progress)
            tv_max_time.text = getMusicTimeFormatString(duration.toInt())
            play_view_pager.setCurrentItem(AudioPlayer.INSTANCE.mMusicList.indexOf(music), true)
        }
        val isShuffle = Preference.play_mode == PlayMode.SHUFFLE.value
        iv_play_mode_loop.setImageLevel(if (isShuffle) 0 else Preference.play_mode)
        iv_play_mode_shuffle.isSelected = isShuffle
    }

    class AlbumCardFragment(private val albumID: Long) : Fragment() {
        override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val view = inflater.inflate(R.layout.card_album, container, false)
            view.album_cover.setImageBitmap(getCover(albumID))
            return view
        }
    }

    inner class PlayAlbumPagerAdapter : FragmentPagerAdapter(childFragmentManager) {
        override fun getItem(position: Int) = fragmentList[position]
        override fun getCount() = fragmentList.size

    }


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        view?.tv_current_time?.text = getMusicTimeFormatString(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        player.seekTo(seekBar!!.progress)
    }

    override fun onChange(music: Music) {
        view?.updateUI(music)
    }

    override fun onPlayerStart() {
        view?.iv_play_btn_play?.isSelected = true
    }

    override fun onPlayerPause() {
        view?.iv_play_btn_play?.isSelected = false
    }

    override fun onPublish(progress: Int) {
        view?.seek_bar?.progress = progress
        view?.tv_current_time?.text = getMusicTimeFormatString(progress)
    }

    override fun onBufferingUpdate(percent: Int) {
    }

    override fun onModeChange(value: Int) {
        when (PlayMode.valueOf(value)) {
            PlayMode.SINGLE, PlayMode.LOOP, PlayMode.SINGLE_LOOP -> {
                iv_play_mode_loop.setImageLevel(value)
                iv_play_mode_shuffle.isSelected = false
            }
            PlayMode.SHUFFLE -> {
                iv_play_mode_loop.setImageLevel(0)
                iv_play_mode_shuffle.isSelected = true
            }
        }
    }
}