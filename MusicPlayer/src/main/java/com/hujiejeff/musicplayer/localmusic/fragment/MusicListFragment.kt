package com.hujiejeff.musicplayer.localmusic.fragment

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hujiejeff.musicplayer.localmusic.LocalMusicActivity
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.AbstractLazyLoadFragment
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.data.entity.Music
import com.hujiejeff.musicplayer.localmusic.LocalMusicViewModel
import com.hujiejeff.musicplayer.service.AudioPlayer
import com.hujiejeff.musicplayer.util.loadCover
import com.hujiejeff.musicplayer.util.logD
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_music_list.view.*

class MusicListFragment : AbstractLazyLoadFragment() {

    private val musicList: MutableList<Music> = mutableListOf()
    private lateinit var localMusicActivity: LocalMusicActivity
    private lateinit var viewModel: LocalMusicViewModel

    override fun getLayoutId(): Int = R.layout.fragment_list
    override fun initView(view: View) {
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

    override fun onAttach(context: Context) {
        super.onAttach(context)
        localMusicActivity = activity as LocalMusicActivity
        viewModel = localMusicActivity.obtainViewModel()
        subscribe()
    }

    private fun subscribe() {
        viewModel.apply {
            musicItems.observe(localMusicActivity, Observer<List<Music>> { list ->
                musicList.addAll(list)
                view?.rv_list?.adapter?.notifyDataSetChanged()
                logD("observer")
                viewModel.loadDefaultMusic()
            })

            isDataLoadingError.observe(localMusicActivity, Observer { isError ->
                if (isError) Toast.makeText(
                    localMusicActivity,
                    "load error",
                    Toast.LENGTH_SHORT
                ).show()
            })

            musicDataLoading.observe(localMusicActivity, Observer { isLoading ->
                view?.rv_list?.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
                if (isLoading) {
                    view?.loading_view?.show()
                } else {
                    view?.loading_view?.hide()
                }
            })
        }
    }

    override fun getTAG(): String = MusicListFragment::class.java.simpleName

    override fun onLoadData() {
        if (musicList.isEmpty())
            viewModel.loadMusicList()
    }

    override fun onPermissionFailed() {
        Toast.makeText(context, "permission deny", Toast.LENGTH_SHORT).show()
    }

    override fun getPermissions(): Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    inner class MusicRecyclerViewAdapter :
        BaseRecyclerViewAdapter<Music>(context, R.layout.item_music_list, musicList) {
        override fun convert(holder: BaseViewHolder, data: Music) {
            holder.itemView.apply {
                tv_music_title.text = data.title
                tv_music_artist.text = data.artist
                iv_music_cover.loadCover(data.albumID)
            }
        }
    }
}