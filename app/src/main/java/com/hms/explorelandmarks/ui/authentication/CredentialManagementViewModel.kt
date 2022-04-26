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

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.hms.explorelandmarks.R
import com.hms.explorelandmarks.di.ResourceProvider
import com.hms.explorelandmarks.ui.base.BaseViewModel
import com.hms.explorelandmarks.utils.BaseEventState
import com.hms.explorelandmarks.utils.Constants
import com.hms.explorelandmarks.utils.State
import com.huawei.hms.support.api.keyring.credential.*
import dagger.hilt.android.lifecycle.HiltViewModel
import java.nio.charset.Charset
import javax.inject.Inject

typealias CredentialsState = State<List<Credential>>
typealias SaveCredentialsState = State<Boolean>

@HiltViewModel
class CredentialManagementViewModel @Inject constructor(
    private val credentialClient: CredentialClient,
    private val resourceProvider: ResourceProvider
) : BaseViewModel() {

    private val _credentialList = MutableLiveData<CredentialsState>(State.Empty)
    val credentialList: LiveData<CredentialsState>
        get() = _credentialList

    private var isCurrentCredentialsUpToDate = true

    val userName = MutableLiveData("")
    val password = MutableLiveData("")

    init {
        findCredentials()
    }

    fun findCredentials() {
        if (isCredentialsAlreadyExistAndUpToDate()) {
            _credentialList.postValue(_credentialList.value)
            return
        }

        _credentialList.value = State.Loading

        credentialClient.findCredential(
            getTrustedAppsList(),
            object : CredentialCallback<List<Credential>> {
                override fun onSuccess(credentials: List<Credential>) {
                    _credentialList.value = State.Success(credentials)
                    isCurrentCredentialsUpToDate = true
                }

                override fun onFailure(errorCode: Long, description: CharSequence) {
                    _credentialList.value =
                        State.Failure(errorCode.toString(), description.toString())
                }
            }
        )
    }

    fun deleteCredential(credential: Credential): LiveData<State<Nothing>> {
        val result = MutableLiveData<State<Nothing>>(State.Loading)

        credentialClient.deleteCredential(
            credential,
            object : CredentialCallback<Void?> {
                override fun onSuccess(unused: Void?) {
                    isCurrentCredentialsUpToDate = false
                    findCredentials() // Because a credential deleted so we need get new credential from HMS CORE
                    result.value = State.Success()
                }

                override fun onFailure(errorCode: Long, description: CharSequence) {
                    result.value = State.Failure(errorCode.toString(), description.toString())
                }
            }
        )

        return result
    }

    private fun isCredentialsAlreadyExistAndUpToDate(): Boolean {
        return _credentialList.value != null &&
            _credentialList.value is State.Success &&
            isCurrentCredentialsUpToDate
    }

    fun getCredentialPassword(credential: Credential): LiveData<State<String>> {
        val result = MutableLiveData<State<String>>(State.Loading)

        credential.getContent(object : CredentialCallback<ByteArray?> {
            override fun onSuccess(content: ByteArray?) {
                content?.let {
                    val password = it.toString(Charset.defaultCharset())
                    result.value = State.Success(password)
                } ?: run {
                    result.value = State.Failure(
                        "",
                        resourceProvider.getString(
                            R.string.error_obtain_credential_content
                        )
                    )
                }
            }

            override fun onFailure(erroCode: Long, description: CharSequence?) {
                // Processing if the credentials fail to be obtained.
                result.value = State.Failure(erroCode.toString(), description.toString())
            }
        })
        return result
    }

    /**
     * This function responsible to save a new credential information
     *
     * @param isAuthorizationRequired Checks whether the user identity needs to be verified when the getContent method is called.
     *                 Set true if you want that, the user needs to complete verification through their biometric feature
     *                 or lock screen password before the credential content can be obtained.
     *
     *
     * [More detail about Credential Properties](https://developer.huawei.com/consumer/en/doc/development/Security-References/credential-0000001133185188)
     *
     * */

    fun saveCredential(
        isAuthorizationRequired: Boolean = true
    ): LiveData<SaveCredentialsState> {
        val name = userName.value?.trim() ?: ""
        val password = password.value?.trim() ?: ""
        val inputsValidationState = isInputsCorrect(name, password)
        if ((inputsValidationState is SignupValidationState.Success).not()) {
            return MutableLiveData(State.Failure("", "", inputsValidationState))
        }

        val saveCredentialState = MutableLiveData<SaveCredentialsState>(State.Loading)
        credentialClient.saveCredential(
            getCredentialInstanceByUserInputs(isAuthorizationRequired),
            object : CredentialCallback<Void?> {
                override fun onSuccess(unused: Void?) {
                    isCurrentCredentialsUpToDate = false // Current stored credentials
                    resetCredentialInputs()
                    saveCredentialState.value = State.Success(true)
                }

                override fun onFailure(errorCode: Long, description: CharSequence) {
                    saveCredentialState.value =
                        State.Failure(errorCode.toString(), description.toString())
                }
            }
        )

        return saveCredentialState
    }

    private fun resetCredentialInputs() {
        userName.value = ""
        password.value = ""
    }

    private fun getCredentialInstanceByUserInputs(isAuthorizationRequired: Boolean): Credential {
        val name = userName.value ?: ""
        val password = password.value ?: ""

        return Credential(
            name, CredentialType.PASSWORD,
            isAuthorizationRequired, password.toByteArray()
        ).apply {
            displayName = name
            sharedWith = getTrustedAppsList()
            syncable = true // syncs credential with cloud
        }
    }

    private fun isInputsCorrect(userName: String, password: String): SignupValidationState {
        if (userName.isBlank()) {
            return SignupValidationState.UserNameError
        }

        if (password.isBlank()) {
            return SignupValidationState.PasswordError
        }

        return SignupValidationState.Success
    }

    sealed class SignupValidationState : BaseEventState() {
        object Success : SignupValidationState()
        object UserNameError : SignupValidationState()
        object PasswordError : SignupValidationState()
    }

    /**
     * When you will not share credentials to another app or platform you can return empty list.
     *
     * When you will share your credentials with other platforms, please provide needed information of them.
     *      Don't forget to update Constants variables which used in below.
     * */
    private fun getTrustedAppsList(): List<AppIdentity> {
        return emptyList()

        // A list example of including information of platforms that sharing credentials with them
        return listOf<AppIdentity>(
            AndroidAppIdentity(
                Constants.KEYRING_SOURCE_APP_NAME,
                Constants.KEYRING_SOURCE_APP_PACKAGE_NAME,
                Constants.KEYRING_SOURCE_APP_SIGN_CERTIFICATE_SHA256
            )
        )
    }
}
