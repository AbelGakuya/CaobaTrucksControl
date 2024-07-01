package com.example.caobatruckscontrol.data.repository

import com.example.caobatruckscontrol.data.model.Box
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await
import com.example.caobatruckscontrol.common.Result

class BoxRepository {

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }
    private val boxesCollection = db.collection("boxes")
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getAllBoxes(): Result<List<Box>> {
        return try {
            val querySnapshot = boxesCollection.get().await()
            val boxes = querySnapshot.documents.mapNotNull { document ->
                document.toObject<Box>()?.apply { id = document.id }
            }
            Result.Success(boxes)
        } catch (e: Exception) {
            Result.Error("Error fetching boxes: ${e.message}")
        }
    }

    suspend fun getBoxById(boxId: String): Result<Box?> {
        return try {
            val documentSnapshot = boxesCollection.document(boxId).get().await()
            val box = documentSnapshot.toObject<Box>()
            Result.Success(box)
        } catch (e: Exception) {
            Result.Error("Error fetching box with id $boxId: ${e.message}")
        }
    }

    suspend fun addBox(box: Box): Result<String> {
        return try {
            val documentReference = boxesCollection.add(box).await()
            Result.Success(documentReference.id)
        } catch (e: Exception) {
            Result.Error("Error adding box: ${e.message}")
        }
    }

    suspend fun deleteBox(boxId: String): Result<Unit> {
        return try {
            boxesCollection.document(boxId).delete().await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error("Error deleting box with id $boxId: ${e.message}")
        }
    }


    suspend fun fetchBoxById(boxId: String): Result<Box> {
        return try {
            val snapshot = boxesCollection.document(boxId).get().await()
            val box = snapshot.toObject(Box::class.java)
            if (box != null) {
                Result.Success(box)
            } else {
                Result.Error(Exception("Box not found").toString())
            }
        } catch (e: Exception) {
            Result.Error(e.toString())
        }
    }

    suspend fun updateBox(boxId: String, updatedBox: Box): Result<Unit> {
        return try {
            boxesCollection.document(boxId).set(updatedBox).await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toString())
        }
    }

    suspend fun toggleBoxStatus(boxId: String, isActive: Boolean): Result<Unit> {
        return try {
            boxesCollection.document(boxId)
                .update("isActive", isActive)
                .await()
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(e.toString())
        }
    }
}
