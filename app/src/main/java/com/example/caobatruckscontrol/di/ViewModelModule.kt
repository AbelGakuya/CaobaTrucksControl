// ViewModelModule.kt
package com.example.caobatruckscontrol.di

import com.example.caobatruckscontrol.presentation.composables.sign_in.GoogleAuthClient
import com.example.caobatruckscontrol.presentation.composables.sign_in.SignInViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ViewModelModule {

    @Provides
    fun provideSignInViewModel(
        googleAuthClient: GoogleAuthClient
    ): SignInViewModel {
        return SignInViewModel(googleAuthClient)
    }
}
