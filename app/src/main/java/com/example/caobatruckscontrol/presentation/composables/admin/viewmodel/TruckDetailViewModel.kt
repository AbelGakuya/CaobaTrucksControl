package com.example.caobatruckscontrol.presentation.composables.admin.viewmodel


import androidx.lifecycle.ViewModel
import com.example.caobatruckscontrol.common.Result
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.caobatruckscontrol.data.repository.DriverRepository
import com.example.caobatruckscontrol.data.model.Driver
import com.example.caobatruckscontrol.data.model.Truck
import com.example.caobatruckscontrol.data.repository.TruckRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TruckDetailViewModel @Inject constructor(
    private val repository: TruckRepository
) : ViewModel() {

    private val _truckDetail = MutableStateFlow<Truck?>(null)
    val truckDetail: StateFlow<Truck?> = _truckDetail

    private val _toggleStatusResult = MutableStateFlow<Result<Unit>?>(null)
    val toggleStatusResult: StateFlow<Result<Unit>?> = _toggleStatusResult

    private val _updateDriverResult = MutableStateFlow<Result<Unit>?>(null)
    val updateDriverResult: StateFlow<Result<Unit>?> = _updateDriverResult

    fun fetchTruckDetail(driverId: String) {
        viewModelScope.launch {
            val driver = repository.getTruckById(driverId)
            _truckDetail.value = driver
        }
    }

    fun toggleTruckStatus(driverId: String?, isActive: Boolean) {
        viewModelScope.launch {
            val result = repository.toggleTruckStatus(driverId, isActive)
            _toggleStatusResult.value = result
        }
    }

    fun updateTruck(driverId: String?, updatedTruck: Truck) {
        viewModelScope.launch {
            val result = repository.updateTruck(driverId, updatedTruck)
            _updateDriverResult.value = result
        }
    }
}
