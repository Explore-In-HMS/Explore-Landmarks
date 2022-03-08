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
import androidx.fragment.app.Fragment
import com.hms.explorelandmarks.utils.analytics.AnalyticsManager
import javax.inject.Inject

abstract class BaseFragment<VDB : ViewDataBinding> : Fragment(), IBaseFragment {

    private var _mBinding: VDB? = null
    protected val mBinding: VDB
        get() = _mBinding!!

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        initBinding(container)
        super.onCreateView(inflater, container, savedInstanceState)
        return _mBinding?.root
    }

    override fun initBinding(container: ViewGroup?) {
        _mBinding = DataBindingUtil.inflate(layoutInflater, getLayoutId(), container, false)
        _mBinding?.lifecycleOwner = viewLifecycleOwner
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUI()
        setListeners()
        initObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _mBinding = null
    }
}
