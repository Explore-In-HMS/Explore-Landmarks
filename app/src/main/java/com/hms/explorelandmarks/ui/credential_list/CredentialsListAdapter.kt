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

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncDifferConfig
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.hms.explorelandmarks.databinding.ItemCredentialBinding
import com.hms.explorelandmarks.utils.CommonAdapterClickListener
import com.hms.explorelandmarks.utils.extensions.invisible
import com.hms.explorelandmarks.utils.extensions.show
import com.huawei.hms.support.api.keyring.credential.Credential
import dagger.hilt.android.scopes.FragmentScoped
import javax.inject.Inject

@FragmentScoped
class CredentialsListAdapter @Inject constructor(val clickListener: CommonAdapterClickListener) :
    ListAdapter<Credential, CredentialsListAdapter.CredentialViewHolder>(
        AsyncDifferConfig.Builder(CredentialDiffCallBack()).build()
    ) {
    class CredentialViewHolder(private val mBinding: ItemCredentialBinding) :
        RecyclerView.ViewHolder(mBinding.root) {
        fun bind(credentialItem: Credential, isLastItem: Boolean) {
            mBinding.credential = credentialItem
            if (isLastItem) {
                mBinding.separator.invisible()
            } else {
                mBinding.separator.show()
            }
        }

        fun getForegroundLayout() = mBinding.foregroundLayout
    }

    override fun onBindViewHolder(credentialViewHolder: CredentialViewHolder, position: Int) {
        val item = getItem(position)
        val isItemLastItem = position == (currentList.size - 1)
        credentialViewHolder.bind(item, isLastItem = isItemLastItem)
        credentialViewHolder.itemView.setOnClickListener { clickListener.onClick(position) }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CredentialViewHolder {
        val mBinding = ItemCredentialBinding.inflate(LayoutInflater.from(parent.context))
        return CredentialViewHolder(mBinding)
    }

    private class CredentialDiffCallBack : DiffUtil.ItemCallback<Credential>() {
        override fun areItemsTheSame(oldItem: Credential, newItem: Credential) =
            oldItem.timeCreated == newItem.timeCreated

        override fun areContentsTheSame(oldItem: Credential, newItem: Credential) =
            oldItem.username == newItem.username
    }
}
