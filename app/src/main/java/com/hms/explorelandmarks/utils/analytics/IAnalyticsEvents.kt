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

import com.huawei.hms.support.api.keyring.credential.Credential

interface IAnalyticsEvents {

    fun userLogout()

    fun enterScreen(screen: String)

    fun permissionNotAllowedAppClosed()

    fun userLogin(userCredential: Credential)

    fun landmarkRecognized(landmarkName: String)

    fun userPreparedImage(isTakeFromCamera: Boolean)

    fun clickedPanoramicImage(landmarkName: String)

    fun clickedPanoramicVideo(landmarkName: String)
}
