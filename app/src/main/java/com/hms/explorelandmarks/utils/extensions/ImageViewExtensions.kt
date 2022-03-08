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

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import androidx.core.content.ContextCompat
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable
import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.utils.Constants
import com.hms.explorelandmarks.utils.Constants.SHIMMER_ANIM_DURATION

private fun ImageView.getDefaultGlideBuilder(
    url: String,
    @DrawableRes errorDrawable: Int = Constants.ERROR_ANIM_RES,
    blockBeforeLoadUrl: (RequestManager.() -> Unit)? = null
): RequestBuilder<Drawable> {
    return Glide.with(this)
        .apply { blockBeforeLoadUrl?.invoke(this) }
        .load(url)
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .centerCrop()
        .error(errorDrawable)
}

fun ImageView.load(
    url: String,
    @DrawableRes errorDrawable: Int = Constants.ERROR_ANIM_RES
) {
    getDefaultGlideBuilder(url, errorDrawable)
        .into(this)
}

fun ImageView.loadWithLottieLoadingPlaceHolder(
    url: String,
    @DrawableRes errorDrawable: Int = Constants.ERROR_ANIM_RES,
    @RawRes placeHolderLottieRes: Int = Constants.LOADING_ANIM_RES
) {

    val lottieDrawable = LottieDrawable()
    LottieCompositionFactory.fromRawRes(context, placeHolderLottieRes)
        .addListener { lottieComposition ->
            lottieDrawable.composition = lottieComposition
            lottieDrawable.scale = 0.5F
            lottieDrawable.repeatCount = LottieDrawable.INFINITE
            lottieDrawable.playAnimation()
        }

    val requestOptions = RequestOptions()
        .placeholder(lottieDrawable)
        .error(errorDrawable)

    getDefaultGlideBuilder(url, errorDrawable) {
        setDefaultRequestOptions(requestOptions)
    }.into(this)
}

fun ImageView.loadWithShimmerPlaceHolder(
    url: String,
    @DrawableRes errorDrawable: Int = Constants.ERROR_ANIM_RES
) {
    val shimmer = Shimmer.ColorHighlightBuilder()
        .setBaseColor(ContextCompat.getColor(context, R.color.bg_loading))
        .setBaseAlpha(0.7f)
        .setHighlightAlpha(0.7f)
        .setHighlightColor(ContextCompat.getColor(context, R.color.mode_color))
        .setDuration(SHIMMER_ANIM_DURATION)
        .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
        .setAutoStart(true)
        .build()

    val shimmerDrawable = ShimmerDrawable()
    shimmerDrawable.setShimmer(shimmer)

    getDefaultGlideBuilder(url, errorDrawable)
        .placeholder(shimmerDrawable)
        .into(this)
}
