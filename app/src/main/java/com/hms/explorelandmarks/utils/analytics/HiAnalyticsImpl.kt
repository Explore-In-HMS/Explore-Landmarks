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

package com.hms.explorelandmarks.utils.analytics

import android.os.Bundle
import com.huawei.hms.analytics.HiAnalyticsInstance
import com.huawei.hms.support.api.keyring.credential.Credential
import javax.inject.Singleton

@Singleton
class HiAnalyticsImpl constructor(
    private val hiAnalytics: HiAnalyticsInstance
) : IAnalyticsEvents {

    companion object {
        private const val USER_NAME = "user_name"
        private const val DISPLAY_NAME = "display_name"
        private const val SCREEN_NAME = "screen_name"
        private const val PERMISSIONS = "permission"
        private const val IMAGE_OBTAINED_FROM = "image_obtained_from"
        private const val CAMERA = "camera"
        private const val GALLERY = "gallery"
        private const val LANDMARK_NAME = "landmark_name"

        private const val EVENT_USER_LOGIN = "user_login"
        private const val EVENT_ENTER_SCREEN = "enter_screen"
        private const val EVENT_APP_CLOSED = "app_closed"
        private const val EVENT_LANDMARK_IMAGE_PREPARED = "landmark_image_prepared"
        private const val EVENT_LANDMARK_RECOGNIZED = "landmark_recognized"
        private const val EVENT_CLICKED_PANORAMIC_IMAGE = "clicked_panoramic_image"
        private const val EVENT_CLICKED_PANORAMIC_VIDEO = "clicked_panoramic_video"
    }

    override fun userLogin(userCredential: Credential) {
        hiAnalytics.setUserProfile(USER_NAME, userCredential.username)
        hiAnalytics.setUserProfile(DISPLAY_NAME, userCredential.displayName)

        val params = Bundle().apply {
            putString(USER_NAME, userCredential.username)
            putString(DISPLAY_NAME, userCredential.displayName)
        }

        hiAnalytics.onEvent(EVENT_USER_LOGIN, params)
    }

    override fun userLogout() {
        // "Not yet implemented"
    }

    override fun enterScreen(screen: String) {
        val params = Bundle().apply {
            putString(SCREEN_NAME, screen)
        }

        hiAnalytics.onEvent(EVENT_ENTER_SCREEN, params)
    }

    override fun permissionNotAllowedAppClosed() {
        val params = Bundle().apply {
            putString(PERMISSIONS, "User not allowed all permissions")
        }

        hiAnalytics.onEvent(EVENT_APP_CLOSED, params)
    }

    override fun landmarkRecognized(landmarkName: String) {
        val params = Bundle().apply {
            putString(LANDMARK_NAME, landmarkName)
        }

        hiAnalytics.onEvent(EVENT_LANDMARK_RECOGNIZED, params)
    }

    override fun userPreparedImage(isTakeFromCamera: Boolean) {
        val params = Bundle().apply {
            putString(IMAGE_OBTAINED_FROM, if (isTakeFromCamera) CAMERA else GALLERY)
        }

        hiAnalytics.onEvent(EVENT_LANDMARK_IMAGE_PREPARED, params)
    }

    override fun clickedPanoramicImage(landmarkName: String) {
        val params = Bundle().apply {
            putString(LANDMARK_NAME, landmarkName)
        }

        hiAnalytics.onEvent(EVENT_CLICKED_PANORAMIC_IMAGE, params)
    }

    override fun clickedPanoramicVideo(landmarkName: String) {
        val params = Bundle().apply {
            putString(LANDMARK_NAME, landmarkName)
        }

        hiAnalytics.onEvent(EVENT_CLICKED_PANORAMIC_VIDEO, params)
    }
}
