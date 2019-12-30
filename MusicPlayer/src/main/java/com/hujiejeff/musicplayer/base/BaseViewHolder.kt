package com.hujiejeff.musicplayer.base

import android.util.SparseArray
import android.view.View
import androidx.core.util.set
import androidx.recyclerview.widget.RecyclerView

class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val views: SparseArray<View> = SparseArray()
    fun <V : View> getView(id: Int): V {
        var view = views[id]
        if (views[id] == null) {
            view = itemView.findViewById(id)
            views[id] = view
        }
        return view as V
    }
}