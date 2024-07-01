package com.example.caobatruckscontrol.presentation.composables.admin.viewmodel

import androidx.lifecycle.ViewModel
import com.example.caobatruckscontrol.common.Result
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.caobatruckscontrol.data.repository.DriverRepository
import com.example.caobatruckscontrol.data.model.Driver
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class DriverDetailViewModel @Inject constructor(
    private val repository: DriverRepository
) : ViewModel() {

    private val _driverDetail = MutableStateFlow<Driver?>(null)
    val driverDetail: StateFlow<Driver?> = _driverDetail

    private val _toggleStatusResult = MutableStateFlow<Result<Unit>?>(null)
    val toggleStatusResult: StateFlow<Result<Unit>?> = _toggleStatusResult

    private val _updateDriverResult = MutableStateFlow<Result<Unit>?>(null)
    val updateDriverResult: StateFlow<Result<Unit>?> = _updateDriverResult

    fun fetchDriverDetail(driverId: String) {
        viewModelScope.launch {
            val driver = repository.getDriverById(driverId)
            _driverDetail.value = driver
        }
    }

    fun toggleDriverStatus(driverId: String?, isActive: Boolean) {
        viewModelScope.launch {
            val result = repository.toggleDriverStatus(driverId, isActive)
            _toggleStatusResult.value = result
        }
    }

    fun updateDriver(driverId: String?, updatedDriver: Driver) {
        viewModelScope.launch {
            val result = repository.updateDriver(driverId, updatedDriver)
            _updateDriverResult.value = result
        }
    }
}
