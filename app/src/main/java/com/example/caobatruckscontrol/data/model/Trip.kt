package com.example.caobatruckscontrol.data.model

// Sample data class for trip details
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId

data class Trip(
    @DocumentId val id: String = "",
    val driverId: String = "",
    val driverName: String = "",
    val truck: String = "",
    val box: String = "",
    val customerName: String = "",
    val origin: String = "",
    val destination: String = "",
    val kilometers: String = "",
    val cargo: String = "",
    val startDate: String = "",
    val deadline: String = "",
    val charge: String = "",
    val expenses: String = "",
    val paymentType: String = "",
    val whatsappNumber: String = "",
    val adminId: String = "",
    val accepted: Boolean = false,
    val acceptedAt: Long? = null,
    val deposits: List<Deposit> = emptyList(),
    val expensesList: List<TripExpense> = emptyList(),
    val tripIdentifier: String = "",
    val status: String = ""
)

data class Deposit(
    val amount: Double = 0.0,
    val method: String = "",
    val timestamp: Timestamp = Timestamp.now()
)



data class TripExpense(
    val description: String,
    val amount: Double,
    val receiptImage: String // Placeholder for image reference
)

