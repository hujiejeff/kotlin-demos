package com.hujiejeff.musicplayer.localmusic

import android.view.View
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProviders
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.localmusic.sub.AlbumListFragment
import com.hujiejeff.musicplayer.localmusic.sub.ArtistListFragment
import com.hujiejeff.musicplayer.localmusic.sub.MusicListFragment
import com.hujiejeff.musicplayer.util.logD
import kotlinx.android.synthetic.main.home_fragment_my.view.*


class LocalMusicFragment : BaseFragment() {
    private val fragmentList =
        listOf(MusicListFragment(), AlbumListFragment(), ArtistListFragment())
    private val titleList by lazy {
        listOf(
            getString(R.string.tab_music),
            getString(R.string.tab_album),
            getString(R.string.tab_artist)
        )
    }

    override fun getLayoutId(): Int = R.layout.home_fragment_my
    override fun initView(view: View) {
        view.apply {
            view_pager.adapter = PagerAdapter()
            tab_layout.setupWithViewPager(view_pager)
        }

    }

    fun obtainViewModel(): LocalMusicViewModel {
        return  ViewModelProviders.of(this).get(LocalMusicViewModel::class.java)
    }


    inner class PagerAdapter :
        FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getCount() = fragmentList.size
        override fun getItem(position: Int) = fragmentList[position]
        override fun getPageTitle(position: Int): CharSequence?  {
            return titleList[position]
        }
    }

}