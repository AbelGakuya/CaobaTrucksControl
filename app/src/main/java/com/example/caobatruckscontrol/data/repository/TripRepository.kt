package com.example.caobatruckscontrol.data.repository


import android.util.Log
import com.example.caobatruckscontrol.data.model.Trip

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.example.caobatruckscontrol.common.Result
import com.example.caobatruckscontrol.data.model.Deposit
import com.example.caobatruckscontrol.data.model.Expense
import com.example.caobatruckscontrol.data.model.Expensee
import com.example.caobatruckscontrol.data.model.RegisteredDriver
import com.google.android.gms.nearby.messages.Message
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

import org.json.JSONObject
import java.net.HttpURLConnection

class TripRepository {

    private val db = FirebaseFirestore.getInstance()
    private val tripsCollection = db.collection("trips")
    private val driversCollection = db.collection("drivers")

    suspend fun fetchDrivers(): List<RegisteredDriver> {
        return try {
            // Fetch all documents from the 'drivers' collection
            val querySnapshot = driversCollection.get().await()

            // Extract driver IDs and names from the query snapshot
            querySnapshot.documents.map { document ->
                RegisteredDriver(
                    id = document.id,
                    name = document.getString("name") ?: "Unknown"
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()  // Return an empty list in case of any exception
        }
    }

    suspend fun fetchTrucks(): List<String> {
        return try {
            db.collection("trucks").get().await().map { it.getString("licensePlates") ?: "" }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun fetchBoxes(): List<String> {
        return try {
            db.collection("boxes").get().await().map { it.getString("template") ?: "" }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createTrip(
        driverId: String,
        driverName: String,
        truck: String,
        box: String,
        customerName: String,
        origin: String,
        destination: String,
        kilometers: String,
        cargo: String,
        startDate: String,
        deadline: String,
        charge: String,
        expenses: String,
        paymentType: String,
        whatsappNumber: String,
        adminId: String,
        tripIdentifier: String // Added tripIdentifier parameter
    ) {
        try {
            // Get the current trip count
            val tripCount = getTripCount()

            // Create a new trip map
            val trip = hashMapOf(
                "tripIdentifier" to tripIdentifier,
                "driverId" to driverId,
                "driverName" to driverName,
                "truck" to truck,
                "box" to box,
                "customerName" to customerName,
                "origin" to origin,
                "destination" to destination,
                "kilometers" to kilometers,
                "cargo" to cargo,
                "startDate" to startDate,
                "deadline" to deadline,
                "charge" to charge,
                "expenses" to expenses,
                "paymentType" to paymentType,
                "whatsappNumber" to whatsappNumber,
                "status" to "",
                "adminId" to adminId
            )

            // Add the trip to the trips collection in Firestore
            tripsCollection.add(trip).await()

            // Increment the trip count (for the next trip)
            updateTripCount(tripCount + 1)

            // Log success or handle further logic
            println("Trip created successfully!")

        } catch (e: Exception) {
            // Handle exceptions (e.g., Firebase Firestore exceptions, network issues)
            e.printStackTrace()
            // Handle specific exceptions as needed
            throw e
        }
    }

    suspend fun fetchTripsForAdmin(adminId: String): Result<List<Trip>> {
        return try {
            val querySnapshot = tripsCollection
                .whereEqualTo("adminId", adminId) // Filter trips by adminId
                .get()
                .await()

            val trips = querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(Trip::class.java)
            }

            Result.Success(trips)
        } catch (e: Exception) {
            Result.Error(e.message ?: "Error fetching trips")
        }
    }

    suspend fun getExpensesForTrip(tripId: String): List<Expensee> {
        return try {
            val expensesSnapshot = tripsCollection
                .document(tripId)
                .collection("expenses")
                .get()
                .await()

            expensesSnapshot.toObjects(Expensee::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getDepositsForTrip(tripId: String): List<Deposit> {
        return withContext(Dispatchers.IO) {
            val depositsSnapshot = db
                .collection("trips")
                .document(tripId)
                .collection("deposits")
                .get()
                .await()

            depositsSnapshot.toObjects(Deposit::class.java)
        }
    }

    suspend fun addDeposit(tripId: String, deposit: Deposit) {
        val depositData = hashMapOf(
            "amount" to deposit.amount,
            "method" to deposit.method,
            "timestamp" to deposit.timestamp
        )
        tripsCollection
            .document(tripId)
            .collection("deposits")
            .add(depositData)
            .await()
    }

    suspend fun getTripCount(): Int {
        return try {
            val querySnapshot = db.collection("meta").document("counters").get().await()
            querySnapshot.getLong("tripCount")?.toInt() ?: 0
        } catch (e: Exception) {
            0
        }
    }

    private suspend fun updateTripCount(count: Int) {
        try {
            db.collection("meta").document("counters").set(mapOf("tripCount" to count), SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}



