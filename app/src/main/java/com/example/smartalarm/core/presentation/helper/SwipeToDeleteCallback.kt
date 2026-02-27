package com.example.smartalarm.core.presentation.helper

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.drawable.Drawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView


/**
 * A generic swipe-to-delete callback for RecyclerView items.
 *
 * Supports both left and right swipe directions and provides a customizable background
 * and delete icon. Triggers a callback when an item is swiped.
 *
 * @param deleteIcon The icon to show when swiping.
 * @param backgroundColor The background color behind the swiped item.
 * @param cornerRadius The corner radius for the background rectangle.
 * @param onItemSwiped A lambda to invoke when an item is swiped, receiving the item's position.
 */
open class SwipeToDeleteCallback(
    private val deleteIcon: Drawable,
    private val backgroundColor: Int,
    private val cornerRadius: Float = 24f,
    private val onItemSwiped: (position: Int) -> Unit
) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    private val paint = Paint().apply {
        color = backgroundColor
        isAntiAlias = true
    }

    /**
     * Disable item move functionality.
     */
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean = false

    /**
     * Called when an item is swiped left or right.
     *
     * @param viewHolder The ViewHolder that was swiped.
     * @param direction The direction of the swipe.
     */
    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        handleSwipe(viewHolder.bindingAdapterPosition, direction)
    }

    /**
     * Handles the swipe event. Designed as `open` for unit testing or customization.
     *
     * @param position The adapter position of the swiped item.
     * @param direction The direction of the swipe.
     */
    protected open fun handleSwipe(position: Int, direction: Int) {
        onItemSwiped(position)
    }

    /**
     * Draws the swipe background and delete icon while swiping.
     *
     * @param c The canvas on which to draw.
     * @param rv The RecyclerView being interacted with.
     * @param vh The ViewHolder being swiped.
     * @param dX The horizontal distance the item has been swiped.
     * @param dY The vertical distance the item has been swiped (unused here).
     * @param actionState The type of interaction (e.g., swipe, drag).
     * @param isCurrentlyActive True if the user is actively swiping the item.
     */
    override fun onChildDraw(
        c: Canvas,
        rv: RecyclerView,
        vh: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = vh.itemView

            val rect = RectF(
                itemView.left.toFloat(),
                itemView.top.toFloat(),
                itemView.right.toFloat(),
                itemView.bottom.toFloat()
            )
            c.drawRoundRect(rect, cornerRadius, cornerRadius, paint)

            val icon = deleteIcon
            val margin = (itemView.height - icon.intrinsicHeight) / 2
            val top = itemView.top + margin
            val bottom = itemView.bottom - margin

            if (dX > 0) {
                val left = itemView.left + margin
                val right = left + icon.intrinsicWidth
                icon.setBounds(left, top, right, bottom)
            } else {
                val right = itemView.right - margin
                val left = right - icon.intrinsicWidth
                icon.setBounds(left, top, right, bottom)
            }

            icon.draw(c)
        }

        super.onChildDraw(c, rv, vh, dX, dY, actionState, isCurrentlyActive)
    }
}
