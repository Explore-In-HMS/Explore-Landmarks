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

import android.graphics.Bitmap
import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.hms.explorelandmarks.data.enums.ImageLoadingType
import com.hms.explorelandmarks.utils.Constants.PROFILE_IMAGE_PLACE_HOLDER
import com.hms.explorelandmarks.utils.extensions.load
import com.hms.explorelandmarks.utils.extensions.loadWithLottieLoadingPlaceHolder
import com.hms.explorelandmarks.utils.extensions.loadWithShimmerPlaceHolder

@BindingAdapter(value = ["app:loadImage", "app:loadingType"], requireAll = false)
fun loadImage(imageView: ImageView, url: String, loadingType: ImageLoadingType? = null) {
    when (loadingType) {
        ImageLoadingType.WITH_LOTTIE_LOADING_ANIM -> {
            imageView.loadWithLottieLoadingPlaceHolder(url)
        }
        ImageLoadingType.WITH_SHIMMER_PLACE_HOLDER -> {
            imageView.loadWithShimmerPlaceHolder(url)
        }
        else -> {
            imageView.load(url)
        }
    }
}

@BindingAdapter("app:loadProfileImage")
fun loadProfileImage(imageView: ImageView, profileImage: String?) {
    val profileImagePlaceHolder = PROFILE_IMAGE_PLACE_HOLDER

    if (profileImage == null) {
        imageView.setImageResource(profileImagePlaceHolder)
        return
    }

    imageView.load(profileImage, profileImagePlaceHolder)
}

@BindingAdapter("app:onClick")
fun setClickListener(view: View, onClick: () -> Unit) {
    view.setOnClickListener {
        onClick.invoke()
    }
}

@BindingAdapter("app:setBitmap")
fun setImageBitmap(imageView: ImageView, bitmap: Bitmap?) {
    if (bitmap == null) {
        return
    }
    imageView.setImageBitmap(bitmap)
}
