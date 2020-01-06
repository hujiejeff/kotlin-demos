package com.hujiejeff.musicplayer.base

import PermissionReq
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment

/**
 * Create by hujie on 2020/1/2
 */
abstract class AbstractLazyLoadFragment: Fragment() {
    private var isViewCreated = false
    private var isLoadedData = false
    private var isFirstVisible = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(getLayoutId(), container, false)
        initView(view)
        return view
    }

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


    private fun onLazyLoadData() {
        //请求权限
        PermissionReq
            .with(this)
            .permissions(*getPermissions())
            .result(object : PermissionReq.Result {
                override fun onGranted() {
                    onLoadData()
                }

                override fun onDenied() {
                    onPermissionFailed()
                }

            }).request()
    }


    protected abstract fun getLayoutId(): Int
    protected abstract fun initView(view: View)
    protected abstract fun onLoadData()
    protected abstract fun onPermissionFailed()
    protected abstract fun getPermissions(): Array<String>

    override fun onDestroyView() {
        super.onDestroyView()
        isViewCreated = false
        isLoadedData = false
        isFirstVisible = false
    }

    private fun isFragmentVisible(fragment: Fragment) = !fragment.isHidden && fragment.userVisibleHint
}