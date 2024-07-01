package com.example.caobatruckscontrol.di

//import GoogleAuthClient
//import BoxRepository
import com.example.caobatruckscontrol.presentation.composables.sign_in.GoogleAuthClient
import android.app.Application
import android.content.Context
import com.example.caobatruckscontrol.data.repository.BoxRepository
import com.example.caobatruckscontrol.data.repository.DriverRepository
import com.example.caobatruckscontrol.data.repository.ExpenseRepository
import com.example.caobatruckscontrol.data.repository.TripRepository
import com.example.caobatruckscontrol.data.repository.TruckRepository
import com.example.caobatruckscontrol.presentation.composables.admin.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application.applicationContext
    }

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideGoogleAuthClient(
        context: Context,
        auth: FirebaseAuth
    ): GoogleAuthClient {
        return GoogleAuthClient(context, auth)
    }

    @Provides
    @Singleton
    fun provideTripRepository(): TripRepository {
        return TripRepository()
    }

    @Provides
    @Singleton
    fun provideDriverRepository(): DriverRepository {
        return DriverRepository()
    }

    @Provides
    @Singleton
    fun provideTruckRepository(): TruckRepository {
        return TruckRepository()
    }

    @Provides
    @Singleton
    fun provideBoxRepository(): BoxRepository {
        return BoxRepository()
    }

    @Provides
    @Singleton
    fun provideExpenseRepository(): ExpenseRepository {
        return ExpenseRepository()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }


    @Provides
    @Singleton
    fun provideAuthRepository(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ): AuthRepository {
        return AuthRepository(firestore, auth)
    }
}
