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

package com.hms.explorelandmarks.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.hms.explorelandmarks.utils.Logger
import com.hms.explorelandmarks.utils.analytics.AnalyticsManager
import com.hms.explorelandmarks.utils.extensions.makeStatusBarTransparent
import com.hms.explorelandmarks.utils.extensions.setStatusBarAppearanceConsideringUiMode
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
abstract class BaseActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "BaseActivity"
    }

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    open fun showLoading() {
        Logger.e(TAG, "showLoading() not overriden")
    }

    open fun hideLoading() {
        Logger.e(TAG, "hideLoading() not overriden")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setToolbarAndStatusBarAppearance()
        analyticsManager.enterScreen(javaClass.simpleName)
    }

    private fun setToolbarAndStatusBarAppearance() {
        makeStatusBarTransparent()
        setStatusBarAppearanceConsideringUiMode()
    }
}
