package com.example.scoutotask.utils

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration


class ItemSpacingDecoration(private val spacing: Int) : ItemDecoration() {
    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        outRect.top = spacing
        outRect.bottom = spacing
        outRect.left = spacing
        outRect.right = spacing
    }
}