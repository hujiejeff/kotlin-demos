package com.hujiejeff.musicplayer.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

abstract class BaseRecyclerViewAdapter<T>(
    private val context: Context?,
    private val itemLayoutId: Int,
    private val dataList: List<T>
) : RecyclerView.Adapter<BaseViewHolder>() {
    private var mItemClick: ((position: Int) -> Unit)? = null
    private var mItemLongClick: ((position: Int) -> Boolean)? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        val view = LayoutInflater.from(context).inflate(itemLayoutId, parent, false)
        return BaseViewHolder(view)
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val data = dataList[position]
        holder.itemView.setOnClickListener {
            mItemClick?.invoke(position)
        }
        holder.itemView.setOnLongClickListener {
            if (mItemLongClick == null) {
                false
            } else {
                mItemLongClick!!.invoke(position)
            }
        }
        convert(holder, data, position)
    }

    override fun getItemCount() = dataList.size
    abstract fun convert(holder: BaseViewHolder, data: T, position: Int)
    fun setOnItemClickListener(itemClick: (position: Int) -> Unit) {
        mItemClick = itemClick
    }

    fun setOnItemLongClickListener(itemLongClick: (position: Int) -> Boolean) {
        mItemLongClick = itemLongClick
    }
}
