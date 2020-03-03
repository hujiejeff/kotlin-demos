package com.hujiejeff.musicplayer.component

import android.os.Handler
import android.os.Looper
import java.util.concurrent.Executor
import java.util.concurrent.Executors

/**
 * Create by hujie on 2020/1/2
 */
const val THREAD_COUNT = 3

/**
* 线程池聚合抽象
* */
open class AppExecutors(
    val diskIO: Executor = DiskIOThreadExecutor(),
    val networkIO: Executor = Executors.newFixedThreadPool(THREAD_COUNT),
    val mainThread: Executor = MainThreadExecutor()
) {

    private class MainThreadExecutor: Executor {
        private val mainThreadHandler = Handler(Looper.getMainLooper())
        override fun execute(command: Runnable) {
            mainThreadHandler.post(command)
        }
    }

    private  class DiskIOThreadExecutor: Executor {
        private val diskIO = Executors.newSingleThreadExecutor()
        override fun execute(command: Runnable) {
            diskIO.execute(command)
        }
    }
}