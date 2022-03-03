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
import android.media.MediaPlayer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hms.explorelandmarks.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Named

@Module
@InstallIn(FragmentComponent::class)
object FragmentModule {

    @Provides
    fun provideMediaPlayer(): MediaPlayer = MediaPlayer()

    @Provides
    @Named(Constants.VERTICAL_LINEAR_LAYOUT_MANAGER)
    fun provideVerticalLinearLayoutManager(@ApplicationContext context: Context) =
        LinearLayoutManager(context, RecyclerView.VERTICAL, false)

    @Provides
    @Named(Constants.HORIZONTAL_LINEAR_LAYOUT_MANAGER)
    fun provideHorizontalLinearLayoutManager(@ApplicationContext context: Context) =
        LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
}
