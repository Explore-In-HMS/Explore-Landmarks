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

package com.hms.explorelandmarks.di

import android.content.Context
import com.hms.explorelandmarks.utils.analytics.AnalyticsManager
import com.hms.explorelandmarks.utils.analytics.HiAnalyticsImpl
import com.huawei.hms.analytics.HiAnalytics
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.analytics.HiAnalyticsTools
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {

    // Analytics Kit
    @Provides
    @Singleton
    fun provideHiAnalyticsInstance(
        @ApplicationContext context: Context
    ): HiAnalyticsInstance {
        HiAnalyticsTools.enableLog()
        return HiAnalytics.getInstance(context)
    }

    @Provides
    @Singleton
    fun provideHiAnalyticsImpl(hiAnalyticsInstance: HiAnalyticsInstance): HiAnalyticsImpl =
        HiAnalyticsImpl(hiAnalyticsInstance)

    @Provides
    @Singleton
    fun provideAnalyticsManager(hiAnalyticsImpl: HiAnalyticsImpl): AnalyticsManager =
        AnalyticsManager(hiAnalyticsImpl)
}
