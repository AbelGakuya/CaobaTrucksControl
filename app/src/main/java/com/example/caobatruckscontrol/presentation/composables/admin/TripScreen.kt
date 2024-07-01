package com.example.caobatruckscontrol.presentation.composables.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.caobatruckscontrol.data.model.Trip
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.TripViewModel
import com.example.caobatruckscontrol.common.Result
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.State
import androidx.compose.runtime.livedata.observeAsState // Ensure this is not accidentally imported
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import com.example.caobatruckscontrol.data.model.Deposit
import com.example.caobatruckscontrol.data.model.Expense


import com.example.caobatruckscontrol.presentation.composables.driver.viewmodel.DriverExpenseViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.StateFlow



@Composable
fun TripsScreen(
    navController: NavController,
    tripViewModel: TripViewModel = hiltViewModel(),
    onTripClicked: (Trip) -> Unit
) {
    // Fetch trips when the screen is displayed
    val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    tripViewModel.fetchTripsForAdmin(adminId)

    // Collecting states from ViewModel
    val tripsResult: Result<List<Trip>> by tripViewModel.trips.collectAsState()

    val toastMessage by tripViewModel.toastMessage.collectAsState()
    val loading by tripViewModel.loading.collectAsState()

    // UI states for deposit amount and selected method
    var depositAmount by remember { mutableStateOf("") }
    var selectedMethod by remember { mutableStateOf("Cash") }
    val depositMethods = listOf("Cash", "Debit Card", "Credit Card")
    var expanded by remember { mutableStateOf(false) }
    val expandedStates = remember { mutableStateMapOf<String, Boolean>() }
    var inputError by remember { mutableStateOf<String?>(null) }

    //var loading by remember { mutableStateOf(false) }
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding()
    ) {
        Text("Trips Screen",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        when (tripsResult) {
            is Result.Success -> {
                val trips = (tripsResult as Result.Success<List<Trip>>).data
                LazyColumn(
                    modifier = Modifier
                       // .weight(1f)
                        .fillMaxWidth()
                       // .padding(16.dp)
                ) {
                    items(trips) { trip ->

                        tripViewModel.getExpensesForTrip(trip.id)

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                                .background(Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(16.dp)
                            ) {

                                Text(
                                    text = "Trip Identifier: ${trip.tripIdentifier}",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier
                                        .padding(bottom = 8.dp)
                                )
                                Text(
                                    text = "Driver: ${trip.driverName}",
                                    style = TextStyle(fontSize = 16.sp),
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                )


                                Text(
                                    text = "Truck: ${trip.truck}",
                                    style = TextStyle(fontSize = 16.sp),
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                )
                                Text(
                                    text = "Box: ${trip.box}",
                                    style = TextStyle(fontSize = 16.sp),
                                    modifier = Modifier
                                        .padding(bottom = 4.dp)
                                )
//                                Text(
//                                    text = "Remaining Time: ${trip.remainingTime}",
//                                    style = TextStyle(fontSize = 16.sp),
//                                    modifier = Modifier.padding(bottom = 8.dp)
//                                )
                                Divider(
                                    color = Color.Gray,
                                    thickness = 1.dp,
                                    modifier = Modifier
                                    .padding(vertical = 8.dp)
                                )

                                // Deposits section
                                Text(
                                    text = "Deposits:",
                                    style = TextStyle(
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                trip.deposits.forEach { deposit ->
                                    Text(
                                        text = "Amount: ${deposit.amount} - Method: ${deposit.method}",
                                        style = TextStyle(fontSize = 16.sp),
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedTextField(
                                    value = depositAmount,
                                    onValueChange = { depositAmount = it },
                                    label = { Text("Deposit Amount") },
                                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                                    isError = inputError != null
                                )
                                if (inputError != null) {
                                    Text(
                                        text = inputError!!,
                                        color = Color.Red,
                                        style = TextStyle(fontSize = 12.sp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Dropdown for deposit method
                                // Dropdown for deposit method
                                Box {
                                    OutlinedTextField(
                                        value = selectedMethod,
                                        onValueChange = {},
                                        label = { Text("Deposit Method") },
                                        readOnly = true,
                                        trailingIcon = {
                                            Icon(
                                                imageVector = Icons.Default.ArrowDropDown,
                                                contentDescription = "Dropdown Icon",
                                                modifier = Modifier.clickable {
                                                    expandedStates[trip.id] = !(expandedStates[trip.id] ?: false)
                                                }
                                            )
                                        }
                                    )

                                    DropdownMenu(
                                        expanded = expandedStates[trip.id] ?: false,
                                        onDismissRequest = { expandedStates[trip.id] = false }
                                    ) {
                                        depositMethods.forEach { method ->
                                            DropdownMenuItem(onClick = {
                                                selectedMethod = method
                                                expandedStates[trip.id] = false
                                            },
                                                text ={
                                                    Text(text = method)
                                                })
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                if (loading) {
                                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                                } else {
                                    Button(
                                        onClick = {
                                            val cleanedAmount = depositAmount.replace(Regex("[^0-9.]"), "")
                                            if (cleanedAmount.isNotEmpty()) {
                                                try {
                                                    val amount = cleanedAmount.toDouble()
                                                    tripViewModel.addDeposit(trip.id, amount, selectedMethod)
                                                } catch (e: NumberFormatException) {
                                                    // Handle invalid amount
                                                    tripViewModel.showToast("Invalid amount")
                                                }
                                            } else {
                                                // Handle empty amount
                                                tripViewModel.showToast("Amount cannot be empty")
                                            }
                                        },
                                        modifier = Modifier.padding(top = 8.dp)
                                    ) {
                                        Text(text = "Add Deposit")
                                    }
                                }

                                // Display toast message when not null
                                toastMessage?.let { message ->
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    tripViewModel.clearToastMessage()
                                    depositAmount = ""// Clear toast message after showing
                                }


                                Spacer(
                                    modifier = Modifier
                                    .height(16.dp)
                                )


                                // Expenses section
                                ExpensesSection(trip.id, tripViewModel)

                                Spacer(modifier = Modifier.height(16.dp))

//                                // Balance section
//                                val balanceColor = if (trip.balance >= 0) Color.Black else Color.Red
//                                Text(
//                                    text = "Balance: ${trip.balance}",
//                                    style = TextStyle(
//                                        fontSize = 18.sp,
//                                        color = balanceColor
//                                    ),
//                                    modifier = Modifier.padding(bottom = 8.dp)
//                                )

//                                // Cash received section
//                                Text(
//                                    text = "Cash Received: ${trip.cashReceived}",
//                                    style = TextStyle(fontSize = 18.sp),
//                                    modifier = Modifier.padding(bottom = 8.dp)
//                                )
//
//                                // Payment to company section (optional)
//                                trip.paymentToCompany?.let { payment ->
//                                    Row(verticalAlignment = Alignment.CenterVertically) {
//                                        Text(
//                                            text = "Payment to Company: $payment",
//                                            style = TextStyle(fontSize = 18.sp),
//                                            modifier = Modifier.weight(1f)
//                                        )
//                                        // Switch to enable/disable payment entry
//                                        Switch(
//                                            checked = false,
//                                            onCheckedChange = { /* Handle switch state */ },
//                                            modifier = Modifier.padding(start = 8.dp)
//                                        )
//                                    }
//                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Close Trip button
                                Button(
                                    onClick = { /* Handle close trip */ },
                                    modifier = Modifier.align(Alignment.CenterHorizontally)
                                ) {
                                    Text(text = "Close Trip")
                                }
                            }
                        }
                    }
                }
            }
            is Result.Error -> {
                val errorMessage = (tripsResult as Result.Error).message
                Text("Error: $errorMessage")
            }

            Result.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

        }


}

    // Floating action button for creating new trips
    Box(
        modifier = Modifier
            .fillMaxSize()
            .navigationBarsPadding()
            .padding(16.dp),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = {
                /* Handle new trip creation */
                navController.navigate("create_trip")
            },
            modifier = Modifier.padding(16.dp),
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
        Text("No expenses found for this trip.")
    }
}




