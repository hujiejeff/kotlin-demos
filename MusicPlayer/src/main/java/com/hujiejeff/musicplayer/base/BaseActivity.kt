package com.hujiejeff.musicplayer.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.include_activity_main.*


abstract class BaseActivity: AppCompatActivity() {
    protected abstract fun layoutResId(): Int
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResId())
        setSupportActionBar(toolbar)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionReq.onRequestPermissionResult(requestCode, permissions, grantResults)
    }
}
