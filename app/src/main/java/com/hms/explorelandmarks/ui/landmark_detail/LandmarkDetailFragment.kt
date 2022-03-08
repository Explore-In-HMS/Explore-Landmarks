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

package com.hms.explorelandmarks.ui.landmark_detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RawRes
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.data.enums.PanoramaMediaType
import com.hms.explorelandmarks.data.model.Landmark
import com.hms.explorelandmarks.databinding.FragmentLandmarkDetailBinding
import com.hms.explorelandmarks.ui.base.BaseFragmentWithViewModel
import com.hms.explorelandmarks.utils.Constants
import com.huawei.hms.mlsdk.landmark.bo.Location
import com.huawei.hms.panorama.PanoramaInterface
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class LandmarkDetailFragment :
    BaseFragmentWithViewModel<LandmarkDetailViewModel, FragmentLandmarkDetailBinding>(LandmarkDetailViewModel::class) {

    private val args: LandmarkDetailFragmentArgs by navArgs()

    @Inject
    lateinit var panoramaInstance: PanoramaInterface

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.vm = mViewModel
        getRecognizedLandmarkInformation()
    }

    private fun getRecognizedLandmarkInformation() {
        val getLandmarkInformationLiveData =
            mViewModel.getLandmarkInformation(getLandmarkInstanceByFragmentArgs())

        observeStateLiveDataAndDoOnSuccess(getLandmarkInformationLiveData) {
            if (it == null) return@observeStateLiveDataAndDoOnSuccess

//           landmark informations obtained successfully"
        }
    }

    //region PANORAMA IMAGE & VIDEO
    private fun showPanoramicImage(@RawRes panoramicImageResId: Int?) {
        if (panoramicImageResId == null) {
            return
        }

        navigatePanoramaScreen(
            mediaType = PanoramaMediaType.IMAGE,
            panoramicImageResId,
            getRandomPanoramaImageType()
        )
    }

    private fun showPanoramicVideo(landmarkPanoramicVideo: Int?) {
        if (landmarkPanoramicVideo == null) {
            return
        }

        navigatePanoramaScreen(
            mediaType = PanoramaMediaType.VIDEO,
            landmarkPanoramicVideo,
            getRandomPanoramaImageType()
        )
    }

    private fun navigatePanoramaScreen(
        mediaType: PanoramaMediaType,
        @RawRes mediaSourceId: Int,
        panoramaImageType: Int
    ) {
        val action =
            LandmarkDetailFragmentDirections.actionLandmarkDetailFragmentToPanoramaFragment(
                mediaType = mediaType,
                mediaSourceId = mediaSourceId,
                panoramaImageType = panoramaImageType
            )

        findNavController().navigate(action)
    }

    private fun getRandomPanoramaImageType() =
        if (Random.nextInt() % 2 == 0) PanoramaInterface.IMAGE_TYPE_SPHERICAL else PanoramaInterface.IMAGE_TYPE_RING
//endregion

    private fun getLandmarkInstanceByFragmentArgs(): Landmark {
        val argsLocation = Location().apply {
            latitude = args.locationLat.toDouble()
            longitude = args.locationLat.toDouble()
        }

        return Landmark(
            args.landmarkName,
            "",
            landmarkRecognizedImage = args.userLandmarkImage,
            location = argsLocation
        )
    }

    override fun setListeners() {
        mBinding.fabShowPanoramicImage.setOnClickListener {
            analyticsManager.clickedPanoramicImage(args.landmarkName)
            showPanoramicImage(getCurrentLandmarkPanoramicImage())
        }

        mBinding.fabShowPanoramicVideo.setOnClickListener {
            analyticsManager.clickedPanoramicVideo(args.landmarkName)
            showPanoramicVideo(getCurrentLandmarkPanoramicVideo())
        }
        mBinding.fabImageGallery.setOnClickListener {
            val action =
                LandmarkDetailFragmentDirections.actionLandmarkDetailFragmentToImageGalleryFragment(
                    args.landmarkName
                )

            findNavController().navigate(action)
        }
    }

    private fun getCurrentLandmarkPanoramicVideo(): Int? {
        return mViewModel.currentLandmark.value?.landmarkPanoramicVideo
    }

    private fun getCurrentLandmarkPanoramicImage(): Int? {
        return mViewModel.currentLandmark.value?.landmarkPanoramicImage
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val transition =
            TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move)
                .apply {
                    duration = Constants.DURATION_TRANSITION_ANIMATION
                }

        sharedElementEnterTransition = transition

        sharedElementReturnTransition = transition

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun getLayoutId() = R.layout.fragment_landmark_detail
}
