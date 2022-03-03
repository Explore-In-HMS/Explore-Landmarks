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

package com.hms.explorelandmarks

import android.app.Application
import com.hms.explorelandmarks.utils.Constants
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.searchkit.SearchKitInstance
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidApp
class ExploreLandmarksApplication : Application() {

    @Inject
    lateinit var mlApplication: MLApplication

    @Inject
    @Named(Constants.API_KEY)
    lateinit var apiKey: String

    @Inject
    @Named(Constants.AGC_APP_ID)
    lateinit var appId: String

    override fun onCreate() {
        super.onCreate()
        mlApplication.apiKey = apiKey
        SearchKitInstance.init(this, appId)
        if (BuildConfig.DEBUG) {
            SearchKitInstance.enableLog()
        }
    }
}
