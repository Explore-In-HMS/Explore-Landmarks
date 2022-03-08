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

package com.hms.explorelandmarks.data.model

import com.google.gson.annotations.SerializedName

data class AccessToken(
    @SerializedName("token_type")
    private val tokenType: String,

    @SerializedName("expires_in")
    private val expiresIn: String = "", // Remaining validity period of an access token, in seconds.

    @SerializedName("access_token")
    val accessToken: String? = null,

    var obtainedTimeMillis: Long = 0
) : BaseResponse() {

    fun isAccessTokenExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val timeDifferenceBetweenObtainingTimeMillis = currentTime - obtainedTimeMillis
        val remainingValiditySeconds =
            expiresIn.toInt() - (timeDifferenceBetweenObtainingTimeMillis / 1000)
        return remainingValiditySeconds < 60 // if one minute remained to token expiration,
        // think as it is expired. Because when you send a request to service,
        // the token can expire during some processes.
        // For this reason, the request may be broken
    }
}
