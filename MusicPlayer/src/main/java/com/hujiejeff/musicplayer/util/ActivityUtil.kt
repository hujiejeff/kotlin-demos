package com.hujiejeff.musicplayer.util

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.hujiejeff.musicplayer.ViewModelFactory

fun AppCompatActivity.transaction(action: FragmentTransaction.() -> Unit) {
    supportFragmentManager.beginTransaction().apply {
        action()
        commit()
    }
}

fun <T : ViewModel> AppCompatActivity.obtainViewModel(viewModelClass: Class<T>) =
    ViewModelProviders.of(this, ViewModelFactory.getInstance(application)).get(viewModelClass)