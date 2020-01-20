package com.hujiejeff.musicplayer.discover.sub

import android.Manifest
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.*
import com.hujiejeff.musicplayer.data.entity.PlayList
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.util.loadPlayListCover
import com.hujiejeff.musicplayer.util.logD
import com.hujiejeff.musicplayer.util.transaction
import kotlinx.android.synthetic.main.fragment_list.view.*
import kotlinx.android.synthetic.main.item_playlist_list.view.*

/**desc: 歌单列表，3x3列grid
 * Create by hujie on 2020/1/10
 */

class PlaylistListFragment : AbstractLazyLoadFragment() {

    lateinit var subCat: String
    private var playLists: MutableList<PlayList> = mutableListOf()


    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun initView(view: View) {
        view.rv_list.apply {
            adapter = Adapter().apply {
                setOnItemClickListener {
                    if (activity is AppCompatActivity) {
                        (activity as AppCompatActivity).transaction {
                            replace(
                                android.R.id.content,
                                PlaylistFragment(
                                    playLists[it].id,
                                    playLists[it].coverImgUrl
                                ),
                                "playlist"
                            )
                            addToBackStack("fda")
                        }
                    }
                }
            }
            layoutManager = GridLayoutManager(context, 3)
            itemAnimator = DefaultItemAnimator()
        }
    }

    override fun getTAG(): String = PlaylistListFragment::class.java.simpleName

    override fun onPermissionFailed() {
        logD("onPermissionFailed")
    }

    override fun getPermissions(): Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    override fun onLoadData() {
        App.dateRepository.getPlayLists(subCat, 10, "hot", object : Callback<List<PlayList>> {
            override fun onLoaded(t: List<PlayList>) {
                playLists.addAll(t)
                view?.rv_list?.adapter?.notifyDataSetChanged()
            }

            override fun onFailed(mes: String) {
                logD(mes)
            }
        })
    }


    inner class Adapter :
        BaseRecyclerViewAdapter<PlayList>(context, R.layout.item_playlist_list, playLists) {
        override fun convert(holder: BaseViewHolder, data: PlayList) {
            holder.itemView.apply {
                tv_playlist_name.text = data.name
                tv_playlist_play_count.text = (data.playCount / 10000).toString() + "万"
                iv_playlist_cover.loadPlayListCover(data.coverImgUrl)
            }
        }
    }
}