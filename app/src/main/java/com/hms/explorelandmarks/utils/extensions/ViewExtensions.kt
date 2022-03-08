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

package com.hms.explorelandmarks.utils.extensions

import android.view.View
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar

fun View.show() {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
}

fun View.gone() {
    if (visibility != View.GONE) {
        visibility = View.GONE
    }
}

fun View.invisible() {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }
}

//region SNACKBAR
inline fun View.showSnackBar(
    @StringRes messageRes: Int,
    length: Int = Snackbar.LENGTH_LONG,
    block: Snackbar.() -> Unit
) {
    showSnackBar(resources.getString(messageRes), length, block)
}

inline fun View.showSnackBar(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
    block: Snackbar.() -> Unit
) {
    createSnackBar(message, length).apply {
        block()
        show()
    }
}

fun View.showSnackBar(
    @StringRes messageRes: Int,
    length: Int = Snackbar.LENGTH_LONG,
) = showSnackBar(resources.getString(messageRes), length)

fun View.showSnackBar(
    message: String,
    length: Int = Snackbar.LENGTH_LONG,
) = createSnackBar(message, length).apply { show() }

fun View.createSnackBar(message: String, length: Int = Snackbar.LENGTH_LONG) =
    Snackbar.make(this, message, length)

fun View.createSnackBar(@StringRes messageRes: Int, length: Int = Snackbar.LENGTH_LONG) =
    createSnackBar(resources.getString(messageRes), length)

fun Snackbar.action(@StringRes actionRes: Int, color: Int? = null, listener: (View) -> Unit) =
    action(view.resources.getString(actionRes), color, listener)

fun Snackbar.action(action: String, color: Int? = null, listener: (View) -> Unit): Snackbar {
    setAction(action, listener)
    color?.let { setActionTextColor(ContextCompat.getColor(context, color)) }
    return this
}

fun Snackbar.onShown(
    onShown: (Snackbar) -> Unit?,
) = addCallBack(onShown = { snackbar ->
    onShown.invoke(snackbar)
})

fun Snackbar.onDismissed(
    onDismissed: (Snackbar, event: Int) -> Unit
) = addCallBack { snackbar, event ->
    onDismissed.invoke(snackbar, event)
}

fun Snackbar.addCallBack(
    onShown: ((Snackbar) -> Unit?)? = null,
    onDismissed: ((Snackbar, event: Int) -> Unit?)? = null
): Snackbar {
    addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar>() {
        override fun onShown(transientBottomBar: Snackbar?) {
            onShown?.invoke(transientBottomBar ?: return)
        }

        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
            onDismissed?.invoke(transientBottomBar ?: return, event)
        }
    })
    return this
}
//endregion
