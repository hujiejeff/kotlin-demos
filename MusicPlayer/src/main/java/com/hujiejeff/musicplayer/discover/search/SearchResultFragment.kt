package com.hujiejeff.musicplayer.discover.search

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.AbstractLazyLoadFragment
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.data.entity.*
import com.hujiejeff.musicplayer.discover.playlist.PlaylistActivity
import com.hujiejeff.musicplayer.util.loadPlayListCover
import com.hujiejeff.musicplayer.util.logD
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_search_result_album.view.*
import kotlinx.android.synthetic.main.item_search_result_artist.view.*
import kotlinx.android.synthetic.main.item_search_result_song.view.*

/**
 * Create by hujie on 2020/3/6
 * 具体类型的搜索结果
 */
class SearchResultFragment<T> : AbstractLazyLoadFragment() {

    companion object {
        fun <T> newInstance(type: SearchType): SearchResultFragment<T> {
            val instance = SearchResultFragment<T>()
            instance.arguments = Bundle().apply {
                putInt("type", type.value)
            }
            return instance
        }
    }

    private var type: SearchType? = null
    private lateinit var viewModel: SearchViewModel
    private val dataList: MutableList<T> = mutableListOf()
    private var itemLayoutID: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getInt("type")?.SearchType()
        itemLayoutID = when (type) {
            SearchType.SONG -> R.layout.item_search_result_song
            SearchType.ARTIST -> R.layout.item_search_result_artist
            SearchType.ALBUM -> R.layout.item_search_result_album
            SearchType.PLAYLIST -> R.layout.item_search_result_album
            SearchType.USER -> R.layout.item_search_result_artist
            else -> R.layout.item_search_result_song
        }
        viewModel.checkType(type!!)
        subscribe()
    }

    override fun getTAG(): String = "SearchResultFragment"

    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = (context as SearchActivity).obtainViewModel()
    }

    private fun subscribe() {
        viewModel.apply {
            searchLoadingMap[type]?.observe(this@SearchResultFragment, Observer {
                view?.loading_view?.visibility = if (it) View.VISIBLE else View.INVISIBLE
                view?.rv_list?.visibility = if (it) View.INVISIBLE else View.VISIBLE
            })

            searchResultMap[type]?.observe(this@SearchResultFragment, Observer {
                dataList.clear()
                dataList.addAll(it as List<T>)
                view?.rv_list?.adapter?.notifyDataSetChanged()
            })
        }
    }

    override fun initView(view: View) {
        view.rv_list.apply {
            adapter = Adapter(context, itemLayoutID, dataList).apply {
               setOnItemClickListener {i ->
                   when(val t = dataList[i]) {
                       is SearchPlayList -> {
                           PlaylistActivity.start(t.id, t.coverImgUrl, activity!!)
                       }
                   }
               }
            }
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun onLoadData() {
        type?.apply {
            viewModel.loadSearchResult(this, 0, 10)
        }
    }

    override fun onPermissionFailed() {

    }

    override fun getPermissions(): Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    class Adapter<K>(context: Context, layoutID: Int, datas: List<K>) :
        BaseRecyclerViewAdapter<K>(context, layoutID, datas) {
        override fun convert(holder: BaseViewHolder, data: K, position: Int) {
            when (data) {
                is SearchSong -> {
                    holder.itemView.tv_item_song_name.text = data.name
                    var string = ""
                    for (i in data.artists.indices) {
                        string += if (i != data.artists.size - 1) {
                            data.artists[i].name + "/"
                        } else {
                            data.artists[i].name
                        }
                    }
                    holder.itemView.tv_item_artist_name.text = string + " - " + data.album.name

                }

                is SearchArtist -> {
                    holder.itemView.tv_search_result_item_artist_name.text = data.name
                    val url = data.picUrl ?: ""
                    holder.itemView.civ_search_result_item_artist_pic.loadPlayListCover(url)
                }

                is SearchAlbum -> {
                    var string = ""
                    for (i in data.artists.indices) {
                        string += if (i != data.artists.size - 1) {
                            data.artists[i].name + "/"
                        } else {
                            data.artists[i].name
                        }
                    }
                    holder.itemView.tv_search_result_item_album_name.text = data.name
                    holder.itemView.tv_search_result_item_album_artist.text = string
                    holder.itemView.iv_search_result_item_album_pic.loadPlayListCover(data.picUrl)
                }

                is SearchPlayList -> {
                    holder.itemView.tv_search_result_item_album_name.text = data.name
                    holder.itemView.tv_search_result_item_album_artist.text =
                        "${data.trackCount}首 by ${data.creator.nickname}, 播放${data.playCount / 10000}万次"
                    holder.itemView.iv_search_result_item_album_pic.loadPlayListCover(data.coverImgUrl)
                }

                is SearchUser -> {
                    holder.itemView.tv_search_result_item_artist_name.text = data.nickname
                    val url = data.avatarUrl ?: ""
                    holder.itemView.civ_search_result_item_artist_pic.loadPlayListCover(url)
                }
            }
        }
    }


}