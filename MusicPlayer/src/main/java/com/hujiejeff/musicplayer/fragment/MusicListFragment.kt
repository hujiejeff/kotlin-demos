package com.hujiejeff.musicplayer.fragment

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.entity.Music
import com.hujiejeff.musicplayer.service.AudioPlayer
import com.hujiejeff.musicplayer.util.getCover
import com.hujiejeff.musicplayer.util.getMusicList
import com.hujiejeff.musicplayer.util.logD
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_music_list.view.*

class MusicListFragment : BaseFragment() {

    private val musicList: MutableList<Music> = mutableListOf()

    override fun getLayoutId(): Int = R.layout.fragment_list
    override fun iniView(view: View) {
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
                    musicList.addAll(getMusicList())
                    view?.rv_list?.adapter?.notifyDataSetChanged()
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
                iv_music_cover.setImageBitmap(getCover(data.albumID))
            }
        }
    }
}