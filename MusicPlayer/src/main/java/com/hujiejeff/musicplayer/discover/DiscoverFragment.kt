package com.hujiejeff.musicplayer.discover

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.BaseFragment
import com.hujiejeff.musicplayer.discover.sub.PlaylistSquareFragment
import com.hujiejeff.musicplayer.util.transaction
import kotlinx.android.synthetic.main.home_fragment_discover.view.*

class DiscoverFragment : BaseFragment() {
    override fun getLayoutId(): Int = R.layout.home_fragment_discover

    override fun initView(view: View) {
        view.bnv_discover_top.apply {
            setOnNavigationItemSelectedListener {
                when (it.itemId) {
                    R.id.menu_discover_top_item_playlist -> {
                        (activity as AppCompatActivity).transaction {
                            replace(R.id.fl_discover_sub_container,
                                PlaylistSquareFragment()
                            )
                            addToBackStack(null)
                        }
                    }
                }
                true
            }
        }
    }

}