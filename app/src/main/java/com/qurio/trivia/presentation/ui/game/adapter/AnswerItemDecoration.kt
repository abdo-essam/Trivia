package com.qurio.trivia.presentation.ui.game.adapter

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class AnswerItemDecoration(
    private val spacing: Int
) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)

        // Add spacing to all items except the last one
        if (position != state.itemCount - 1) {
            outRect.bottom = spacing
        }
    }
}