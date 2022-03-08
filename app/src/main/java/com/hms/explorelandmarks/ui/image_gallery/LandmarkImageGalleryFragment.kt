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

import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.databinding.FragmentLandmarkImageGalleryBinding
import com.hms.explorelandmarks.ui.base.BaseFragmentWithViewModel
import com.hms.explorelandmarks.utils.extensions.show
import com.hms.explorelandmarks.utils.extensions.toast
import com.hms.explorelandmarks.utils.viewpager_transformations.CubeOutRotationTransformation
import com.huawei.hms.searchkit.bean.ImageItem
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.abs
import kotlin.random.Random

@AndroidEntryPoint
class LandmarkImageGalleryFragment :
    BaseFragmentWithViewModel<SearchImageViewModel, FragmentLandmarkImageGalleryBinding>(SearchImageViewModel::class) {

    private var currentImageList = emptyList<ImageItem>()

    @Inject
    lateinit var imageGalleryAdapter: LandmarkImageGalleryPagerAdapter

    private val args: LandmarkImageGalleryFragmentArgs by navArgs()

    override fun initObservers() {
        val getImagesByQueryLiveData = mViewModel.getImagesByQuery(args.imageQuery)
        observeStateLiveDataAndDoOnSuccess(getImagesByQueryLiveData) { imageList ->
            if (imageList?.isNotEmpty() == true) {
                setImagesToGallery(imageList)
            }
        }
    }

    private fun setImagesToGallery(imageList: List<ImageItem>) {
        submitImagesToGallery(imageList)
        attachIndicatorToViewPager()
    }

    private fun submitImagesToGallery(imageList: List<ImageItem>) {
        currentImageList = imageList
        imageGalleryAdapter.submitList(imageList)
    }

    private fun attachIndicatorToViewPager() {
        mBinding.indicator.attachToPager(mBinding.vpImageGallery)
        mBinding.cvContainerIndicator.show()
    }

    override fun initUI() {
        mBinding.vpImageGallery.adapter = imageGalleryAdapter

        mBinding.vpImageGallery.clipToPadding = false
        mBinding.vpImageGallery.clipChildren = false
        mBinding.vpImageGallery.offscreenPageLimit = 3
        mBinding.vpImageGallery.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        mBinding.vpImageGallery.setPageTransformer(getRandomPageTransformer())
    }

    override fun setListeners() {
        imageGalleryAdapter.clickListener.onItemClick = { position ->
            landmarkImageClicked(position)
        }
    }

    private fun landmarkImageClicked(position: Int) {
        toast("Clicked ${currentImageList[position].title}")
    }

    private fun getRandomPageTransformer(): ViewPager2.PageTransformer {
        return if (Random.nextInt() % 2 == 0) {
            getCubeOutRotationTransformation()
        } else {
            getCompositeCubeTransformer()
        }
    }

    private fun getCubeOutRotationTransformation() = CubeOutRotationTransformation()

    private fun getCompositeCubeTransformer() = CompositePageTransformer().apply {
        addTransformer(MarginPageTransformer(48))
        addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = (0.95f + r * 0.05f)
        }
    }

    override fun getLayoutId() = R.layout.fragment_landmark_image_gallery
}
