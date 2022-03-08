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

package com.hms.explorelandmarks.ui.landmark_recognition

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.lifecycle.MutableLiveData
import androidx.navigation.NavDirections
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.databinding.FragmentLandmarkRecognitionBinding
import com.hms.explorelandmarks.ui.base.BaseFragmentWithViewModel
import com.hms.explorelandmarks.utils.Logger
import com.hms.explorelandmarks.utils.UserManager
import com.hms.explorelandmarks.utils.extensions.action
import com.hms.explorelandmarks.utils.extensions.onDismissed
import com.hms.explorelandmarks.utils.extensions.showSnackBar
import com.hms.explorelandmarks.utils.extensions.toast
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LandmarkRecognitionFragment :
    BaseFragmentWithViewModel<LandmarkRecognitionViewModel, FragmentLandmarkRecognitionBinding>(
        LandmarkRecognitionViewModel::class
    ) {
    // current permission
    private lateinit var currentRequestedPermission: String

    //region permissionLauncher
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                if (currentRequestedPermission == cameraPermission) {
                    takePhotoFromCamera()
                } else if (currentRequestedPermission == readStoragePermission) {
                    choosePhotoFromGallery()
                }
            } else {
                informUserToGrantPermissions()
            }
        }
//endregion permissionLauncher

    // A holder to store users last landmark image
    private val currentUserLandmarkImage = MutableLiveData<Bitmap?>(null)

    // A holder to store imageview of image obtaining method layout. It uses for transition animation
    private var currentImageObtainingMethodImageView: ImageView? = null

//region Launcher Variables

    private val photoTakingLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                return@registerForActivityResult
            }

            val extraData = result.data?.extras?.get("data") ?: return@registerForActivityResult

            val capturedImage = extraData as Bitmap

            landmarkImageObtainedStartAnalyze(
                true,
                capturedImage,
                mBinding.containerTakePhoto.imgTakePhoto
            )
        }

    private val photoChooserLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        if (imageUri == null) {
            return@registerForActivityResult
        }

        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ImageDecoder.decodeBitmap(
                ImageDecoder.createSource(requireContext().contentResolver, imageUri)
            )
        } else {
            MediaStore.Images.Media.getBitmap(requireContext().contentResolver, imageUri)
        }

        landmarkImageObtainedStartAnalyze(
            false,
            bitmap,
            mBinding.containerChoosePhoto.imgChoosePhoto
        )
    }

//endregion

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkUserLoginStatus()

        super.onViewCreated(view, savedInstanceState)

        setUserLandmarkImageObserver()
    }

