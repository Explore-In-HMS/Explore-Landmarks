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

package com.hms.explorelandmarks.ui.image_gallery

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.databinding.ItemLandmarkBinding
import com.hms.explorelandmarks.utils.CommonAdapterClickListener
import com.huawei.hms.searchkit.bean.ImageItem
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class LandmarkImageGalleryPagerAdapter @Inject constructor(val clickListener: CommonAdapterClickListener) :
    ListAdapter<ImageItem, LandmarkImageGalleryPagerAdapter.ImageViewHolder>(
        AsyncDifferConfig.Builder(ImageDiffCallback()).build()
    ) {

    class ImageViewHolder(private val mBinding: ItemLandmarkBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(imageItem: ImageItem) {
            mBinding.item = imageItem
        }
    }

    override fun onBindViewHolder(imageViewHolder: ImageViewHolder, position: Int) {
        val item = getItem(position)
        imageViewHolder.bind(item)
        imageViewHolder.itemView.setOnClickListener { clickListener.onClick(position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val mBinding = DataBindingUtil.inflate<ItemLandmarkBinding>(
            LayoutInflater.from(parent.context), R.layout.item_landmark, parent, false
        )
        return ImageViewHolder(mBinding)
    }

    private class ImageDiffCallback : DiffUtil.ItemCallback<ImageItem>() {
        override fun areItemsTheSame(oldItem: ImageItem, newItem: ImageItem) =
            oldItem.sourceImage == newItem.sourceImage

        override fun areContentsTheSame(oldItem: ImageItem, newItem: ImageItem) =
            oldItem.sourceImage.imageContentUrl == newItem.sourceImage.imageContentUrl
    }
}
