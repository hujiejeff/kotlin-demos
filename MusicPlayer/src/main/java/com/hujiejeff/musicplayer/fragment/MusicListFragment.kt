package com.hujiejeff.musicplayer.fragment

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hujiejeff.musicplayer.MainActivity
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.data.source.LocalDataSource
import com.hujiejeff.musicplayer.service.AudioPlayer
import com.hujiejeff.musicplayer.util.loadCover
import com.hujiejeff.musicplayer.util.logD
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_music_list.view.*

class MusicListFragment : BaseFragment() {

    private val musicList: MutableList<Music> = mutableListOf()
    private lateinit var mainActivity: MainActivity

    override fun getLayoutId(): Int = R.layout.fragment_list
    override fun initView(view: View) {
        logD(view.rv_list.toString())
        view.rv_list.apply {
            adapter = MusicRecyclerViewAdapter().apply {
                setOnItemClickListener {
                    AudioPlayer.INSTANCE.play(it)
                }
            }
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mainActivity = activity as MainActivity
        if (musicList.isEmpty()) {
            loadMusicList()
        }
    }

    private fun loadMusicList() {
        PermissionReq
            .with(this)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .result(object : PermissionReq.Result {
                override fun onGranted() {
                    App.dateRepository.getLocalMusicList(object: LocalDataSource.Callback<Music>{
                        override fun onLoaded(dataList: MutableList<Music>) {
                            musicList.addAll(dataList)
                            AudioPlayer.INSTANCE.mMusicList = musicList
                            view?.rv_list?.adapter?.notifyDataSetChanged()
                            mainActivity.loadControlPanel()
                        }

                        override fun onFailed(mes: String) {
                            Toast.makeText(context, mes, Toast.LENGTH_SHORT).show()
                        }
                    })
                }
                override fun onDenied() {
                    Toast.makeText(context, "permission deny", Toast.LENGTH_SHORT).show()
                }
            })
            .request()
    }

    inner class MusicRecyclerViewAdapter : BaseRecyclerViewAdapter<Music>(context, R.layout.item_music_list, musicList) {
        override fun convert(holder: BaseViewHolder, data: Music) {
            holder.itemView.apply {
                tv_music_title.text = data.title
                tv_music_artist.text = data.artist
                iv_music_cover.loadCover(data.albumID)
            }
        }
    }
}