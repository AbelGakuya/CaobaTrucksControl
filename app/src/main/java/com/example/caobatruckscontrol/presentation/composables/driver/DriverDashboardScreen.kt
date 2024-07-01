package com.example.caobatruckscontrol.presentation.composables.driver

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.caobatruckscontrol.data.model.Trip
import com.example.caobatruckscontrol.presentation.composables.driver.viewmodel.DriverDashboardViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.TextField
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.DriverViewModel
import java.util.Calendar
import kotlin.concurrent.fixedRateTimer
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.TripViewModel
import com.example.caobatruckscontrol.common.Result
import com.example.caobatruckscontrol.data.model.TripReport


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverDashboardScreen(
    navController: NavController,
    viewModel: DriverDashboardViewModel = hiltViewModel(),
    tripViewModel: TripViewModel = hiltViewModel()
) {
    val trips by viewModel.trips.observeAsState(emptyList())
    val loading by viewModel.loading.observeAsState(true)

    val driverId = FirebaseAuth.getInstance().currentUser?.uid

    // Fetch trips on screen creation (optional)
    LaunchedEffect(Unit) {
        viewModel.fetchTrips(driverId!!)
    }

    // Callback to trigger screen recomposition after trip ends
    val onTripEnded: () -> Unit = {
        viewModel.fetchTrips(driverId!!) // Fetch trips again after trip ends
    }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Driver Dashboard") },
               // backgroundColor = MaterialTheme.colors.primary
            )
        },
        content = {paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize(),
//                verticalArrangement = Arrangement.Center,
//                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (loading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    if (trips.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "You have no trips",
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        TripList(
                            navController,
                            trips = trips,
                            driverId!! ,
                            viewModel,
                            tripViewModel,
                            onTripEnded
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun TripList(
    navController: NavController,
    trips: List<Trip>,
    driverId: String,
    viewModel: DriverDashboardViewModel,
    tripViewModel: TripViewModel,
    onTripEnded: () -> Unit

) {
    LazyColumn(
        verticalArrangement = Arrangement.Top, // Ensure list starts from the top
        modifier = Modifier.fillMaxSize()
    ) {
        items(trips) { trip ->
            tripViewModel.getExpensesForTrip(trip.id)

            tripViewModel.getDepositsForTrip(trip.id)

            TripListItem(
                navController = navController,
                trip = trip,
                driverId,
                viewModel,
                tripViewModel,
                onTripEnded
            )

        }
    }
}

@Composable
fun TripListItem(
    navController: NavController,
    trip: Trip,
    driverId: String,
    viewModel: DriverDashboardViewModel,
    tripViewModel: TripViewModel,
    onTripEnded: () -> Unit
) {
    // State to hold remaining time
    var remainingTime by remember { mutableStateOf(calculateRemainingTime(trip)) }

    DisposableEffect(Unit) {
        val timer = fixedRateTimer("Timer", false, 0L, 1000) {
            remainingTime = calculateRemainingTime(trip)
        }
        onDispose {
            timer.cancel()
        }
    }

    // Function to show toast message
    val context = LocalContext.current
    val showToast: (String) -> Unit = { message ->
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text(
                text = "Trip Details",
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Display trip details
            Text(
                text = "Truck: ${trip.truck}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Box: ${trip.box}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Start Date: ${trip.startDate}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Deadline: ${trip.deadline}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                text = "Cargo: ${trip.cargo}",
                style = MaterialTheme.typography.body1,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Display remaining time or accept button
            if (trip.accepted) {
                CountdownTimer(remainingTime)

                Spacer(modifier = Modifier.height(16.dp))

                ExpensesSection(
                    trip.id,
                    tripViewModel
                )

                DepositsSection(
                    trip.id,
                    tripViewModel
                )

                AcceptPaymentButton(
                    trip = trip,
                    tripViewModel = tripViewModel,
                    showToast = showToast
                )

                Spacer(modifier = Modifier.height(16.dp))

                EndTripButton(
                    trip = trip,
                    tripViewModel = tripViewModel,
                    viewModel,
                    onTripEnded
                    //showDialog = remember { mutableStateOf(false) }
                )

                FAB(trip,navController)
            } else {
                Button(
                    onClick = {
                        viewModel.acceptTrip(driverId, trip.id)
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Accept Trip")
                }
            }
        }
    }
}



@Composable
fun EndTripButton(
    trip: Trip,
    tripViewModel: TripViewModel,
    viewModel: DriverDashboardViewModel,
    onTripEnded: () -> Unit
) {
    val showEndTripDialog = remember { mutableStateOf(false) }
    val showReportDialog = remember { mutableStateOf(false) }
    val reportData = remember { mutableStateOf<TripReport?>(null) }

    Button(
        onClick = { showEndTripDialog.value = true }
    ) {
        Text("End Trip")
    }

    // Confirmation dialog to end the trip
    if (showEndTripDialog.value) {
        AlertDialog(
            onDismissRequest = { showEndTripDialog.value = false },
            title = { Text("End Trip") },
            text = { Text("Do you want to end the trip?") },
            confirmButton = {
                Button(
                    onClick = {
                        showEndTripDialog.value = false
                        tripViewModel.endTrip(trip.id) { tripReport ->
                            reportData.value = tripReport
                            showReportDialog.value = true
                        }
                        //onTripEnded()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showEndTripDialog.value = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // Trip report dialog
    reportData.value?.let { tripReport ->
        if (showReportDialog.value) {
            AlertDialog(
                onDismissRequest = { showReportDialog.value = false },
                title = { Text("Trip Report") },
                text = { TripReportContent(tripReport) },
                confirmButton = {
                    Button(
                        onClick = {
                            showReportDialog.value = false

                            onTripEnded()
                        }
                    ) {
                        Text("Close")
                    }
                }
            )
        }
    }
}

@Composable
fun ExpensesSection(tripId: String, viewModel: TripViewModel) {
    val expensesMap by viewModel.expensesMap.collectAsState()
    val expenses = expensesMap[tripId] ?: emptyList()

    if (expenses.isNotEmpty()) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                text = "Expenses:",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            expenses.forEach { expense ->
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "Category: ${expense.category}",
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Amount: ${expense.amount}",
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    } else {
        Text(
            text = "Expenses:",
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text("There are currently no expenses found for this trip.")

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DepositsSection(
    tripId: String,
    viewModel: TripViewModel
) {
    val depositsMap by viewModel.depositsMap.collectAsState()
    val deposits = depositsMap[tripId] ?: emptyList()

    if (deposits.isNotEmpty()) {
        Column(modifier = Modifier.padding(top = 8.dp)) {
            Text(
                text = "Deposits:",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 8.dp)
            )
            deposits.forEach { deposit ->
                Column(modifier = Modifier.padding(bottom = 8.dp)) {
                    Text(
                        text = "Amount: ${deposit.amount}",
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = "Method: ${deposit.method}",
                        style = TextStyle(fontSize = 14.sp),
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
            }
        }
    } else {
        Text(
            text = "Deposits:",
            style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text("There are currently no deposits found for this trip.")

        Spacer(modifier = Modifier.height(16.dp))
    }
}




@Composable
fun FAB(
    trip: Trip,
    navController: NavController
){
    // Floating action button for creating new trips
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(8.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                /* Handle new trip creation */
                navController.navigate("driver_expense/${trip.id}")
            },
            modifier = Modifier.padding(8.dp),
            containerColor = Color.Green // Set background color here
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add"
            )
        }
    }
}


@Composable
fun AcceptPaymentButton(
    trip: Trip,
    tripViewModel: TripViewModel,
    showToast: (String) -> Unit // Callback to show toast message
) {
    var showDialog by remember { mutableStateOf(false) }
    var paymentAmount by remember { mutableStateOf("") }

    // LiveData to observe the payment result
    val paymentResult: LiveData<Result<String>> = tripViewModel.paymentResult

    // Observe the LiveData
    val result by paymentResult.observeAsState()

    // State to manage toast visibility
    var toastMessage by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    // Function to handle accepting payment
    fun handleAcceptPayment() {
        val amount = paymentAmount.toDoubleOrNull()
        if (amount != null && amount > 0) {
            tripViewModel.acceptCustomerPayment(trip.id, amount)

        } else {
            toastMessage = "Invalid amount entered."
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Accept Customer Payment") },
            text = {
                Column {
                    Text(text = "Enter amount paid by customer:")
                    TextField(
                        value = paymentAmount,
                        onValueChange = { paymentAmount = it },
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        isLoading = true
                        handleAcceptPayment()
                        isLoading = false
                        showDialog = false
                    }
                ) {
                    Text("Accept")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Button(
        onClick = { showDialog = true }
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = Color.White)
        } else {
            Text(
                text = "Accept Payment",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }

    // Handle payment result
    LaunchedEffect(result) {
        result?.let { paymentResult ->
            isLoading = false
            when (paymentResult) {
                is Result.Success -> {
                    toastMessage = "Payment accepted!"
                }
                is Result.Error -> {
                    toastMessage = "Failed to accept payment: ${paymentResult.message}"
                }
                is Result.Loading -> {
                    isLoading = true
                }
            }
        }
    }

    // Show toast message
    toastMessage?.let {
        LaunchedEffect(it) {
            showToast(it)
            toastMessage = null // Reset toast message state after showing
        }
    }
}








@Composable
fun TripReportContent(tripReport: TripReport) {
    Column {
        Text("Driver's Name: ${tripReport.driverName}")
        Text("Truck and Box: ${tripReport.truck}, ${tripReport.box}")
        Text("Starting City: ${tripReport.origin}")
        Text("Destination City: ${tripReport.destination}")
        Text("Kilometers Traveled: ${tripReport.kilometers}")
        Text("Cargo: ${tripReport.cargo}")
        Text("Journey Time: ${tripReport.journeyTime}")
        Text("Mileage: ${tripReport.mileage}")
        Text("Total Travel Expenses: ${tripReport.totalExpenses}")
        Text("Total Deposits for Travel Expenses: ${tripReport.totalDeposits}")
        Text("Wear Cost: ${tripReport.wearCost}")
        Text("Customer Payment: ${tripReport.customerPayment}")
        Text("Comparison of Travel Expenses Against Deposits: ${tripReport.expenseComparison}")
    }
}



@Composable
fun CountdownTimer(remainingTime: Long) {
    val hours = TimeUnit.MILLISECONDS.toHours(remainingTime)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(remainingTime) % 60

    val textColor = if (hours > 0 || minutes > 30) {
        Color.Green // More than 30 minutes left: Green color
    } else {
        Color.Red // Less than or equal to 30 minutes left: Red color
    }

    Text(
        text = "Remaining Time: ${String.format("%02d:%02d", hours, minutes)}",
        style = MaterialTheme.typography.body1.copy(color = textColor),
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

// Function to calculate remaining time
fun calculateRemainingTime(trip: Trip): Long {
    val deadlineDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val deadlineDate = deadlineDateFormat.parse(trip.deadline) ?: Date()

    val currentTime = Calendar.getInstance().time
    val remainingMillis = deadlineDate.time - currentTime.time

    return remainingMillis
}



