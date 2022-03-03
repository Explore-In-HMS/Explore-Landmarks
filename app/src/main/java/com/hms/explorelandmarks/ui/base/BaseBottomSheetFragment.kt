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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.hms.explorelandmarks.utils.Logger
import com.hms.explorelandmarks.utils.analytics.AnalyticsManager
import javax.inject.Inject

abstract class BaseBottomSheetFragment<VDB : ViewDataBinding> : BottomSheetDialogFragment(), IBaseFragment {

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    private var _mBinding: VDB? = null
    protected val mBinding: VDB
        get() = _mBinding!!

    private var _mBottomSheetBehavior: BottomSheetBehavior<*>? = null
    protected val mBottomSheetBehavior: BottomSheetBehavior<*>
        get() = _mBottomSheetBehavior!!

    private val mBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            onBottomSheetStateChanged(bottomSheet, newState)
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            onBottomSheetSlide(bottomSheet, slideOffset)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(container)
        initBottomSheet()
        initUI()
        setListeners()
        initObservers()

        super.onCreateView(inflater, container, savedInstanceState)
        analyticsManager.enterScreen(javaClass.simpleName)
        return _mBinding?.root
    }

    override fun initBinding(container: ViewGroup?) {
        _mBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), container, false)
        _mBinding?.lifecycleOwner = viewLifecycleOwner
    }

    private fun initBottomSheet() {
        _mBottomSheetBehavior = (dialog as BottomSheetDialog).behavior
        addBottomSheetCallBack()
    }

    protected open fun onBottomSheetStateChanged(bottomSheet: View, newState: Int) {
        Logger.e(TAG, "onBottomSheetStateChanged() not overriden")
    }

    protected open fun onBottomSheetSlide(bottomSheet: View, slideOffset: Float) {
        Logger.e(TAG, "onBottomSheetSlide() not overriden")
    }

    private fun addBottomSheetCallBack() {
        _mBottomSheetBehavior?.addBottomSheetCallback(mBottomSheetCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mBinding = null
        _mBottomSheetBehavior?.removeBottomSheetCallback(mBottomSheetCallback)
    }

    companion object {
        const val TAG = "BaseBottomSheetFragment"
    }
}
