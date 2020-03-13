package com.hujiejeff.musicplayer.discover.search

import android.Manifest
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.AbstractLazyLoadFragment
import com.hujiejeff.musicplayer.data.entity.*
import kotlinx.android.synthetic.main.fragment_search_result_container.view.*

/**
 * Create by hujie on 2020/3/6
 * viewpager搜索结果
 */
class SearchResultContainerFragment : AbstractLazyLoadFragment() {
    private val fragmentList: List<Fragment> = listOf(
        SearchResultFragment.newInstance<SearchSong>(SearchType.SONG),
        SearchResultFragment.newInstance<SearchArtist>(SearchType.ARTIST),
        SearchResultFragment.newInstance<SearchAlbum>(SearchType.ALBUM),
        SearchResultFragment.newInstance<SearchPlayList>(SearchType.PLAYLIST),
        SearchResultFragment.newInstance<SearchUser>(SearchType.USER)
    )
    private val typeList: List<String> = listOf("单曲", "歌手", "专辑", "歌单", "用户")

    private lateinit var viewModel: SearchViewModel


    override fun getTAG(): String = "SearchResultContainerFragment"

    override fun getLayoutId(): Int = R.layout.fragment_search_result_container

    override fun initView(view: View) {
        view.apply {
            vp_search_result.adapter = PagerAdapter()
            tb_search_result.setupWithViewPager(vp_search_result)
        }
    }

    override fun onLoadData() {
        viewModel = (activity as SearchActivity).obtainViewModel()

    }


    override fun onPermissionFailed() {

    }

    override fun getPermissions(): Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )


    inner class PagerAdapter :
        FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = fragmentList[position]

        override fun getCount(): Int = fragmentList.size

        override fun getPageTitle(position: Int): CharSequence? = typeList[position]

    }
}