package com.hujiejeff.musicplayer.discover.sub

import android.Manifest
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.AbstractLazyLoadFragment
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.data.entity.PlayListDetail
import com.hujiejeff.musicplayer.data.entity.Track
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.util.loadPlayListCover
import com.hujiejeff.musicplayer.util.logD
import kotlinx.android.synthetic.main.fragment_playlist.view.*
import kotlinx.android.synthetic.main.include_playlist_detail.view.*
import kotlinx.android.synthetic.main.item_music_list.view.*

/**
 * desc: 歌单信息，歌曲播放列表
 * Create by hujie on 2020/1/10
 */
class PlaylistFragment(private val id: Long, private val url: String): AbstractLazyLoadFragment() {
    private val trackList: MutableList<Track> = mutableListOf()

    private lateinit var playListDetail: PlayListDetail

    override fun getTAG(): String = PlaylistFragment::class.java.simpleName

    override fun getLayoutId(): Int = R.layout.fragment_playlist

    override fun initView(view: View) {
        view.apply {
//            iv_playlist_detail_cover.loadPlayListCover(url)
            iv_playlist_detail_cover.loadPlayListCover(url)

            rv_playlist_music_list.apply {
                adapter = Adapter()
                layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
                itemAnimator = DefaultItemAnimator()
            }


        }
    }

    fun updateUI(){
        view?.apply {
            tv_playlist_detail_name.text = playListDetail.name
            tv_playlist_detail_desc.text = playListDetail.description.substring(0,20)
            tv_playlist_detail_comment_count.text = playListDetail.commentCount.toString()
            tv_playlist_detail_share_count.text = playListDetail.shareCount.toString()
        }
    }

    override fun onLoadData() {
        App.dateRepository.getPlayListDetail(id, object : Callback<PlayListDetail>{
            override fun onLoaded(t: PlayListDetail) {
                trackList.addAll(t.tracks)
                logD(trackList.toString())
                view?.rv_playlist_music_list?.adapter?.notifyDataSetChanged()
                playListDetail = t
                updateUI()

            }

            override fun onFailed(mes: String) {
            }

        })
    }

    override fun onPermissionFailed() {
    }

    override fun getPermissions(): Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET
    )


    inner class Adapter :
        BaseRecyclerViewAdapter<Track>(context, R.layout.item_music_list, trackList) {
        override fun convert(holder: BaseViewHolder, data: Track) {
            holder.itemView.apply {
                tv_music_title.text = data.name
                tv_music_artist.text = data.ar[0].name
//                iv_music_cover.loadPlayListCover(data.al.picUrl)
            }
        }
    }

}