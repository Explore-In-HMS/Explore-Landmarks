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

package com.hms.explorelandmarks.ui.authorization

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.hms.explorelandmarks.BuildConfig
import com.hms.explorelandmarks.data.model.AccessToken
import com.hms.explorelandmarks.data.network.ApiServices
import com.hms.explorelandmarks.utils.Config
import com.hms.explorelandmarks.utils.Constants
import com.hms.explorelandmarks.utils.State
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorizationRepository @Inject constructor(
    private val services: ApiServices
) {

    fun getAccessToken(): LiveData<State<AccessToken>> = liveData {

        emit(State.Loading)

        // Is app access token exist already
        if (Config.appAccessToken != null &&
            Config.appAccessToken?.accessToken.isNullOrBlank().not()
        ) {
            emit(State.Success(Config.appAccessToken))
            return@liveData
        }

        val accessTokenRequest = services.getAccessToken(
            Constants.TYPE_ACCESS_TOKEN_GRANT_TYPE,
            BuildConfig.CLIENT_ID,
            BuildConfig.CLIENT_SECRET
        )

        if (accessTokenRequest.isSuccessful.not() && accessTokenRequest.body()?.accessToken.isNullOrBlank()) {
            emit(State.Failure(accessTokenRequest.code().toString(), accessTokenRequest.message()))
            return@liveData
        }

        emit(State.Success(accessTokenRequest.body()))
    }
}
