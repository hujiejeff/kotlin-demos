package com.hujiejeff.musicplayer.discover.sub

import android.Manifest
import android.content.Context
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.AbstractLazyLoadFragment
import com.hujiejeff.musicplayer.base.App
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.data.entity.SubCat
import com.hujiejeff.musicplayer.data.source.Callback
import com.hujiejeff.musicplayer.discover.PlaylistSquareViewModel
import com.hujiejeff.musicplayer.util.logD
import kotlinx.android.synthetic.main.fragment_playlist_square.view.*

/**
 * desc: 歌单广场，viewpager
 * Create by hujie on 2020/1/10
 */

class PlaylistSquareFragment : AbstractLazyLoadFragment() {

    private lateinit var viewModel: PlaylistSquareViewModel

    override fun getTAG(): String = "PlaylistSquareFragment"

    override fun onLoadData() {
        loadData()
    }

    override fun onPermissionFailed() {

    }

    override fun getPermissions(): Array<String> = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE
    )

    private val fragmentList: MutableList<Fragment> = mutableListOf()


    override fun getLayoutId(): Int = R.layout.fragment_playlist_square

    override fun initView(view: View) {
        view.apply {
            vp_playlist_list.adapter = PagerAdapter()
            tb_playlist_cats.setupWithViewPager(vp_playlist_list)
        }

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        viewModel = obtainViewModel()
        subscribe()
    }

    private fun subscribe() {
        viewModel.subCatList.observe(this, Observer { list ->
            list.forEach {
                fragmentList.add(PlaylistListFragment().apply { subCat = it })
            }
            view?.vp_playlist_list?.adapter?.notifyDataSetChanged()
        })
    }

    private fun loadData() {
        viewModel.loadSubCat()
    }

    fun obtainViewModel(): PlaylistSquareViewModel =
        ViewModelProviders.of(this).get(PlaylistSquareViewModel::class.java)

    inner class PagerAdapter :
        FragmentPagerAdapter(childFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        override fun getItem(position: Int): Fragment = fragmentList[position]

        override fun getCount(): Int = fragmentList.size

        override fun getPageTitle(position: Int): CharSequence? = viewModel.subCatList.value?.get(position)?.name

    }
}