//region request permission

    private fun isPermissionGranted(permission: String) =
        ActivityCompat.checkSelfPermission(
            requireActivity(),
            permission
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestPermission(permission: String) {
        requestPermissionLauncher.launch(permission)
    }

    private fun informUserToGrantPermissions() {
        requireActivity().window.decorView
            .showSnackBar(R.string.warning_grant_permissions)
            .action(R.string.grant) {
                requestPermission(currentRequestedPermission)
            }.onDismissed { _, event ->
                if (event == BaseTransientBottomBar.BaseCallback.DISMISS_EVENT_ACTION) { // user click action button
                    return@onDismissed
                }

                // User was not grant all permissions. Because of that app will close
                analyticsManager.permissionNotAllowedAppClosed()
                Logger.w(TAG, "User did not grant permissions. So app will close ")
            }
    }
//endregion

//region Analyze Landmark Image

    private fun setUserLandmarkImageObserver() {
        currentUserLandmarkImage.observe(viewLifecycleOwner) { landmarkImage ->
            if (landmarkImage == null) {
                return@observe
            }

            analyzeTheLandmark(landmarkImage)
        }
    }

//region Analyze Landmark Image

    private fun landmarkImageObtainedStartAnalyze(
        isFromCamera: Boolean,
        landmarkImage: Bitmap,
        clickedImageViewForImageObtaining: ImageView
    ) {
        analyticsManager.userPreparedImage(isFromCamera)

        bindLandmarkImageToUIAndStartAnalyze(landmarkImage, clickedImageViewForImageObtaining)
    }

    private fun bindLandmarkImageToUIAndStartAnalyze(
        landmarkImage: Bitmap,
        clickedImageViewForImageObtaining: ImageView
    ) {
        currentImageObtainingMethodImageView = clickedImageViewForImageObtaining
        currentImageObtainingMethodImageView?.setImageBitmap(landmarkImage)

        startAnalyzingTheLandmark(landmarkImage)
    }

    private fun startAnalyzingTheLandmark(landmarkImage: Bitmap) {
        // Trigger to start analyzing to the landmark
        currentUserLandmarkImage.value = landmarkImage
    }

    private fun analyzeTheLandmark(userLandmarkImage: Bitmap) {
        observeStateLiveDataAndDoOnSuccess(mViewModel.analyzeLandmark(userLandmarkImage)) { estimationList ->
            if (estimationList.isNullOrEmpty()) {
                toast(R.string.error_general_unknown)
                return@observeStateLiveDataAndDoOnSuccess
            }

            val firstEstimation: MLRemoteLandmark = estimationList[0]

            analyticsManager.landmarkRecognized(landmarkName = firstEstimation.landmark)

            clearCurrentState()

            navigateToLandmarkDetail(firstEstimation, userLandmarkImage)
        }
    }

    private fun clearCurrentState() {
        currentUserLandmarkImage.value = null
    }

    private fun navigateToLandmarkDetail(landmark: MLRemoteLandmark, userLandmarkImage: Bitmap) {
        val extras = FragmentNavigatorExtras(
            (
                currentImageObtainingMethodImageView
                    ?: mBinding.containerChoosePhoto.imgChoosePhoto
                ) to "detailImageTransitionName"
        )

        findNavController().navigate(
            landmark.toLandmarkDetailNavDirections(userLandmarkImage),
            extras
        )
    }

    private fun MLRemoteLandmark.toLandmarkDetailNavDirections(userLandmarkImage: Bitmap): NavDirections {
        val landmarkLocation = positionInfos[0]
        val landmarkLat = landmarkLocation.lat
        val landmarkLng = landmarkLocation.lng

        return LandmarkRecognitionFragmentDirections.actionLandmarkRecognitionToLandmarkDetail(
            landmark,
            landmarkLat.toFloat(),
            landmarkLng.toFloat(),
            userLandmarkImage
        )
    }

//endregion

//region Landmark Image Obtaining

    private fun choosePhotoFromGallery() {
        photoChooserLauncher.launch("image/*")
    }

    private fun takePhotoFromCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        photoTakingLauncher.launch(takePictureIntent)
    }

//endregion

    // region Common functions
    override fun setListeners() {
        mBinding.containerTakePhoto.cvRootTakePhoto.setOnClickListener {
            requestPermissionWithGrantsCallback(cameraPermission) {
                takePhotoFromCamera()
            }
        }

        mBinding.containerChoosePhoto.cvRootChoosePhoto.setOnClickListener {
            requestPermissionWithGrantsCallback(readStoragePermission) {
                choosePhotoFromGallery()
            }
        }
    }

    private fun requestPermissionWithGrantsCallback(
        permission: String,
        permissionGrantsCallback: () -> Unit
    ) {
        setCurrentPermission(permission)
        if (isPermissionGranted(permission)) {
            permissionGrantsCallback()
        } else {
            requestPermission(permission)
        }
    }

    private fun setCurrentPermission(permission: String) {
        currentRequestedPermission = permission
    }

    private fun checkUserLoginStatus() {
        if (isUserLogged().not()) {
            findNavController().navigate(R.id.action_landmarkRecognition_to_authentication)
        }
    }

    private fun isUserLogged() = UserManager.isUserLoggedIn()

    override fun getLayoutId() = R.layout.fragment_landmark_recognition

    companion object {
        private const val TAG = "LandmarkRecognitionFragment"

        // permission types
        private const val cameraPermission = Manifest.permission.CAMERA
        private const val readStoragePermission = Manifest.permission.READ_EXTERNAL_STORAGE
    }

//endregion
}
