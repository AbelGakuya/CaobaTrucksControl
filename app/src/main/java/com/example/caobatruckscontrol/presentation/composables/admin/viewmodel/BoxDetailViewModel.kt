package com.example.caobatruckscontrol.presentation.composables.admin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caobatruckscontrol.data.model.Box
import com.example.caobatruckscontrol.data.repository.BoxRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.example.caobatruckscontrol.common.Result
import javax.inject.Inject

@HiltViewModel
class BoxDetailViewModel @Inject constructor(
    private val boxRepository: BoxRepository
) : ViewModel() {

    private val _boxDetail = MutableStateFlow<Box?>(null)
    val boxDetail: StateFlow<Box?> = _boxDetail.asStateFlow()

    private val _toggleStatusResult = MutableStateFlow<Result<Unit>?>(null)
    val toggleStatusResult: StateFlow<Result<Unit>?> = _toggleStatusResult.asStateFlow()

    private val _updateBoxResult = MutableStateFlow<Result<Unit>?>(null)
    val updateBoxResult: StateFlow<Result<Unit>?> = _updateBoxResult.asStateFlow()

    fun fetchBoxDetail(boxId: String) {
        viewModelScope.launch {
            val result = boxRepository.fetchBoxById(boxId)
            if (result is Result.Success) {
                _boxDetail.value = result.data
            }
        }
    }

    fun updateBox(boxId: String, updatedBox: Box) {
        viewModelScope.launch {
            //_updateBoxResult.value = Result.Loading
            val result = boxRepository.updateBox(boxId, updatedBox)
            _updateBoxResult.value = result
        }
    }

    fun toggleBoxStatus(boxId: String, isActive: Boolean) {
        viewModelScope.launch {
           // _toggleStatusResult.value = Result.Loading
            val result = boxRepository.toggleBoxStatus(boxId, isActive)
            _toggleStatusResult.value = result
        }
    }
}