package com.example.caobatruckscontrol.presentation.composables.sign_in

data class SignInState(
    val isLoading: Boolean = false,  // Add isLoading property
    val isSignInSuccessful: Boolean = false,
    val signInError: String? = null
)
