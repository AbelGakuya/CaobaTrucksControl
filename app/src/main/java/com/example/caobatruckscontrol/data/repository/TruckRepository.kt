package com.example.caobatruckscontrol.data.repository

import com.example.caobatruckscontrol.common.Result
import com.example.caobatruckscontrol.data.model.Driver
import com.example.caobatruckscontrol.data.model.Truck
import com.example.caobatruckscontrol.data.model.Expense
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class TruckRepository {
    private val db = FirebaseFirestore.getInstance()
    private val trucksCollection = db.collection("trucks")

    suspend fun getTrucks(): List<Truck> {
        return try {
            val result = trucksCollection.get().await()
            result.documents.map { it.toObject(Truck::class.java)!!.copy(id = it.id) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getTruckById(truckId: String): Truck? {
        return try {
            val document = trucksCollection.document(truckId).get().await()
            document.toObject(Truck::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addTruck(truck: Truck) {
        try {
            trucksCollection.add(truck).await()
        } catch (e: Exception) {
            // Handle the error
        }
    }


    suspend fun updateTruck(truckId: String?, updatedTruck: Truck): Result<Unit> {
        return try {
            trucksCollection.document(truckId!!).set(updatedTruck).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun deleteTruck(truckId: String) {
        try {
            trucksCollection.document(truckId).delete().await()
        } catch (e: Exception) {
            // Handle the error
        }
    }

    suspend fun updateTruckActiveStatus(truckId: String, isActive: Boolean) {
        try {
            trucksCollection.document(truckId).update("isActive", isActive).await()
        } catch (e: Exception) {
            // Handle the error
        }
    }

    suspend fun toggleTruckStatus(truckId: String?, isActive: Boolean): Result<Unit> {
        return try {
            val updateData = mapOf(
                "active" to isActive
            )
            trucksCollection.document(truckId!!).update(updateData).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun addExpense(truckId: String, expense: Expense) {
        try {
            val truckDoc = trucksCollection.document(truckId).get().await()
            val truck = truckDoc.toObject(Truck::class.java)
            if (truck != null) {
                val updatedExpenses = truck.expenses + expense
                trucksCollection.document(truckId).update("expenses", updatedExpenses).await()
            }
        } catch (e: Exception) {
            // Handle the error
        }
    }
}
