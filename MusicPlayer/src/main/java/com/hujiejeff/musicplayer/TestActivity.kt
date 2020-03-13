package com.hujiejeff.musicplayer

import android.graphics.drawable.Animatable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        iv_hook2.setOnClickListener {
            val drawable = iv_hook2.drawable
            (drawable as Animatable).start()
        }
    }
}
