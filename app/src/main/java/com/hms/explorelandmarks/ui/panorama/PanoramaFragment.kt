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

package com.hms.explorelandmarks.ui.panorama

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.data.enums.PanoramaMediaType
import com.hms.explorelandmarks.databinding.FragmentPanoramaBinding
import com.hms.explorelandmarks.ui.base.BaseFragmentWithViewModel
import com.hms.explorelandmarks.ui.base.BaseViewModel
import com.hms.explorelandmarks.utils.extensions.toast
import com.huawei.hms.panorama.PanoramaInterface
import com.huawei.hms.panorama.ResultCode
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import javax.inject.Inject

@AndroidEntryPoint
class PanoramaFragment :
    BaseFragmentWithViewModel<BaseViewModel, FragmentPanoramaBinding>(BaseViewModel::class) {

    private val args: PanoramaFragmentArgs by navArgs()

    @Inject
    lateinit var mLocalInterface: PanoramaInterface.PanoramaLocalInterface

    @Inject
    lateinit var mediaPlayer: MediaPlayer

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showPanoramicMediaByType()
    }

    private fun showPanoramicMediaByType() {
        val mediaSourceUri = args.mediaSourceId.getUriFormat()
        val panoramaImageType = args.panoramaImageType

        if (args.mediaType == PanoramaMediaType.VIDEO) {
            showPanoramicVideo(mediaSourceUri, panoramaImageType)
        } else {
            showPanoramicImage(mediaSourceUri, panoramaImageType)
        }
    }

    private fun showPanoramicVideo(mediaSourceUri: Uri, panoramaImageType: Int) {
        if (isLocalInstanceInitialized().not() || mLocalInterface.getSurface(panoramaImageType) == null) {
            showGeneralError()
            return
        }

        try {
            mediaPlayer.apply {
                setDataSource(requireActivity().applicationContext, mediaSourceUri)
                isLooping = true
                setSurface(mLocalInterface.getSurface(panoramaImageType))
                prepare()
                start()
            }
        } catch (e: IOException) {
            showGeneralError()
            return
        }

        val ratio = mediaPlayer.videoWidth / mediaPlayer.videoHeight
        mLocalInterface.setValue(PanoramaInterface.KEY_VIDEO_RATIO, ratio.toString())

        val videoView = mLocalInterface.view
        videoView?.setTouchListener()

        addViewToUI(videoView)
    }

    private fun showPanoramicImage(mediaSourceUri: Uri, panoramaImageType: Int) {
        if (isLocalInstanceInitialized() &&
            mLocalInterface.setImage(mediaSourceUri, panoramaImageType) == ResultCode.SUCCEED
        ) {
            val localInterfaceView = mLocalInterface.view
            localInterfaceView?.setTouchListener()

            addViewToUI(localInterfaceView)
        } else {
            showGeneralError()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun View.setTouchListener() {
        setOnTouchListener { _, event ->
            mLocalInterface.updateTouchEvent(event)
            true
        }
    }

    private fun isLocalInstanceInitialized() = mLocalInterface.init() == ResultCode.SUCCEED

    override fun onStop() {
        super.onStop()
        if (args.mediaType == PanoramaMediaType.VIDEO) {
            mediaPlayer.stop()
            mediaPlayer.reset()
        }
        mBinding.frmRoot.removeAllViews()
        mLocalInterface.deInit()
    }

    private fun showGeneralError() {
        toast(getString(R.string.error_general_unknown))
    }

    private fun addViewToUI(view: View) {
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        mBinding.frmRoot.addView(view)
    }

    private fun Int.getUriFormat() =
        Uri.parse("android.resource://" + context?.packageName + "/" + this)

    override fun getLayoutId() = R.layout.fragment_panorama
}
