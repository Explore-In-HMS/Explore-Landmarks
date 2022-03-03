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

package com.hms.explorelandmarks.utils

import androidx.annotation.DrawableRes
import androidx.annotation.RawRes
import com.hms.explorelandmarks.R

object Constants {
    const val API_KEY = "api_key"
    const val AGC_APP_ID = "agc_app_id"
    const val OAUTH_BASE_URL = "oauth_base_url"
    const val APP_NAME = "Explore Landmarks"
    const val PACKAGE_NAME = "com.hms.explorelandmarks"
    const val APP_SIGN_CERTIFICATE_SHA256 = // todo delete
        "01:CD:49:BE:24:0C:BC:70:DF:66:AF:9C:F2:D8:E6:F9:42:28:98:86:0B:21:A1:98:36:16:91:04:58:69:50:D9"
    const val LOADING_ANIM_DELAY_BEFORE_HIDE = 1000L // todo delete
    const val DURATION_TRANSITION_ANIMATION: Long = 500

    /** TRUSTED KEYRING SOURCE (SHARING) APP */
    const val KEYRING_SOURCE_APP_NAME = "KeyringDemo_SharingApp"
    const val KEYRING_SOURCE_APP_PACKAGE_NAME = "com.example.keyringdemo_sharingapp" // todo delete?
    const val KEYRING_SOURCE_APP_SIGN_CERTIFICATE_SHA256 =
        "21:82:7D:65:DC:4D:D2:CC:6B:C8:7E:58:E5:27:D8:BE:EA:3B:CF:32:77:EA:F4:46:26:2D:36:79:C9:54:51:50" // todo delete?

    const val ENDPOINT_TOKEN = "oauth2/v3/token"
    const val TYPE_ACCESS_TOKEN_GRANT_TYPE: String = "client_credentials"

    @RawRes
    const val LOADING_ANIM_RES = R.raw.lottie_loading

    @DrawableRes
    const val ERROR_ANIM_RES = R.drawable.app_icon

    @DrawableRes
    const val PROFILE_IMAGE_PLACE_HOLDER = R.drawable.ic_profile

    const val SHIMMER_ANIM_DURATION = 750L

    // DI NAMES
    const val VERTICAL_LINEAR_LAYOUT_MANAGER = "VERTICAL_LINEAR_LAYOUT_MANAGER"
    const val HORIZONTAL_LINEAR_LAYOUT_MANAGER = "HORIZONTAL_LINEAR_LAYOUT_MANAGER"
}
