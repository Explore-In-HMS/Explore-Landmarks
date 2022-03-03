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

package com.hms.explorelandmarks.utils

import android.util.Log
import com.hms.explorelandmarks.BuildConfig

object Logger {

    private val isAppInDebugMode: Boolean by lazy { BuildConfig.DEBUG }

    fun d(tag: String, message: String) {
        if (isAppInDebugMode) {
            Log.d(tag, message)
        }
    }

    fun e(tag: String, message: String) {
        if (isAppInDebugMode) {
            Log.e(tag, message)
        }
    }

    fun i(tag: String, message: String) {
        if (isAppInDebugMode) {
            Log.i(tag, message)
        }
    }

    fun w(tag: String, message: String) {
        if (isAppInDebugMode) {
            Log.w(tag, message)
        }
    }
}
