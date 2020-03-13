package com.hujiejeff.musicplayer.discover.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.hujiejeff.musicplayer.R
import com.hujiejeff.musicplayer.base.BaseActivity
import com.hujiejeff.musicplayer.base.BaseRecyclerViewAdapter
import com.hujiejeff.musicplayer.base.BaseViewHolder
import com.hujiejeff.musicplayer.data.entity.HotSearchString
import com.hujiejeff.musicplayer.util.logD
import com.hujiejeff.musicplayer.util.obtainViewModel
import com.hujiejeff.musicplayer.util.transaction
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.include_search_toolbar.*
import kotlinx.android.synthetic.main.item_hot_search_song.view.*
import kotlinx.android.synthetic.main.item_search_history.view.*

/**
 * Create by hujie on 2020/3/4
 */
class SearchActivity : BaseActivity() {
    private val hotSearchStringList = mutableListOf<HotSearchString>()
    private val searchHistoryStringList = mutableListOf<String>()
    private lateinit var viewModel: SearchViewModel
    private val containerFragment = SearchResultContainerFragment()
    override fun layoutResId(): Int = R.layout.activity_search

    override fun isLightStatusBar(): Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        rv_history.apply {
            adapter = Adapter(this@SearchActivity, R.layout.item_search_history, searchHistoryStringList).apply {
                setOnItemClickListener {
                    viewModel.startSearch(searchHistoryStringList[it])
                }
            }
            itemAnimator = DefaultItemAnimator()
            layoutManager = LinearLayoutManager(this@SearchActivity, LinearLayoutManager.HORIZONTAL, false)
        }


        rv_hot_search.apply {
            adapter = Adapter(this@SearchActivity, R.layout.item_hot_search_song, hotSearchStringList).apply {
                setOnItemClickListener {
                    viewModel.startSearch(hotSearchStringList[it].first)
                }
            }
            itemAnimator = DefaultItemAnimator()
            layoutManager =
                LinearLayoutManager(this@SearchActivity, LinearLayoutManager.VERTICAL, false)
        }

        viewModel = obtainViewModel()

        subscribe()

        et_search.setOnEditorActionListener { textView, i, keyEvent ->
            if (keyEvent != null && KeyEvent.KEYCODE_ENTER == keyEvent.keyCode && KeyEvent.ACTION_DOWN == keyEvent.action) {
                viewModel.startSearch(textView.text.toString())

                return@setOnEditorActionListener true
            }
            false
        }

        et_search.setOnClickListener {
            et_search.isCursorVisible = true
        }

        iv_search_history_clear.setOnClickListener {
            viewModel.clearHistory()
        }

       transaction {
           add(R.id.fl_container, containerFragment)
           hide(containerFragment)
       }

        viewModel.loadHistory()
        viewModel.loadHotSearch()

    }


    private fun subscribe() {
        viewModel.apply {
            loading.observe(this@SearchActivity, Observer { isLoading ->
                lv_loading.visibility = if (isLoading) View.VISIBLE else View.GONE
                fl_container.visibility = if (isLoading) View.GONE else View.VISIBLE
            })

            hotSearch.observe(this@SearchActivity, Observer {
                hotSearchStringList.clear()
                hotSearchStringList.addAll(it)
                rv_hot_search.adapter?.notifyDataSetChanged()
            })

            searchHistory.observe(this@SearchActivity, Observer {
                searchHistoryStringList.clear()
                searchHistoryStringList.addAll(it)
                rv_history.adapter?.notifyDataSetChanged()
            })

            currentSearchKey.observe(this@SearchActivity, Observer {
                et_search.setText(it)
                et_search.isCursorVisible = false
                transaction {
                    show(containerFragment)
                }
            })
        }
    }

    fun obtainViewModel() = obtainViewModel(SearchViewModel::class.java)

    class Adapter<T>(context: Context, layoutID: Int, datas: List<T>) :
        BaseRecyclerViewAdapter<T>(context, layoutID, datas) {
        override fun convert(holder: BaseViewHolder, data: T, position: Int) {
            when (data) {
                is String -> {
                    holder.itemView.tv_history_string.text = data
                }
                is HotSearchString -> {
                    holder.itemView.tv_hot_search_index.text = (position + 1).toString()
                    holder.itemView.tv_hot_search_string.text = data.first
                }
            }
        }
    }

    override fun onBackPressed() {
        if (!containerFragment.isHidden) {
            transaction {
                hide(containerFragment)
            }
            return
        }
        super.onBackPressed()
    }
}