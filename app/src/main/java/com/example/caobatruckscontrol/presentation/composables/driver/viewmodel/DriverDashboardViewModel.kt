package com.example.caobatruckscontrol.presentation.composables.driver.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caobatruckscontrol.data.model.Trip
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject


@HiltViewModel
class DriverDashboardViewModel @Inject constructor(

    private val firestore: FirebaseFirestore

) : ViewModel() {
    private val _trips = MutableLiveData<List<Trip>>()
    val trips: LiveData<List<Trip>> = _trips

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    //private val firestore = FirebaseFirestore.getInstance()

    companion object {
        private const val TAG = "DriverViewModel"
    }

    fun fetchTrips(driverId: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val snapshot = firestore
                    .collection("trips")
                    .whereEqualTo("driverId", driverId)
                    .get()
                    .await()
                val trips = snapshot.documents.map { document ->
                    document.toObject(Trip::class.java)!!.copy(id = document.id)
                }
                _trips.value = trips
            } catch (e: Exception) {
                e.printStackTrace() // Handle exceptions
            } finally {
                _loading.value = false
            }
        }
    }


    fun acceptTrip(
        driverId: String,
        tripId: String
    ) {
        viewModelScope.launch {
            try {
                val acceptedAt = System.currentTimeMillis()
                firestore.collection("trips")
                    .document(tripId)
                    .update("accepted", true, "acceptedAt", acceptedAt).await()
                fetchTrips(driverId) // Refresh trips after accepting
            } catch (e: Exception) {
                e.printStackTrace() // Handle exceptions
            }
        }
    }


}



