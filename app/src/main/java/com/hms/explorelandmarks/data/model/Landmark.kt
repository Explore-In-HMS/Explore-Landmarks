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

package com.hms.explorelandmarks.data.model

import android.graphics.Bitmap
import androidx.annotation.RawRes
import com.huawei.hms.mlsdk.landmark.bo.Location

data class Landmark(
    val landmarkName: String,
    val landmarkDescription: String,
    val location: Location? = null,
    val landmarkRecognizedImage: Bitmap? = null,
    @RawRes val landmarkPanoramicImage: Int? = null,
    @RawRes val landmarkPanoramicVideo: Int? = null
)
