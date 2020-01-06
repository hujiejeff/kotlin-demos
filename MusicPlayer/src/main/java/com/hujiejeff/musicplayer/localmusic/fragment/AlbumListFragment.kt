package com.hujiejeff.musicplayer.localmusic.fragment

import PermissionReq
import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.hujiejeff.musicplayer.localmusic.LocalMusicActivity
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.data.entity.Album
import com.hujiejeff.musicplayer.localmusic.LocalMusicViewModel
import com.hujiejeff.musicplayer.util.loadCover
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_album_list.view.*

class AlbumListFragment: BaseFragment() {
    private val albumList: MutableList<Album> = mutableListOf()
    private val spanCount = 2
    private lateinit var localMusicActivity: LocalMusicActivity
    private lateinit var viewModel: LocalMusicViewModel

    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun initView(view: View) {
        view.rv_list.apply {
            adapter = AlbumRecycleViewAdapter().apply {
                setOnItemClickListener {
                }
            }
            layoutManager = GridLayoutManager(context, spanCount)
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        localMusicActivity = context as LocalMusicActivity
        viewModel = localMusicActivity.obtainViewModel()
        subscribe()
    }

    private fun subscribe() {
        viewModel.albumItems.observe(localMusicActivity, Observer {
            albumList.addAll(it)
            view?.rv_list?.adapter?.notifyDataSetChanged()
        })
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (albumList.isEmpty()) {
            loadAlbumList()
        }
    }

    private fun loadAlbumList() {
        PermissionReq
            .with(this)
            .permissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .result(object : PermissionReq.Result {
                override fun onGranted() {
                    viewModel.loadAlbumList()
                }
                override fun onDenied() {
                    Toast.makeText(context, "permission deny", Toast.LENGTH_SHORT).show()
                }
            })
            .request()
    }

    inner class AlbumRecycleViewAdapter: BaseRecyclerViewAdapter<Album>(context, R.layout.item_album_list, albumList) {
        override fun convert(holder: BaseViewHolder, data: Album) {
            holder.itemView.apply {
                album_title.text = data.title
                album_artist.text = data.artist
                album_cover.loadCover(data.id)
            }
        }
    }
}