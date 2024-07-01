package com.example.caobatruckscontrol.presentation.composables.driver.viewmodel

import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caobatruckscontrol.data.model.Expensee
import com.example.caobatruckscontrol.data.repository.ExpenseRepository
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class DriverExpenseViewModel @Inject constructor(
    private val expenseRepository: ExpenseRepository
) : ViewModel() {



    fun saveExpense(
        tripId: String,
        isTruckExpense: Boolean,
        category: String,
        expenseType: String,
        invoiceFolio: String?,
        issuingCompany: String,
        amount: Double,
        odometer: Double?,
        description: String?,
        photoUri: Uri? // Accept Uri instead of String for photoUri
    ) {
        val db = FirebaseFirestore.getInstance()

        // Reference to the expenses subcollection under the specific tripId
        val expensesRef = db.collection("trips").document(tripId).collection("expenses")

        // Create a new expense document with auto-generated ID
        val expenseData = hashMapOf(
            "isTruckExpense" to isTruckExpense,
            "category" to category,
            "expenseType" to expenseType,
            "invoiceFolio" to invoiceFolio,
            "issuingCompany" to issuingCompany,
            "amount" to amount,
            "odometer" to odometer,
            "description" to description,
            "timestamp" to Timestamp.now() // Current timestamp when the expense is added
        )

        // Upload photoUri to Firebase Storage if it exists
        if (photoUri != null) {
            uploadPhotoToStorage(tripId, photoUri) { downloadUrl ->
                expenseData["photoUri"] = downloadUrl

                // Add the expense document to Firestore after photo upload
                expensesRef.add(expenseData)
                    .addOnSuccessListener { documentReference ->
                        // Handle success
                        println("Expense added with ID: ${documentReference.id}")
                    }
                    .addOnFailureListener { e ->
                        // Handle failure
                        println("Error adding expense: $e")
                    }
            }
        } else {
            // No photo to upload, directly add the expense document to Firestore
            expenseData["photoUri"] = null
            expensesRef.add(expenseData)
                .addOnSuccessListener { documentReference ->
                    // Handle success
                    println("Expense added with ID: ${documentReference.id}")
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    println("Error adding expense: $e")
                }
        }
    }

    private fun uploadPhotoToStorage(tripId: String, photoUri: Uri, callback: (String) -> Unit) {
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        val photoRef = storageRef.child("expenses/${tripId}_${UUID.randomUUID()}")

        val uploadTask = photoRef.putFile(photoUri)

        uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            photoRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                callback(downloadUri.toString())
            } else {
                // Handle unsuccessful upload
                println("Failed to upload photo: ${task.exception}")
            }
        }
    }


}
