package com.example.caobatruckscontrol.presentation.composables.sign_in

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val googleAuthClient: GoogleAuthClient
) : ViewModel() {

    private val TAG = "SignInViewModel"

    private val _state = MutableStateFlow(SignInState())
    val state: StateFlow<SignInState> = _state

    fun signInWithGoogle(account: GoogleSignInAccount?) {
        viewModelScope.launch {
            try {
                if (account != null) {
                    googleAuthClient.signInWithGoogle(account)
                    _state.value = SignInState(isSignInSuccessful = true)
                    Log.d(TAG, "signInWithGoogle: success")
                } else {
                    _state.value = SignInState(signInError = "Google sign-in account is null")
                    Log.e(TAG, "signInWithGoogle: Google sign-in account is null")
                }
            } catch (e: Exception) {
                _state.value = SignInState(signInError = e.message)
                Log.e(TAG, "signInWithGoogle: failed", e)
            }
        }
    }

    fun updateState(newState: SignInState) {
        _state.value = newState
    }

    fun getSignInIntent() = googleAuthClient.getSignInIntent()
}
