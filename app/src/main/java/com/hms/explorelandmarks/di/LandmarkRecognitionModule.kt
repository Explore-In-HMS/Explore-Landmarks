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

import com.huawei.hms.mlsdk.MLAnalyzerFactory
import com.huawei.hms.mlsdk.common.MLApplication
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object LandmarkRecognitionModule {

    // Machine Learning Kit
    @Provides
    @Singleton
    fun provideMLApplicationInstance(): MLApplication = MLApplication.getInstance()

    @Provides
    @Singleton
    fun provideMLAnalyzerFactory(): MLAnalyzerFactory = MLAnalyzerFactory.getInstance()

    @Provides
    @Singleton
    fun provideRemoteLandmarkAnalyzer(factory: MLAnalyzerFactory): MLRemoteLandmarkAnalyzer =
        factory.remoteLandmarkAnalyzer
}
