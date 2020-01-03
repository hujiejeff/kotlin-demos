package com.hujiejeff.musicplayer.fragment

import PermissionReq
import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.data.entity.Album
import com.hujiejeff.musicplayer.util.getAlbumList
import com.hujiejeff.musicplayer.util.getCover
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_album_list.view.*

class AlbumListFragment: BaseFragment() {
    private val albumList: MutableList<Album> = mutableListOf()
    private val spanCount = 2

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
                    albumList.addAll(getAlbumList())
                    view?.rv_list?.adapter?.notifyDataSetChanged()
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
                album_cover.setImageBitmap(getCover(data.id))
            }
        }
    }
}