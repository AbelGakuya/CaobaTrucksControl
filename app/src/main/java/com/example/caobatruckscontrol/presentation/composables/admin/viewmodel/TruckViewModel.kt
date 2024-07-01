package com.example.caobatruckscontrol.presentation.composables.admin.viewmodel


import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caobatruckscontrol.common.Result
import com.example.caobatruckscontrol.data.model.Truck
import com.example.caobatruckscontrol.data.model.Expense
import com.example.caobatruckscontrol.data.repository.TruckRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class TruckViewModel @Inject constructor(
    private val repository: TruckRepository
) : ViewModel() {

    private val _trucks = MutableStateFlow<List<Truck>>(emptyList())
    val trucks: StateFlow<List<Truck>> = _trucks

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        fetchTrucks()
    }

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadImage(uri: Uri, path: String): String? {
        return try {
            val ref = storage.reference.child(path)
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addTruck(truck: Truck): Result<Unit> {
        return try {
            firestore.collection("trucks").add(truck).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message!!)
        }
    }

    private fun fetchTrucks() {
        viewModelScope.launch {
            try {
                val result = repository.getTrucks()
                _trucks.value = result
            } catch (e: Exception) {
                _error.value = "Failed to fetch trucks: ${e.message}"
            }
        }
    }

    fun addTruck(truck: Truck,callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            try {
                repository.addTruck(truck)
                fetchTrucks() // Refresh truck list
            } catch (e: Exception) {
                _error.value = "Failed to add truck: ${e.message}"
            }
        }
    }

//    fun updateTruck(truck: Truck) {
//        viewModelScope.launch {
//            try {
//                repository.updateTruck(truck)
//                fetchTrucks() // Refresh truck list
//            } catch (e: Exception) {
//                _error.value = "Failed to update truck: ${e.message}"
//            }
//        }
//    }

    fun deleteTruck(truckId: String) {
        viewModelScope.launch {
            try {
                repository.deleteTruck(truckId)
                fetchTrucks() // Refresh truck list
            } catch (e: Exception) {
                _error.value = "Failed to delete truck: ${e.message}"
            }
        }
    }

    fun pauseTruck(truckId: String) {
        viewModelScope.launch {
            try {
                repository.updateTruckActiveStatus(truckId, false)
                fetchTrucks() // Refresh truck list
            } catch (e: Exception) {
                _error.value = "Failed to pause truck: ${e.message}"
            }
        }
    }

    fun enableTruck(truckId: String) {
        viewModelScope.launch {
            try {
                repository.updateTruckActiveStatus(truckId, true)
                fetchTrucks() // Refresh truck list
            } catch (e: Exception) {
                _error.value = "Failed to enable truck: ${e.message}"
            }
        }
    }

    fun addExpense(truckId: String, expense: Expense) {
        viewModelScope.launch {
            try {
                repository.addExpense(truckId, expense)
                fetchTrucks() // Refresh truck list
            } catch (e: Exception) {
                _error.value = "Failed to add expense: ${e.message}"
            }
        }
    }
}
