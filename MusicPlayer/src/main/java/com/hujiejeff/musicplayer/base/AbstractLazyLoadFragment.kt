package com.hujiejeff.musicplayer.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

/**
 * Create by hujie on 2020/1/2
 */
abstract class AbstractLazyLoadFragment: Fragment() {
    private var isViewCreated = false
    private var isLoadedData = false
    private var isFirstVisible = true

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        if (isFragmentVisible(this) && isAdded) {
            if (parentFragment == null || isFragmentVisible(parentFragment!!)) {
                onLazyLoadData()
                isLoadedData = true
                if (isFirstVisible) {
                    isFirstVisible = false
                }
            }
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        //viewpager 管理时，考虑使用setMaxLifeCycle
        super.setUserVisibleHint(isVisibleToUser)
        if (isFragmentVisible(this) && !isLoadedData && isAdded) {
            onLazyLoadData()
            isLoadedData = true
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        //FragmentManager管理时
        super.onHiddenChanged(hidden)
        if (!hidden && !isResumed) {
            return
        }
        if (!hidden && isFirstVisible && isAdded) {
            onLazyLoadData()
            isFirstVisible = false
            isLoadedData = true
        }
    }


    protected fun onLazyLoadData() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        isLoadedData = false
        isFirstVisible = false
    }

    private fun isFragmentVisible(fragment: Fragment) = !fragment.isHidden && fragment.userVisibleHint
}