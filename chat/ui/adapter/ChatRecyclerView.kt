package com.jirah.sitterapp.chat.ui.adapter

import android.content.Context
import android.util.AttributeSet
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Ramiz Raja on 04/01/2019
 */
class ChatRecyclerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : RecyclerView(context, attrs, defStyleAttr), View.OnLayoutChangeListener {
    init {
        layoutManager = LinearLayoutManager(context).apply {
            stackFromEnd = true
        }
//        addOnLayoutChangeListener(this)
//        addAdapterObserver()
    }

    private fun addAdapterObserver() {
        adapter?.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (itemCount > 0) {
                    moveToBottom()
                }
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                moveToBottom()
            }
        })
    }

    override fun onLayoutChange(v: View, left: Int, top: Int, right: Int, bottom: Int, oldLeft: Int,
                                oldTop: Int, oldRight: Int, oldBottom: Int) {
        if (bottom < oldBottom) {
            moveToBottom()
        }
    }

//    override fun setAdapter(adapter: Adapter<*>?) {
//        super.setAdapter(adapter)
//        addAdapterObserver()
//    }
}

fun RecyclerView.moveToBottom() {
    if (adapter == null || adapter!!.itemCount <= 0) return
    post { scrollToPosition(adapter!!.itemCount - 1) }
}