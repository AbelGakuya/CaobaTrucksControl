package com.example.caobatruckscontrol.presentation.composables.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caobatruckscontrol.data.model.TripReport
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class ReportViewModel
@Inject constructor(

    private val firestore: FirebaseFirestore

): ViewModel() {


    private val reportsState = MutableStateFlow<List<TripReport>>(emptyList())
    private val errorState = MutableStateFlow<String?>(null)

    val reports: StateFlow<List<TripReport>> = reportsState
    val error: StateFlow<String?> = errorState


    // Function to fetch reports for the current admin user
    fun fetchReports(adminId: String) {
        viewModelScope.launch {
            try {
                val snapshot = firestore
                    .collection("reports")
                    .whereEqualTo("adminId", adminId)
                    .get()
                    .await()

                val reports = snapshot.documents.mapNotNull { document ->
                    document.toObject(TripReport::class.java)
                }
                reportsState.value = reports
            } catch (e: Exception) {
                errorState.value = "Failed to fetch reports: ${e.message}"
            }
        }
    }
}
