package com.hujiejeff.musicplayer.player

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.data.entity.PlayMode
import com.hujiejeff.musicplayer.data.Preference
import com.hujiejeff.musicplayer.HomeActivity
import com.hujiejeff.musicplayer.util.*
import kotlinx.android.synthetic.main.card_album.view.*
import kotlinx.android.synthetic.main.fragment_music_play.view.*

class MusicPlayFragment : BaseFragment(), SeekBar.OnSeekBarChangeListener {

    private val fragmentList = mutableListOf<Fragment>()

    private var isFirst = true

    private lateinit var homeActivity: HomeActivity
    private lateinit var viewModel: PlayerViewModel

    override fun getLayoutId() = R.layout.fragment_music_play

    override fun initView(view: View) {
        view.apply {
            viewModel.currentMusic.value?.let {
                logD("updateUI")
                updateUI(it)
                seek_bar.progress = Preference.play_progress
            }
            //attach前尚未订阅，所以无法接收消息改变UI
            iv_play_btn_play?.isSelected = viewModel.isPlay.value!!

            initClickListener()
            isClickable = true
            //viewpager
            viewModel.musicItems.value?.forEach {
                fragmentList.add(
                    AlbumCardFragment(
                        it.albumID
                    )
                )
            }
            play_view_pager.adapter = PlayAlbumPagerAdapter()
            play_view_pager.setPageTransformer(true,
                ScaleTransformer()
            )
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
                            viewModel.play(position)
                        }, 500)

                    }
                    isFirst = false
                }
            })
            play_view_pager.currentItem = viewModel.playPosition
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeActivity = context as HomeActivity
        viewModel = App.playerViewModel
        subscribe()
    }

    //初始按钮监听
    private fun View.initClickListener() {
        iv_play_mode_loop.setOnClickListener {
            viewModel.changeLoopMode()
        }

        iv_play_mode_shuffle.setOnClickListener {
            viewModel.changeShuffleMode()
        }

        iv_paly_btn_prev.setOnClickListener {
            viewModel.pre()
        }
        iv_play_btn_next.setOnClickListener {
            viewModel.next()
        }
        cv_play_btn_play.setOnClickListener {
            viewModel.playOrPause()
        }

        seek_bar.setOnSeekBarChangeListener(this@MusicPlayFragment)
        seek_bar.setOnClickListener {
            //屏蔽点击
        }

        iv_play_btn_close.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    //订阅消息改变UI
    private fun subscribe() {
        viewModel.apply {
            //music change
            currentMusic.observe(homeActivity, Observer {
                view?.updateUI(it)
            })

            //music state
            isPlay.observe(homeActivity, Observer {
                view?.iv_play_btn_play?.isSelected = it
            })

            //music progress
            playProgress.observe(homeActivity, Observer {
                view?.seek_bar?.progress = it
                view?.tv_current_time?.text = getMusicTimeFormatString(it)
            })

            //music mode
            playMode.observe(homeActivity, Observer {
                when (it) {
                    PlayMode.SINGLE, PlayMode.LOOP, PlayMode.SINGLE_LOOP -> {
                        view?.iv_play_mode_loop?.setImageLevel(it.value)
                        view?.iv_play_mode_shuffle?.isSelected = false
                    }
                    PlayMode.SHUFFLE -> {
                        view?.iv_play_mode_loop?.setImageLevel(0)
                        view?.iv_play_mode_shuffle?.isSelected = true
                    }
                    else -> {
                    }
                }
            })
        }


    }

    private fun View.updateUI(music: Music) {
        music.apply {
            tv_play_title.text = title
            tv_play_artist.text = artist
            seek_bar.max = duration.toInt()
            seek_bar.progress = 0
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
            view.album_cover.loadCover(albumID)
            return view
        }
    }

    inner class PlayAlbumPagerAdapter :
        FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int) = fragmentList[position]
        override fun getCount() = fragmentList.size
    }


    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        view?.tv_current_time?.text = getMusicTimeFormatString(progress)
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        viewModel.seekTo(seekBar!!.progress)
    }
}