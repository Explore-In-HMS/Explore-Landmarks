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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.hms.explorelandmarks.data.model.Landmark
import com.hms.explorelandmarks.ui.base.BaseViewModel
import com.hms.explorelandmarks.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LandmarkDetailViewModel @Inject constructor(
    private val landmarksRepository: ILandmarksRepository
) : BaseViewModel() {

    private val landmarkInformationState = MutableLiveData<State<Landmark>>(null)

    private val _currentLandmark = MutableLiveData<Landmark>(null)
    val currentLandmark: LiveData<Landmark>
        get() = _currentLandmark

    fun getLandmarkInformation(recognizedLandmark: Landmark): LiveData<State<Landmark>> {
        if (isLandmarkInformationAlreadyObtained(recognizedLandmark)) {
            return landmarkInformationState
        }

        _currentLandmark.value = recognizedLandmark // Update Landmark Detail UI

        landmarkInformationState.value = State.Loading

        viewModelScope.launch {
            landmarksRepository.getLandmarkInformation(recognizedLandmark).collect { state ->
                saveLandmarkInformationState(state)
            }
        }

        return landmarkInformationState
    }

    private fun saveLandmarkInformationState(state: State<Landmark>) {
        if (state is State.Success) {
            state.data?.let { _currentLandmark.value = it }
        }
        landmarkInformationState.value = state
    }

    private fun isLandmarkInformationAlreadyObtained(recognizedLandmark: Landmark): Boolean {
        if (isLandmarkInformationStateIsSuccess().not()) {
            return false
        }

        val lastLandmarkInformation = landmarkInformationState.value as State.Success

        val lastSuccessfullyObtainedLandmarkName =
            lastLandmarkInformation.data?.landmarkName ?: "CT"
        val currentLandmarkName = recognizedLandmark.landmarkName

        if (lastSuccessfullyObtainedLandmarkName == currentLandmarkName) {
            // Landmark Information Already Exist. Maybe we need update current image
            updateCurrentLandmarkImageIfImagesNotSame(lastLandmarkInformation, recognizedLandmark)
            return true
        } else {
            return false
        }
    }

    private fun updateCurrentLandmarkImageIfImagesNotSame(
        landmarkInfoState: State<Landmark>?,
        recognizedLandmark: Landmark
    ) {
        if (isLandmarkInformationStateIsSuccess().not()) {
            return
        }

        val landmarkInformationState = (landmarkInfoState as State.Success)

        val lastSuccessfullyObtainedLandmarkImage =
            landmarkInformationState.data?.landmarkRecognizedImage
        val currentLandmarkImage = recognizedLandmark.landmarkRecognizedImage

        if (lastSuccessfullyObtainedLandmarkImage == null || currentLandmarkImage == null) {
            return
        }

        val isLandmarkImagesSame =
            lastSuccessfullyObtainedLandmarkImage.sameAs(currentLandmarkImage)

        if (isLandmarkImagesSame.not()) {
            val updatedLandmarkState =
                landmarkInformationState.data.copy(landmarkRecognizedImage = recognizedLandmark.landmarkRecognizedImage)
            saveLandmarkInformationState(landmarkInformationState.copy(updatedLandmarkState))
        }
    }

    private fun isLandmarkInformationStateIsSuccess(): Boolean {
        return (landmarkInformationState.value is State.Success)
    }
}
