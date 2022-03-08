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

package com.hms.explorelandmarks.ui.main

import android.os.Bundle
import com.hms.explorelandmarks.databinding.ActivityMainBinding
import com.hms.explorelandmarks.ui.base.BaseActivity
import com.hms.explorelandmarks.utils.extensions.gone
import com.hms.explorelandmarks.utils.extensions.show
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    private var mBinding: ActivityMainBinding? = null

    override fun showLoading() {
        mBinding?.layoutLoading?.root?.show()
    }

    override fun hideLoading() {
        mBinding?.layoutLoading?.root?.gone()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding?.root)
    }
}
