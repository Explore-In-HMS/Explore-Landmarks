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

package com.hms.explorelandmarks.utils.extensions

import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.createViewModelLazy
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.ui.base.BaseActivity
import com.hms.explorelandmarks.utils.BaseEventState
import com.hms.explorelandmarks.utils.Logger
import com.hms.explorelandmarks.utils.State
import kotlin.reflect.KClass

fun Fragment.toast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    context?.toast(message, duration)
}

fun Fragment.toast(@StringRes resId: Int, duration: Int = Toast.LENGTH_SHORT) {
    toast(resources.getString(resId), duration)
}

fun Fragment.showGeneralErrorToast() {
    toast(R.string.error_general_unknown)
}

fun Fragment.hideKeyboard() {
    view?.let { activity?.hideKeyboard(it) }
}

/**
 *
 * This function is responsible to creating an instance of given ViewModel class by the ViewModel sharing status.
 * If you will use ViewModel as a SharedViewModel and you need to use as Fragment.activityModels() you should pass true to isSharedViewModel param
 *  otherwise if you want to use such as Fragment.viewmodels() you should pass false.
 *
 * ```
 * class MyFragment : Fragment() {
 *    ---
 *    val myViewModel = getViewModelBySharingStatus(MyViewModel::class, true) // Such as  val myViewModel by activityViewModels<MyViewModel::class>()
 *    ---
 *    ---
 *    val myViewModel = getViewModelBySharingStatus(MyViewModel::class, false) // Such as  val myViewModel by viewModels<MyViewModel::class>()
 *    ---
 *    ---
 * }
 *  ```
 * */

fun <VM : ViewModel> Fragment.getViewModelBySharingStatus(
    vmClass: KClass<VM>,
    isSharedViewModel: Boolean,
    factoryProducer: (() -> ViewModelProvider.Factory)? = null,
): VM {
    val ownerProducer: () -> ViewModelStoreOwner = { if (isSharedViewModel) requireActivity() else this }
    val viewModel by createViewModelLazy(vmClass, { ownerProducer().viewModelStore }, factoryProducer)
    return viewModel
}

fun <T> Fragment.triggerUIStateByObservingStateLiveData(
    liveData: LiveData<State<T>>,
    logTag: String? = null,
    onFailureCallback: ((errorCode: String?, errorMessage: String?, eventState: BaseEventState?) -> Unit)? = null,
    showCustomLoadingCallBack: (() -> Unit)? = null,
    hideCustomLoadingCallBack: (() -> Unit)? = null,
    onSuccessCallback: ((data: T?) -> Unit)?
) {
//region GENERAL UI STATE BEHAVIOURS

    fun showLoading() {
        (activity as BaseActivity?)?.showLoading()
    }

    fun hideLoading() {
        (activity as BaseActivity?)?.hideLoading()
    }

    fun requestFailure(errorCode: String?, errorMessage: String?, eventState: BaseEventState?) {
        val message = "Request Failure ☹. " +
            (if (errorCode.isNullOrEmpty().not()) "Error Code :$errorCode" else "") +
            (if (errorMessage.isNullOrEmpty().not()) "Error Message :$errorMessage" else "") +
            (if (eventState != null) "Event State $eventState" else "")

        Logger.e(logTag ?: javaClass.simpleName, message)
        toast(message)
    }

//endregion

    val showLoadingBlock: (() -> Unit) = showCustomLoadingCallBack ?: ::showLoading
    val hideLoadingBlock: (() -> Unit) = hideCustomLoadingCallBack ?: ::hideLoading
    val requestFailureBlock: ((errorCode: String?, errorMessage: String?, eventState: BaseEventState?) -> Unit) =
        onFailureCallback ?: ::requestFailure

    liveData.observe(viewLifecycleOwner) {
        when (it) {
            is State.Loading -> {
                showLoadingBlock.invoke()
            }
            is State.Failure -> {
                hideLoadingBlock.invoke()
                requestFailureBlock.invoke(it.errorCode, it.errorMessage, it.eventState)
            }
            is State.Success -> {
                hideLoadingBlock.invoke()
                onSuccessCallback?.invoke(it.data)
            }
            else -> Unit
        }
    }
}
