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

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hms.explorelandmarks.ui.base.BaseViewModel
import com.hms.explorelandmarks.utils.Logger
import com.hms.explorelandmarks.utils.State
import com.huawei.hms.mlsdk.common.MLException
import com.huawei.hms.mlsdk.common.MLFrame
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmark
import com.huawei.hms.mlsdk.landmark.MLRemoteLandmarkAnalyzer
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.IOException
import javax.inject.Inject

typealias AnalyzerResult = State<List<MLRemoteLandmark>>

@HiltViewModel
class LandmarkRecognitionViewModel @Inject constructor(
    private val remoteLandmarkAnalyzer: MLRemoteLandmarkAnalyzer
) : BaseViewModel() {

    fun analyzeLandmark(bitmap: Bitmap): LiveData<AnalyzerResult> {

        val result = MutableLiveData<AnalyzerResult>(State.Loading)

        val mlFrame = MLFrame.Creator().setBitmap(bitmap).create()

        fun analyzingFailure(errorCode: String?, errorMessage: String?) {
            result.value = State.Failure(errorCode, errorMessage)
            stopLandmarkAnalyzer()
        }

        val task = remoteLandmarkAnalyzer.asyncAnalyseFrame(mlFrame)

        task.addOnSuccessListener { remoteLandmarkList ->
            result.value = State.Success(remoteLandmarkList)
            stopLandmarkAnalyzer()
        }.addOnFailureListener { e ->
            try {
                val mlException = e as MLException
                analyzingFailure(mlException.errCode.toString(), mlException.message)
            } catch (exception: Exception) {
                analyzingFailure("", exception.localizedMessage)
            }
        }

        return result
    }

    private fun stopLandmarkAnalyzer() {
        try {
            remoteLandmarkAnalyzer.stop()
        } catch (ioException: IOException) {
            Logger.e(
                javaClass.simpleName,
                "Stopping analyzer failed. Message ${ioException.localizedMessage}"
            )
            // Exception handling.
        }
    }
}
