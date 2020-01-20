package com.hujiejeff.musicplayer.discover.sub

import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.data.entity.SubCat
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.util.logD
import kotlinx.android.synthetic.main.fragment_playlist_square.view.*

/**
 * desc: 歌单广场，viewpager
 * Create by hujie on 2020/1/10
 */

class PlaylistSquareFragment : BaseFragment() {

    private val fragmentList: MutableList<Fragment> = mutableListOf()
    private val subCatList: MutableList<SubCat> = mutableListOf()


    override fun getLayoutId(): Int = R.layout.fragment_playlist_square

    override fun initView(view: View) {
        view.apply {
            vp_playlist_list.adapter = PagerAdapter()
            tb_playlist_cats.setupWithViewPager(vp_playlist_list)
        }
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun loadData() {
        App.dateRepository.getSubCat(object : Callback<List<SubCat>> {
            override fun onLoaded(t: List<SubCat>) {
                subCatList.addAll(t.subList(0, 10))
                subCatList.forEach { subCat ->
                    fragmentList.add(PlaylistListFragment().apply { this.subCat = subCat.name })
                }
                view?.vp_playlist_list?.adapter?.notifyDataSetChanged()
            }

            override fun onFailed(mes: String) {
                logD("onFailed: $mes")
            }

        })
    }

    inner class PagerAdapter :
        FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = fragmentList[position]

        override fun getCount(): Int = fragmentList.size

        override fun getPageTitle(position: Int): CharSequence? = subCatList[position].name

    }
}