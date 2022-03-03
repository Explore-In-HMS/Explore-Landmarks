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

import androidx.lifecycle.liveData
import com.hms.explorelandmarks.utils.State
import com.huawei.hms.searchkit.SearchKitInstance
import com.huawei.hms.searchkit.bean.CommonSearchRequest
import com.huawei.hms.searchkit.utils.Language
import com.huawei.hms.searchkit.utils.Region
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchImageRepository @Inject constructor(
    private val searchKitInstance: SearchKitInstance
) {

    fun getImagesByQuery(
        query: String,
        accessToken: String,
        pageSize: Int = 50,
        pageNumber: Int = 1,
        timeOut: Int = 10000
    ) = liveData(Dispatchers.IO) {

        emit(State.Loading)

        searchKitInstance.setInstanceCredential(accessToken)

        val commonSearchRequest = CommonSearchRequest().apply {
            setQ(query)
            setLang(Language.ENGLISH)
            setSregion(Region.TURKEY)
            setPs(pageSize)
            setPn(pageNumber)
        }

        searchKitInstance.imageSearcher.setTimeOut(timeOut)

        val response = searchKitInstance.imageSearcher.search(commonSearchRequest)

        if (response?.data?.isNotEmpty() == true) {
            emit(State.Success(response.data))
        } else {
            emit(State.Failure(response?.code ?: "", response?.msg ?: ""))
        }
    }
}
