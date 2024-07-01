package com.example.caobatruckscontrol.presentation.composables.admin.viewmodel

import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caobatruckscontrol.data.model.Driver
import com.example.caobatruckscontrol.data.repository.DriverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import com.example.caobatruckscontrol.common.Result
import com.example.caobatruckscontrol.presentation.composables.admin.LoadingState
import com.example.caobatruckscontrol.presentation.composables.sign_in.SignInState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State


@HiltViewModel
class DriverViewModel @Inject constructor(
    private val repository: DriverRepository
) : ViewModel() {

    private val _drivers = MutableStateFlow<List<Driver>>(emptyList())
    val drivers: StateFlow<List<Driver>> = _drivers

    private val _loadingState = mutableStateOf(LoadingState())
    val loadingState: State<LoadingState> get() = _loadingState

    init {
        fetchDrivers()
    }

    fun uploadImageToFirebase(uri: Uri, path: String, onResult: (String?) -> Unit)  {
        viewModelScope.launch {
            val downloadUrl = repository.uploadImageToFirebase(uri, path)
            onResult(downloadUrl)
        }
    }

    fun updateLoadingState(isLoading: Boolean) {
        _loadingState.value = _loadingState.value.copy(isLoading = isLoading)
    }


    fun fetchDrivers() {
        viewModelScope.launch {
            val result = repository.getDrivers()
            _drivers.value = result
        }
    }

    private fun handleFetchError(message: String) {
        // Handle fetch drivers error (e.g., show error message, log error)
        println("Fetch drivers error: $message")
    }


    fun addDriver(driver: Driver,callback: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val result = repository.addDriver(driver)
                withContext(Dispatchers.Main) {
                    callback(result)
                }
            }
        }
    }

    fun updateDriver(driverId: String, updatedDriver: Driver) {
        viewModelScope.launch {
            when (val result = repository.updateDriver(driverId, updatedDriver)) {
                is Result.Success -> fetchDrivers() // Refresh driver list on success
                is Result.Error -> handleOperationError("Update driver", result.message)
                else -> {}
            }
        }
    }

    fun deleteDriver(driverId: String) {
        viewModelScope.launch {
            when (val result = repository.deleteDriver(driverId)) {
                is Result.Success -> fetchDrivers() // Refresh driver list on success
                is Result.Error -> handleOperationError("Delete driver", result.message)
                else -> {}
            }
        }
    }

    private fun handleOperationError(operation: String, message: String) {
        // Handle operation specific error (e.g., show error message, log error)
        println("$operation error: $message")
    }
}
