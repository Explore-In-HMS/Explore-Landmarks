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

package com.hms.explorelandmarks.utils.viewpager_transformations

import android.view.View
import androidx.viewpager2.widget.ViewPager2

class CubeInRotationTransformation : ViewPager2.PageTransformer {
    override fun transformPage(page: View, position: Float) {
        page.cameraDistance = 20000f
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            page.alpha = 0f
        } else if (position <= 0) { // [-1,0]
            page.alpha = 1f
            page.pivotX = page.width.toFloat()
            page.rotationY = 90 * Math.abs(position)
        } else if (position <= 1) { // (0,1]
            page.alpha = 1f
            page.pivotX = 0f
            page.rotationY = -90 * Math.abs(position)
        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            page.alpha = 0f
        }
    }
}
