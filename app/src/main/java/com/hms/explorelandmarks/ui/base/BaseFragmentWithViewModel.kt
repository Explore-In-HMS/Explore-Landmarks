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

package com.hms.explorelandmarks.ui.base

import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LiveData
import com.hms.explorelandmarks.utils.BaseEventState
import com.hms.explorelandmarks.utils.State
import com.hms.explorelandmarks.utils.extensions.getViewModelBySharingStatus
import com.hms.explorelandmarks.utils.extensions.triggerUIStateByObservingStateLiveData
import kotlin.reflect.KClass

abstract class BaseFragmentWithViewModel<VM : BaseViewModel, VDB : ViewDataBinding>(
    viewModelClass: KClass<VM>
) : BaseFragment<VDB>() {

    protected val mViewModel by lazy { getViewModelBySharingStatus(viewModelClass, willViewModelBeShared()) }
    open fun willViewModelBeShared() = false

    fun <T> observeStateLiveDataAndDoOnSuccess(
        liveData: LiveData<State<T>>,
        onFailureCallback: ((errorCode: String?, errorMessage: String?, eventState: BaseEventState?) -> Unit)? = null,
        onSuccessCallback: ((data: T?) -> Unit)?
    ) {
        triggerUIStateByObservingStateLiveData(
            liveData,
            mViewModel.javaClass.simpleName,
            onFailureCallback,
            onSuccessCallback = onSuccessCallback
        )
    }
}
