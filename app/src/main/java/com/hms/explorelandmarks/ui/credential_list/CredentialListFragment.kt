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

package com.hms.explorelandmarks.ui.credential_list

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.databinding.FragmentCredentialListBinding
import com.hms.explorelandmarks.ui.authentication.CredentialManagementViewModel
import com.hms.explorelandmarks.ui.base.BaseBottomSheetFragmentWithViewModel
import com.hms.explorelandmarks.utils.Constants
import com.hms.explorelandmarks.utils.UserManager
import com.hms.explorelandmarks.utils.extensions.showGeneralErrorToast
import com.hms.explorelandmarks.utils.extensions.toast
import com.huawei.hms.support.api.keyring.credential.Credential
import dagger.hilt.android.AndroidEntryPoint
import getSwipeTouchHelper
import javax.inject.Inject
import javax.inject.Named

@AndroidEntryPoint
class CredentialListFragment :
    BaseBottomSheetFragmentWithViewModel<CredentialManagementViewModel, FragmentCredentialListBinding>(
        CredentialManagementViewModel::class
    ) {
    @Inject
    lateinit var credentialListAdapter: CredentialsListAdapter

    @Inject
    @Named(Constants.VERTICAL_LINEAR_LAYOUT_MANAGER)
    lateinit var linearLayoutManager: LinearLayoutManager

    private val currentCredentialList: List<Credential>
        get() = credentialListAdapter.currentList

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeCredentialList()
    }

    private fun observeCredentialList() {
        observeStateLiveDataAndDoOnSuccess(mViewModel.credentialList, onFailureCallback = { _, _, _ ->
            showErrorAndQuit()
        }) { credentialList: List<Credential>? ->
            if (credentialList.isNullOrEmpty()) {
                showErrorAndQuit()
                return@observeStateLiveDataAndDoOnSuccess
            }
            submitCredentialList(credentialList)
        }
    }

    private fun deleteCredential(credentialPosition: Int) {
        val credentialForDelete = currentCredentialList[credentialPosition]
        val deleteCredentialLiveData = mViewModel.deleteCredential(credentialForDelete)
        observeStateLiveDataAndDoOnSuccess(deleteCredentialLiveData) {
            toast(R.string.success_credential_deleted)
        }
    }

    private fun submitCredentialList(credentialList: List<Credential>) {
        credentialListAdapter.submitList(credentialList)
    }

    private fun getCredentialPassword(credential: Credential) {
        observeStateLiveDataAndDoOnSuccess(
            mViewModel.getCredentialPassword(credential)
        ) { password ->
            password?.let {
                analyticsManager.userLogin(credential)
                saveUserAndNavigateNextScreen(credential, it)
            } ?: run {
                showGeneralErrorToast()
            }
        }
    }

    private fun saveUserAndNavigateNextScreen(credential: Credential, password: String) {
        UserManager.setUserData(credential, password)
        toast("Password ==> $password")
        dismiss()
        findNavController().navigate(R.id.action_credentialListFragment_to_landmarkRecognitionFragment)
    }

    override fun initUI() {
        mBinding.rvCredentialList.apply {
            layoutManager = linearLayoutManager
            setHasFixedSize(true)
            adapter = credentialListAdapter
        }

        getItemTouchHelper().attachToRecyclerView(mBinding.rvCredentialList)
    }

    override fun setListeners() {
        mBinding.containerClose.root.setOnClickListener { dismiss() }

        credentialListAdapter.clickListener.onItemClick = { position ->
            getCredentialPassword(currentCredentialList[position])
        }
    }

    private fun showErrorAndQuit() {
        showGeneralErrorToast()
        dismiss()
    }

    private fun getItemTouchHelper(): ItemTouchHelper {
        return getSwipeTouchHelper(
            foregroundLayoutProvider = { viewHolder: RecyclerView.ViewHolder ->
                (viewHolder as CredentialsListAdapter.CredentialViewHolder).getForegroundLayout()
            },
            onSwiped = { credentialPosition ->
                deleteCredential(credentialPosition)
            }
        )
    }

    override fun getLayoutId() = R.layout.fragment_credential_list

    override fun willViewModelBeShared() = true
}
