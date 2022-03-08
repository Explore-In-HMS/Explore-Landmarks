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

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

fun Activity.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    applicationContext.toast(message, duration)
}

fun Activity.toast(@StringRes messageRes: Int, duration: Int = Toast.LENGTH_SHORT) {
    applicationContext.toast(messageRes, duration)
}

inline fun <reified T : Activity> Activity.startActivity(
    finisCurrent: Boolean = false,
    block: Intent.() -> Unit = {}
) {
    startActivity(Intent(this, T::class.java).apply(block))
    if (finisCurrent) {
        finish()
    }
}

fun AppCompatActivity.hideToolBarSetStatusBarTransparent() {
    makeStatusBarTransparent()
    hideToolbar()
}

fun AppCompatActivity.hideToolbar() {
    supportActionBar?.hide()
}

fun AppCompatActivity.showToolbar() {
    supportActionBar?.show()
}

fun Activity.makeStatusBarTransparent() {
    window.setFlags(
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
        WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
    )
}

/**
 * The aim of this functions is that, improving the reading texts on the status bar in both dark and light mode.
 *
 *  Note that, this function has no effect on API < 23
 *
 * By this function when OS in dark mode, the text color of the status bar will be white,
 * otherwise the text color of status bar will be like gray.
 *
 * So the texts on the status bar will be read more clearly.
 * */

fun Activity.setStatusBarAppearanceConsideringUiMode() {
    WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars =
        resources?.configuration?.uiMode?.and(Configuration.UI_MODE_NIGHT_MASK) != Configuration.UI_MODE_NIGHT_YES
}

fun Activity.hideStatusBar() {
    with(WindowInsetsControllerCompat(window, window.decorView)) {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_BARS_BY_SWIPE
        hide(WindowInsetsCompat.Type.systemBars())
    }
}

fun Activity.showStatusBar() {
    WindowInsetsControllerCompat(
        window,
        window.decorView
    ).show(WindowInsetsCompat.Type.systemBars())
}

fun Activity.isAllPermissionsGranted(permissions: Array<String>): Boolean {

    var isAllGranted = true

    permissions.forEach { permission ->
        isAllGranted = isAllGranted && isPermissionGranted(permission)
    }

    return isAllGranted
}

fun Activity.isPermissionGranted(permission: String) =
    ActivityCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED

fun Activity.hideKeyboard() {
    hideKeyboard(currentFocus ?: View(this))
}
