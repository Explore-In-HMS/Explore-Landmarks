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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.di.ResourceProvider
import com.hms.explorelandmarks.ui.authorization.AuthorizationRepository
import com.hms.explorelandmarks.ui.base.BaseViewModel
import com.hms.explorelandmarks.utils.Config
import com.hms.explorelandmarks.utils.State
import com.huawei.hms.searchkit.bean.ImageItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

typealias SearchImagesState = State<List<ImageItem>>

@HiltViewModel
class SearchImageViewModel @Inject constructor(
    private val searchImageRepository: SearchImageRepository,
    private val authorizationRepository: AuthorizationRepository,
    private val resourceProvider: ResourceProvider,
) : BaseViewModel() {

    fun getImagesByQuery(query: String) =
        checkAccessTokenAndStartImageSearch(query)

    private fun checkAccessTokenAndStartImageSearch(query: String): LiveData<SearchImagesState> {

        val resultState = MediatorLiveData<SearchImagesState>().also { it.value = State.Loading }

        val accessTokenLiveData = authorizationRepository.getAccessToken()

        resultState.addSource(accessTokenLiveData) {
            when (it) {
                is State.Success -> {
                    Config.appAccessToken = it.data
                    resultState.addImagesByQueryLiveData(query, it.data?.accessToken ?: "")
                }
                is State.Failure -> {
                    resultState.value =
                        State.Failure(
                            "",
                            resourceProvider.getString(R.string.error_general_unknown)
                        )
                }
                else -> Unit
            }
        }

        return resultState
    }

    private fun MediatorLiveData<SearchImagesState>.addImagesByQueryLiveData(
        query: String,
        accessToken: String
    ) {
        addSource(
            searchImageRepository.getImagesByQuery(
                query,
                accessToken
            )
        ) {
            value = it
        }
    }
}
