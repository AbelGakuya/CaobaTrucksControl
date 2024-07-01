package com.example.caobatruckscontrol.data.repository

import android.net.Uri
import com.example.caobatruckscontrol.common.Result
import com.example.caobatruckscontrol.data.model.Driver
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.tasks.await

class DriverRepository {

    private val db = FirebaseFirestore.getInstance()
    private val driversCollection = db.collection("drivers details")
    private val storage = FirebaseStorage.getInstance()

    suspend fun uploadImageToFirebase(uri: Uri, path: String): String? {
        return try {
            val storageRef = storage.reference.child("$path/${uri.lastPathSegment}")
            storageRef.putFile(uri).await()
            val downloadUrl = storageRef.downloadUrl.await().toString()
            downloadUrl
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun getDrivers(): List<Driver> {
        return try {
            val snapshot = driversCollection.get().await()
            snapshot.documents.mapNotNull { document ->
                document.toObject(Driver::class.java)?.copy(id = document.id)
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDriverById(driverId: String): Driver? {
        return try {
            val document = driversCollection.document(driverId).get().await()
            document.toObject(Driver::class.java)?.copy(id = document.id)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun addDriver(driver: Driver): Result<Unit> {
        return try {
            driversCollection.add(driver).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun updateDriver(driverId: String?, updatedDriver: Driver): Result<Unit> {
        return try {
            driversCollection.document(driverId!!).set(updatedDriver).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun deleteDriver(driverId: String): Result<Unit> {
        return try {
            driversCollection.document(driverId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun toggleDriverStatus(driverId: String?, isActive: Boolean): Result<Unit> {
        return try {
            val updateData = mapOf(
                "active" to isActive
            )
            driversCollection.document(driverId!!).update(updateData).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Unknown error")
        }
    }
}
