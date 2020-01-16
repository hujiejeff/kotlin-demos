package com.hujiejeff.musicplayer.localmusic.fragment

import android.Manifest
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.hujiejeff.musicplayer.localmusic.LocalMusicActivity
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.AbstractLazyLoadFragment
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.data.entity.Album
import com.hujiejeff.musicplayer.localmusic.LocalMusicViewModel
import com.hujiejeff.musicplayer.util.loadCover
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_album_list.view.*

class AlbumListFragment : AbstractLazyLoadFragment() {

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
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        localMusicActivity = context as LocalMusicActivity
        viewModel = localMusicActivity.obtainViewModel()
        subscribe()
    }

    private fun subscribe() {
        viewModel.apply {
            albumItems.observe(localMusicActivity, Observer {
                albumList.addAll(it)
                view?.rv_list?.adapter?.notifyDataSetChanged()

            })

            albumDataLoading.observe(localMusicActivity, Observer { isLoading ->
                view?.rv_list?.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
                if (isLoading) {
                    view?.loading_view?.show()
                } else {
                    view?.loading_view?.hide()
                }
            })
        }
    }

    override fun getTAG(): String = AlbumListFragment::class.java.simpleName

    override fun onLoadData() {
        if (albumList.isEmpty()) {
            viewModel.loadAlbumList()
        }
    }

    override fun onPermissionFailed() {
        Toast.makeText(context, "permission deny", Toast.LENGTH_SHORT).show()
    }

    override fun getPermissions(): Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    inner class AlbumRecycleViewAdapter :
        BaseRecyclerViewAdapter<Album>(context, R.layout.item_album_list, albumList) {
        override fun convert(holder: BaseViewHolder, data: Album) {
            holder.itemView.apply {
                album_title.text = data.title
                album_artist.text = data.artist
                album_cover.loadCover(data.id)
            }
        }
    }
}