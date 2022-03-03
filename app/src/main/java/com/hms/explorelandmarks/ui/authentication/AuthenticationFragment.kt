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

package com.hms.explorelandmarks.ui.authentication

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.databinding.FragmentAuthenticationBinding
import com.hms.explorelandmarks.ui.base.BaseFragmentWithViewModel
import com.hms.explorelandmarks.utils.extensions.hideKeyboard
import com.hms.explorelandmarks.utils.extensions.toast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationFragment :
    BaseFragmentWithViewModel<CredentialManagementViewModel, FragmentAuthenticationBinding>(
        CredentialManagementViewModel::class
    ) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding.view = mViewModel
        getCredentials()
    }

    private fun getCredentials() {
        observeStateLiveDataAndDoOnSuccess(mViewModel.credentialList) { credentialList ->
            if (credentialList.isNullOrEmpty()) {
                toast(R.string.error_no_credenntials_signup)
                return@observeStateLiveDataAndDoOnSuccess
            }

            val currentFragment = findNavController().currentDestination?.id
            if (currentFragment != R.id.credentialListFragment) {
                findNavController().navigate(R.id.action_authentication_to_credentialList)
            }
        }
    }

    private fun saveCredential() {
        hideKeyboard()
        observeStateLiveDataAndDoOnSuccess(
            mViewModel.saveCredential(),
            onFailureCallback = { errorCode, errorMessage, eventState ->
                if (eventState is CredentialManagementViewModel.SignupValidationState) {
                    savingCredentialFailed(eventState)
                }
            }
        ) {
            activity?.currentFocus?.clearFocus()
            resetInputErrors()
            mViewModel.findCredentials()
        }
    }

    private fun savingCredentialFailed(signupValidationState: CredentialManagementViewModel.SignupValidationState) {
        when (signupValidationState) {
            CredentialManagementViewModel.SignupValidationState.UserNameError -> {
                mBinding.containerSignup.tilName.error = resources.getString(R.string.warning_input)
                mBinding.containerSignup.etName.requestFocus()
            }

            CredentialManagementViewModel.SignupValidationState.PasswordError -> {
                mBinding.containerSignup.containerPassword.tilPassword.error =
                    resources.getString(R.string.warning_input)
                mBinding.containerSignup.containerPassword.etPassword.requestFocus()
            }

            CredentialManagementViewModel.SignupValidationState.Success -> {
                resetInputErrors()
            }
        }
    }

    private fun resetInputErrors() {
        mBinding.containerSignup.tilName.error = null
        mBinding.containerSignup.containerPassword.tilPassword.error = null
    }

    override fun setListeners() {
        mBinding.containerSignup.btnSignup.setOnClickListener {
            saveCredential()
        }
    }

    override fun willViewModelBeShared() = true

    override fun getLayoutId() = R.layout.fragment_authentication
}
