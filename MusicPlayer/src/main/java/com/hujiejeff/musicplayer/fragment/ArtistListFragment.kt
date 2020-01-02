package com.hujiejeff.musicplayer.fragment

import android.Manifest
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.entity.Artist
import com.hujiejeff.musicplayer.util.getArtistCover
import com.hujiejeff.musicplayer.util.getArtistList
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_artist_list.view.*

class ArtistListFragment : BaseFragment() {
    private val artistList: MutableList<Artist> = mutableListOf()
    private val spanCount = 2

    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun initView(view: View) {
        view.rv_list.apply {
            adapter = ArtistRecycleViewAdapter().apply {
                setOnItemClickListener {
                }
            }
            layoutManager = GridLayoutManager(context, spanCount)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (artistList.isEmpty()) {
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
                    artistList.addAll(getArtistList())
                    view?.rv_list?.adapter?.notifyDataSetChanged()
                }

                override fun onDenied() {
                    Toast.makeText(context, "permission deny", Toast.LENGTH_SHORT).show()
                }
            })
            .request()
    }

    inner class ArtistRecycleViewAdapter :
        BaseRecyclerViewAdapter<Artist>(context, R.layout.item_artist_list, artistList) {
        override fun convert(holder: BaseViewHolder, data: Artist) {
            holder.itemView.apply {
                artist_cover.setImageBitmap(getArtistCover())
                artist_name.text = data.name
            }
        }
    }
}