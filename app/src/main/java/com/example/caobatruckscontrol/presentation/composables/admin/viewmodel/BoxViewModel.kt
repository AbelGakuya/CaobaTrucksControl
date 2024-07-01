package com.example.caobatruckscontrol.presentation.composables.admin.viewmodel

//import BoxRepository
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caobatruckscontrol.data.model.Box
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.caobatruckscontrol.common.Result
import com.example.caobatruckscontrol.data.repository.BoxRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

@HiltViewModel
class BoxViewModel @Inject constructor(
    private val repository: BoxRepository
) : ViewModel() {

    private val _boxes = MutableStateFlow<List<Box>>(emptyList())
    val boxes: StateFlow<List<Box>> = _boxes

    init {
        fetchBoxes()
    }

    private fun fetchBoxes() {
        viewModelScope.launch {
            when (val result = repository.getAllBoxes()) {
                is Result.Success -> _boxes.value = result.data
                is Result.Error -> handleFetchError(result.message)
                else -> {
                    //Toast.makeText(context, "Unexpected result: $result", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val storage = FirebaseStorage.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun uploadImage(uri: Uri, path: String): String? {
        return try {
            val ref = storage.reference.child(path)
            ref.putFile(uri).await()
            ref.downloadUrl.await().toString()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addBox(box: Box): Result<Unit> {
        return try {
            firestore.collection("boxes").add(box.toMap()).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message!!)
        }
    }

    fun updateBox(boxId: String, updatedBox: Box, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.updateBox(boxId, updatedBox)) {
                is Result.Success -> onSuccess()
                is Result.Error -> onError(result.message)
                else -> {
                    //Toast.makeText(context, "Unexpected result: $result", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun deleteBox(boxId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            when (val result = repository.deleteBox(boxId)) {
                is Result.Success -> onSuccess()
                is Result.Error -> onError(result.message)
                else -> {
                    //Toast.makeText(context, "Unexpected result: $result", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleFetchError(message: String) {

        println("Fetch boxes error: $message")
    }
}
