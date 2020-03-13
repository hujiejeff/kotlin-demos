package com.hujiejeff.musicplayer.discover

import android.Manifest
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.AbstractLazyLoadFragment
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.data.entity.*
import com.hujiejeff.musicplayer.discover.playlist.PlaylistActivity
import com.hujiejeff.musicplayer.discover.playlistsqure.MainPlaylistActivity
import com.hujiejeff.musicplayer.discover.search.SearchActivity
import com.hujiejeff.musicplayer.util.*
import kotlinx.android.synthetic.main.home_fragment_discover.view.*
import kotlinx.android.synthetic.main.include_search_toolbar.view.*
import kotlinx.android.synthetic.main.item_album_list.view.*
import kotlinx.android.synthetic.main.item_playlist_list.view.*

class DiscoverFragment : AbstractLazyLoadFragment() {

    private val recomendPlaylistList = mutableListOf<RecommendPlayList>()

    private val newSongList = mutableListOf<RecommendNewSong>()

    private val newAlbumList = mutableListOf<RecommendNewAlbum>()

    private lateinit var viewModel: DiscoverViewModel

    override fun getLayoutId(): Int = R.layout.home_fragment_discover


    override fun initView(view: View) {
        view.apply {
            rv_recomend_playlists.apply {
                adapter = Adapter(context, R.layout.item_playlist_list, recomendPlaylistList).apply {
                    setOnItemClickListener {
                        PlaylistActivity.start( recomendPlaylistList[it].id, recomendPlaylistList[it].picUrl, activity!!)
                    }
                }
                layoutManager = GridLayoutManager(context, 3)
                itemAnimator = DefaultItemAnimator()
            }

            rv_new_songs.apply {
                adapter = Adapter(context, R.layout.item_album_list, newSongList)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = DefaultItemAnimator()
            }

            rv_new_albums.apply {
                adapter = Adapter(context, R.layout.item_album_list, newAlbumList)
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                itemAnimator = DefaultItemAnimator()
            }

            et_search.setOnClickListener {
                val intent = Intent(context, SearchActivity::class.java)
                context.startActivity(intent)
            }
        }


        view.bnv_discover_top.apply {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_discover_top_item_playlist -> {
                        (activity as AppCompatActivity).transaction {
                            context.startActivity(Intent(context, MainPlaylistActivity::class.java))
                            addToBackStack(null)
                        }
                    }
                }
                true
            }
        }
    }

    override fun getTAG(): String = "DiscoverFragment"

    override fun onLoadData() {
        viewModel.start()
    }

    override fun onPermissionFailed() {

    }

    override fun getPermissions(): Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET
    )

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = obtainViewModel()
        subscribe()
    }

    private fun subscribe() {
        logD("subscribe")

            viewModel.apply {
                recomendPlaylists.observe {
                    logD("observe")
                    recomendPlaylistList.addAll(it)
                    view?.rv_recomend_playlists?.adapter?.notifyDataSetChanged()
                }
                newSongs.observe {
                    newSongList.addAll(it)
                    view?.rv_new_songs?.adapter?.notifyDataSetChanged()
                }

                newAlbums.observe {
                    newAlbumList.addAll(it)
                    view?.rv_new_albums?.adapter?.notifyDataSetChanged()
                }
                loading.observe {
                    var isLoading = false
                    if (it == 0) {
                        isLoading = true
                    } else if (it == 3) {
                        isLoading = false
                    }
                    view?.ll_discover_container?.visibility = if (isLoading) View.GONE else View.VISIBLE
                    view?.loading_view?.visibility = if (isLoading) View.VISIBLE else View.GONE
                }
            }
    }

    private fun <T> LiveData<T>.observe(action: (T) -> Unit) {
        observe(this@DiscoverFragment, Observer { action(it) })
    }

    private fun obtainViewModel() = obtainViewModel(DiscoverViewModel::class.java)

    class Adapter<T>(context: Context, layoutID: Int, datas: List<T>) :
        BaseRecyclerViewAdapter<T>(context, layoutID, datas) {
        override fun convert(holder: BaseViewHolder, data: T, position: Int) {
            when (data) {
                is RecommendPlayList -> {
                    holder.itemView.apply {
                        tv_playlist_name.text = data.name
                        tv_playlist_play_count.text = (data.playCount / 10000).toString() + "万"
                        iv_playlist_cover.loadPlayListCover(data.picUrl)
                    }
                }
                is RecommendNewAlbum -> {
                    holder.itemView.apply {
                        layoutParams.width = context.dp2Px(140)
                        layoutParams = layoutParams
                        album_cover.loadPlayListCover(data.picUrl)
                        album_title.text = data.name
                        album_artist.text = data.artist.name
                    }
                }
                is RecommendNewSong -> {
                    holder.itemView.apply {
                        layoutParams.width = context.dp2Px(140)
                        layoutParams = layoutParams
                        album_cover.loadPlayListCover(data.picUrl)
                        album_title.text = data.name
                        album_artist.text = data.song.artists[0].name
                    }
                }
            }
        }
    }

}