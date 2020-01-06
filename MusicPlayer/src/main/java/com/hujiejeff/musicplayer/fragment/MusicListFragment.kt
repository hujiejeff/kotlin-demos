package com.hujiejeff.musicplayer.fragment

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hujiejeff.musicplayer.MainActivity
import com.hujiejeff.musicplayer.R
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

class MusicListFragment : BaseFragment() {

    private val musicList: MutableList<Music> = mutableListOf()
    private lateinit var mainActivity: MainActivity
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
        mainActivity = activity as MainActivity
        viewModel = mainActivity.obtainViewModel()
        subscribe()
    }

    private fun subscribe() {
        viewModel.apply {
            musicItems.observe(mainActivity, Observer<List<Music>> { list ->
                musicList.addAll(list)
                view?.rv_list?.adapter?.notifyDataSetChanged()
                logD("observer")
                viewModel.loadDefaultMusic()
            })

            isDataLoadingError.observe(mainActivity, Observer { isError ->
                if (isError) Toast.makeText(mainActivity, "load error", Toast.LENGTH_SHORT).show()
            })

            dataLoading.observe(mainActivity, Observer {isLoading ->
                view?.rv_list?.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
                if (isLoading) {
                    view?.progressBar?.show()
                } else {
                    view?.progressBar?.hide()
                }
            })
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
                    viewModel.loadMusicList()
                }

                override fun onDenied() {
                    Toast.makeText(context, "permission deny", Toast.LENGTH_SHORT).show()
                }
            })
            .request()
    }

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