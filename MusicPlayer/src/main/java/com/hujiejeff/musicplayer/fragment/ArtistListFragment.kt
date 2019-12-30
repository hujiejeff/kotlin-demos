package com.hujiejeff.musicplayer.fragment

import android.graphics.Color
import android.view.View
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.BaseFragment

class ArtistListFragment: BaseFragment(){
    override fun getLayoutId(): Int = R.layout.fragment_list

    override fun iniView(view: View) {
    }

    override fun onResume() {
        super.onResume()
        view?.setBackgroundColor(Color.DKGRAY)
    }
}