/*
 * Copyright 2022. Explore in HMS. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

fun getSwipeTouchHelper(
    foregroundLayoutProvider: (viewHolder: RecyclerView.ViewHolder) -> View,
    swipeDirs: Int = ItemTouchHelper.RIGHT or ItemTouchHelper.LEFT,
    onSwiped: (position: Int) -> Unit
): ItemTouchHelper {
    val itemTouchHelperCallBack = object : ItemTouchHelper.SimpleCallback(
        0, swipeDirs
    ) {

        // Swipe Item
        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val swipedItemPosition = viewHolder.adapterPosition
            onSwiped.invoke(swipedItemPosition)
        }

        override fun onChildDrawOver(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder?,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val foregroundView = foregroundLayoutProvider(viewHolder ?: return)
            getDefaultUIUtil().onDrawOver(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
            )
        }

        override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
            val foregroundView = foregroundLayoutProvider(viewHolder)
            getDefaultUIUtil().clearView(foregroundView)
        }

        override fun onChildDraw(
            c: Canvas,
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            dX: Float,
            dY: Float,
            actionState: Int,
            isCurrentlyActive: Boolean
        ) {
            val foregroundView = foregroundLayoutProvider(viewHolder)
            getDefaultUIUtil().onDraw(
                c, recyclerView, foregroundView, dX, dY,
                actionState, isCurrentlyActive
            )
        }

        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ) = false
    }

    return ItemTouchHelper(itemTouchHelperCallBack)
}
