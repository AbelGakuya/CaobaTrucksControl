package com.example.caobatruckscontrol.presentation.composables.admin.viewmodel
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.caobatruckscontrol.data.model.Trip
import com.example.caobatruckscontrol.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.example.caobatruckscontrol.common.Result
import com.example.caobatruckscontrol.data.model.CustomerPayment
import com.example.caobatruckscontrol.data.model.Deposit
import com.example.caobatruckscontrol.data.model.Expense
import com.example.caobatruckscontrol.data.model.Expensee
import com.example.caobatruckscontrol.data.model.RegisteredDriver
import com.example.caobatruckscontrol.data.model.TripReport
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit


@HiltViewModel
class TripViewModel @Inject constructor(
    private val repository: TripRepository
) : ViewModel()
{

    private val _drivers = MutableStateFlow<List<RegisteredDriver>>(emptyList())
    val drivers: StateFlow<List<RegisteredDriver>> = _drivers

    private val _trucks = MutableStateFlow<List<String>>(emptyList())
    val trucks: StateFlow<List<String>> = _trucks

    private val _boxes = MutableStateFlow<List<String>>(emptyList())
    val boxes: StateFlow<List<String>> = _boxes

    private val _tripCreated = MutableStateFlow<Boolean?>(null)
    val tripCreated: StateFlow<Boolean?> = _tripCreated

//    private val _trips = MutableStateFlow<Result<List<Trip>>>(Result.Success(emptyList()))
//    val trips: StateFlow<Result<List<Trip>>> = _trips

    private val _trips = MutableStateFlow<Result<List<Trip>>>(Result.Loading)
    val trips: StateFlow<Result<List<Trip>>> = _trips.asStateFlow()

    private var tripCounter = MutableStateFlow(0)

    init {
        fetchDrivers()
        initializeTripCounter()
    }

    private fun initializeTripCounter() {
        viewModelScope.launch {
            val count = repository.getTripCount() // Implement repository method to get the trip count
            tripCounter.value = count
        }
    }

    fun fetchCollectionData() {
        viewModelScope.launch {
           // _drivers.update { repository.fetchDrivers() }
            _trucks.update { repository.fetchTrucks() }
            _boxes.update { repository.fetchBoxes() }
        }
    }

    private fun fetchDrivers() {
        viewModelScope.launch {
            _drivers.value = repository.fetchDrivers()
        }
    }

    fun createTrip(
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
        adminId: String
    ) {
        viewModelScope.launch {
            val tripIdentifier = String.format("CTC%04d", tripCounter.value + 1)
            repository.createTrip(
                driverId, driverName, truck, box, customerName, origin, destination,
                kilometers, cargo, startDate, deadline, charge, expenses, paymentType, whatsappNumber, adminId, tripIdentifier
            )
            _tripCreated.value = true
            tripCounter.value += 1 // Increment trip counter
        }
    }

    fun resetTripCreated() {
        _tripCreated.value = null
    }

    fun fetchTripsForAdmin(adminId: String) {
        viewModelScope.launch {
            _loading.value = true
            val result = repository.fetchTripsForAdmin(adminId)
            _trips.value = result // Set the result directly, assuming fetchTripsForAdmin returns Result<List<Trip>>
            _loading.value = false
        }
    }

    private val _depositsMap = MutableStateFlow<Map<String, List<Deposit>>>(emptyMap())
    val depositsMap: StateFlow<Map<String, List<Deposit>>> = _depositsMap

    fun getDepositsForTrip(tripId: String) {
        viewModelScope.launch {
            val deposits = repository.getDepositsForTrip(tripId)
            _depositsMap.update { currentMap ->
                currentMap.toMutableMap().apply {
                    this[tripId] = deposits
                }
            }
        }
    }


    private val _expensesMap = MutableStateFlow<Map<String, List<Expensee>>>(emptyMap())
    val expensesMap: StateFlow<Map<String, List<Expensee>>> = _expensesMap


    fun getExpensesForTrip(tripId: String) {
        viewModelScope.launch {
            val expenses = repository.getExpensesForTrip(tripId)
            _expensesMap.update { currentMap ->
                currentMap.toMutableMap().apply {
                    this[tripId] = expenses
                }
            }
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToastMessage() {
        _toastMessage.value = null
    }




    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean>
        get() = _loading

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?>
        get() = _toastMessage

    fun addDeposit(tripId: String, amount: Double, method: String) {
        _loading.value = true // Start loading

        viewModelScope.launch {
            try {
                val deposit = Deposit(
                    amount = amount,
                    method = method,
                    timestamp = Timestamp.now() // Or use a timestamp as per your requirement
                )
                repository.addDeposit(tripId, deposit)
                _toastMessage.value = "Deposit added successfully!"
            } catch (e: Exception) {
                _toastMessage.value = "Error adding deposit: ${e.message}"
            } finally {
                _loading.value = false // Stop loading
            }
        }
    }

    private val _paymentResult = MutableLiveData<Result<String>>()
    val paymentResult: LiveData<Result<String>> = _paymentResult

    fun acceptCustomerPayment(tripId: String, amount: Double) {
        viewModelScope.launch {
            try {
                val db = FirebaseFirestore.getInstance()
                val tripRef = db.collection("trips").document(tripId)
                val paymentsRef = tripRef.collection("customerPayments")

                // Query for existing payments
                val existingPayment = paymentsRef.limit(1).get().await().documents.firstOrNull()

                if (existingPayment != null) {
                    // Update the existing payment
                    existingPayment.reference.update(
                        "amount", amount,
                        "timestamp", Timestamp.now()
                    ).await()
                    _paymentResult.value = Result.Success(existingPayment.id)
                } else {
                    // Add a new payment
                    val payment = CustomerPayment(amount = amount, timestamp = Timestamp.now())
                    val paymentResult = paymentsRef.add(payment).await()
                    _paymentResult.value = Result.Success(paymentResult.id)
                }
            } catch (e: Exception) {
                _paymentResult.value = Result.Error(e.toString())
            }
        }
    }

    private val firestore = FirebaseFirestore.getInstance()

    // LiveData to hold current trip details
    val currentTrip = MutableLiveData<Trip?>()
    val expenses = MutableLiveData<List<Expensee>>()
    val deposits = MutableLiveData<List<Deposit>>()

    // Constant wear variable
    private val wearVariable = 0.05

    fun fetchTripDetails(tripId: String) {
        viewModelScope.launch {
            try {
                val tripSnapshot = firestore.collection("trips").document(tripId).get().await()
                val trip = tripSnapshot.toObject(Trip::class.java)
                currentTrip.value = trip

                trip?.let {
                    val expensesSnapshot = firestore.collection("trips").document(tripId)
                        .collection("expenses").get().await()
                    val expensesList = expensesSnapshot.toObjects(Expensee::class.java)
                    expenses.value = expensesList

                    val depositsSnapshot = firestore.collection("trips").document(tripId)
                        .collection("deposits").get().await()
                    val depositsList = depositsSnapshot.toObjects(Deposit::class.java)
                    deposits.value = depositsList
                }
            } catch (e: Exception) {
                Log.e("TripViewModel", "Error fetching trip details: ", e)
            }
        }
    }

    fun endTrip(tripId: String, onTripEnded: (TripReport) -> Unit) {
        viewModelScope.launch {
            try {
                firestore.runTransaction { transaction ->
                    val tripRef = firestore.collection("trips").document(tripId)
                    val completedTripRef = firestore.collection("completedTrips").document(tripId)
                    val reportRef = firestore.collection("reports").document(tripId)

                    // Fetch the trip details
                    val tripSnapshot = transaction.get(tripRef)
                    val trip = tripSnapshot.toObject(Trip::class.java)

                    trip?.let {
                        // Update the status
                        val updatedTrip = it.copy(status = "completed")

                        // Save to completed trips collection
                        transaction.set(completedTripRef, updatedTrip)

                        // Copy expenses subcollection
                        val expensesRef = tripRef.collection("expenses")
                        val completedExpensesRef = completedTripRef.collection("expenses")
                        val expensesSnapshot = runBlocking { expensesRef.get().await() }
                        for (expense in expensesSnapshot.documents) {
                            expense.toObject(Expensee::class.java)?.let { it1 ->
                                transaction.set(completedExpensesRef.document(expense.id),
                                    it1
                                )
                                transaction.delete(expense.reference)
                            }
                        }

                        // Copy deposits subcollection
                        val depositsRef = tripRef.collection("deposits")
                        val completedDepositsRef = completedTripRef.collection("deposits")
                        val depositsSnapshot = runBlocking { depositsRef.get().await() }
                        for (deposit in depositsSnapshot.documents) {
                            deposit.toObject(Deposit::class.java)?.let { it1 ->
                                transaction.set(completedDepositsRef.document(deposit.id),
                                    it1
                                )
                                transaction.delete(deposit.reference)
                            }
                        }

                        // Copy customer payments subcollection
                        val customerPaymentsRef = tripRef.collection("customerPayments")
                        val completedCustomerPaymentsRef = completedTripRef.collection("customerPayments")
                        val customerPaymentsSnapshot = runBlocking { customerPaymentsRef.get().await()}
                        for (payment in customerPaymentsSnapshot.documents) {
                            payment.toObject(CustomerPayment::class.java)?.let { paymentData ->
                                transaction.set(completedCustomerPaymentsRef.document(payment.id), paymentData)
                                transaction.delete(payment.reference)
                            }
                        }

                        // Save the trip report
                        val dieselExpenses = expensesSnapshot.toObjects(Expensee::class.java)
                            .filter { it.category == "Diesel" }
                            .sumOf { it.amount }
                        val mileage = trip.kilometers.toDoubleOrNull()?.let { it / dieselExpenses } ?: 0.0
                        val wearCost = trip.kilometers.toDoubleOrNull()?.let { it * wearVariable } ?: 0.0
                        val totalExpenses = expensesSnapshot.toObjects(Expensee::class.java).sumOf { it.amount }
                        val totalDeposits = depositsSnapshot.toObjects(Deposit::class.java).sumOf { it.amount }

                        val tripReport = TripReport(
                            driverName = trip.driverName,
                            adminId = trip.adminId,
                            truck = trip.truck,
                            box = trip.box,
                            origin = trip.origin,
                            destination = trip.destination,
                            kilometers = trip.kilometers,
                            cargo = trip.cargo,
                            journeyTime = calculateJourneyTime(trip.startDate, trip.deadline),
                            mileage = mileage,
                            totalExpenses = totalExpenses,
                            totalDeposits = totalDeposits,
                            wearCost = wearCost,
                            customerPayment = trip.charge,
                            expenseComparison = totalExpenses - totalDeposits
                        )

                        transaction.set(reportRef, tripReport)

                        // Delete the original trip
                        transaction.delete(tripRef)

                        // Return the trip report through the callback
                        onTripEnded(tripReport)
                    }
                }.addOnSuccessListener {
                    // Handle success
                    currentTrip.value = null
                    expenses.value = emptyList()
                    deposits.value = emptyList()
                }.addOnFailureListener { e ->
                    // Handle failure
                    Log.e("TripViewModel", "Error ending trip: ", e)
                }
            } catch (e: Exception) {
                Log.e("TripViewModel", "Error ending trip: ", e)
            }
        }
    }


    private fun calculateJourneyTime(startDate: String, endDate: String): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return try {
            val start = dateFormat.parse(startDate)
            val end = dateFormat.parse(endDate)

            if (start != null && end != null) {
                val duration = end.time - start.time
                val hours = TimeUnit.MILLISECONDS.toHours(duration)
                "$hours hours"
            } else {
                "N/A"
            }
        } catch (e: ParseException) {
            Log.e("CalculateJourneyTime", "Error parsing dates: $startDate - $endDate", e)
            "N/A"
        }
    }
}


