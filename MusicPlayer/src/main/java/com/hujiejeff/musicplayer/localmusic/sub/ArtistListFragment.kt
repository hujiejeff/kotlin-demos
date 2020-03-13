package com.hujiejeff.musicplayer.localmusic.sub

import android.Manifest
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.AbstractLazyLoadFragment
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.data.entity.Artist
import com.hujiejeff.musicplayer.HomeActivity
import com.hujiejeff.musicplayer.localmusic.LocalMusicFragment
import com.hujiejeff.musicplayer.localmusic.LocalMusicViewModel
import com.hujiejeff.musicplayer.util.getArtistCover
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_artist_list.view.*

class ArtistListFragment : AbstractLazyLoadFragment() {

    private val artistList: MutableList<Artist> = mutableListOf()
    private val spanCount = 2
    private lateinit var homeActivity: HomeActivity
    private lateinit var viewModel: LocalMusicViewModel

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


    override fun onAttach(context: Context) {
        super.onAttach(context)
        homeActivity = context as HomeActivity
        viewModel = (parentFragment as LocalMusicFragment).obtainViewModel()
        subscribe()
    }

    private fun subscribe() {
        viewModel.apply {

            artistItems.observe(homeActivity, Observer {
                artistList.addAll(it)
                view?.rv_list?.adapter?.notifyDataSetChanged()
            })


            artistDataLoading.observe(homeActivity, Observer { isLoading ->
                view?.rv_list?.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
                if (isLoading) {
                    view?.loading_view?.show()
                } else {
                    view?.loading_view?.hide()
                }
            })
        }
    }

    override fun getTAG(): String = ArtistListFragment::class.java.simpleName

    override fun onLoadData() {
        if (artistList.isEmpty()) {
            viewModel.loadArtistList()
        }
    }

    override fun onPermissionFailed() {
        Toast.makeText(context, "permission deny", Toast.LENGTH_SHORT).show()
    }

    override fun getPermissions(): Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )


    inner class ArtistRecycleViewAdapter :
        BaseRecyclerViewAdapter<Artist>(context, R.layout.item_artist_list, artistList) {
        override fun convert(holder: BaseViewHolder, data: Artist, position: Int) {
            holder.itemView.apply {
                artist_cover.setImageBitmap(getArtistCover())
                artist_name.text = data.name
            }
        }
    }
}