package com.hujiejeff.musicplayer.base

import PermissionReq
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.hujiejeff.musicplayer.util.logD

/**
 * Create by hujie on 2020/1/2
 */
abstract class AbstractLazyLoadFragment: Fragment() {
    private var isViewCreated = false
    private var isLoadedData = false
    private var isFirstVisible = true

    override fun onAttach(context: Context) {
        super.onAttach(context)
        logD("${getTAG()}: onAttach")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logD("${getTAG()}: onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        logD("${getTAG()}:onCreateView")
        val view = inflater.inflate(getLayoutId(), container, false)
        initView(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isViewCreated = true
        logD("${getTAG()}:onViewCreated")
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        logD("${getTAG()}:onActivityCreated")
    }

    override fun onStart() {
        super.onStart()
        logD("${getTAG()}:onStart")
    }

    override fun onResume() {
        super.onResume()
        logD("${getTAG()}:onResume")
        if (!isLoadedData) {
            onLazyLoadData()
            isLoadedData = true
            if (isFirstVisible) {
                isFirstVisible = false
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        //FragmentManager管理时
        super.onHiddenChanged(hidden)
        logD("${getTAG()}:onHiddenChanged")
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
        logD("${getTAG()}:onLazyLoadData")
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

    protected abstract fun getTAG(): String
    protected abstract fun getLayoutId(): Int
    protected abstract fun initView(view: View)
    protected abstract fun onLoadData()
    protected abstract fun onPermissionFailed()
    protected abstract fun getPermissions(): Array<String>

    override fun onPause() {
        super.onPause()
        logD("${getTAG()}:onPause")
    }

    override fun onStop() {
        super.onStop()
        logD("${getTAG()}:onStop")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        logD("${getTAG()}:onDestroy")
        isViewCreated = false
        isLoadedData = false
        isFirstVisible = false
    }

    override fun onDestroy() {
        super.onDestroy()
        logD("${getTAG()}:onDestroy")
    }

    override fun onDetach() {
        super.onDetach()
        logD("${getTAG()}:onDetach")
    }

}