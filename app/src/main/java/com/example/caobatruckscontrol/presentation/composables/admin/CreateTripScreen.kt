package com.example.caobatruckscontrol.presentation.composables.admin

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.caobatruckscontrol.presentation.composables.admin.components.DropdownField
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.TripViewModel
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTripScreen(
    navController: NavController,
    tripViewModel: TripViewModel = hiltViewModel(),
    onTripCreated: () -> Unit
) {
    val drivers by tripViewModel.drivers.collectAsState()
    val trucks by tripViewModel.trucks.collectAsState()
    val boxes by tripViewModel.boxes.collectAsState()
    val tripCreated by tripViewModel.tripCreated.collectAsState()

    LaunchedEffect(Unit) {
        tripViewModel.fetchCollectionData()
    }

    LaunchedEffect(tripCreated) {
        tripCreated?.let {
            if (it) {
                onTripCreated()
                tripViewModel.resetTripCreated()
            }
        }
    }


    val driverNames = drivers.map { it.name }
    val (selectedDriverName, setSelectedDriverName) = remember { mutableStateOf("") }
    val selectedDriver = drivers.find { it.name == selectedDriverName }

    //val (driver, setDriver) = remember { mutableStateOf("") }
    val (truck, setTruck) = remember { mutableStateOf("") }
    val (box, setBox) = remember { mutableStateOf("") }
    val (customerName, setCustomerName) = remember { mutableStateOf("") }
    val (origin, setOrigin) = remember { mutableStateOf("") }
    val (destination, setDestination) = remember { mutableStateOf("") }
    val (kilometers, setKilometers) = remember { mutableStateOf("") }
    val (cargo, setCargo) = remember { mutableStateOf("") }
    //val (startDate, setStartDate) = remember { mutableStateOf("") }
    //val (deadline, setDeadline) = remember { mutableStateOf("") }
    val (charge, setCharge) = remember { mutableStateOf("") }
    val (expenses, setExpenses) = remember { mutableStateOf("") }
    val (paymentType, setPaymentType) = remember { mutableStateOf("Cash") }
    val (whatsappNumber, setWhatsappNumber) = remember { mutableStateOf("") }

    var deadline by remember { mutableStateOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) }
    var startDate by remember { mutableStateOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) }

    val adminId = FirebaseAuth.getInstance().currentUser?.uid ?: ""


    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding()
    ) {
        item {
            Text(
                text = "Create New Trip",
                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        item {
            DropdownField(label = "Driver", options = driverNames, selectedOption = selectedDriverName,
                onOptionSelected = setSelectedDriverName)
            DropdownField(label = "Truck", options = trucks, selectedOption = truck, onOptionSelected = setTruck)
            DropdownField(label = "Box", options = boxes, selectedOption = box, onOptionSelected = setBox)
        }

        item {
            OutlinedTextField(value = customerName, onValueChange = setCustomerName, label = { Text("Customer Name") })
            OutlinedTextField(value = origin, onValueChange = setOrigin, label = { Text("Origin") })
            OutlinedTextField(value = destination, onValueChange = setDestination, label = { Text("Destination") })
            OutlinedTextField(value = kilometers, onValueChange = setKilometers, label = { Text("Kilometers") })
            OutlinedTextField(value = cargo, onValueChange = setCargo, label = { Text("Cargo") })
            //OutlinedTextField(value = startDate, onValueChange = setStartDate, label = { Text("Start Date") })
           // OutlinedTextField(value = deadline, onValueChange = setDeadline, label = { Text("Deadline") })

            LabeledDatePicker(
                label = "Select Start Date",
                dateText = startDate,
                initialDate = LocalDateTime.now(),
                onDateSelected = { selectedDateTime ->
                    startDate = selectedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) // Update dateText on selection
                }
            )

            LabeledDatePicker(
                label = "Select Deadline Date",
                dateText = deadline,
                initialDate = LocalDateTime.now(),
                onDateSelected = { selectedDateTime ->
                    deadline = selectedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) // Update dateText on selection
                }
            )
            OutlinedTextField(value = charge, onValueChange = setCharge, label = { Text("Charge") })
            OutlinedTextField(value = expenses, onValueChange = setExpenses, label = { Text("Initial amount for expenses") })
        }

        item {
            Text(
                text = "Payment Type",
                style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        item {
            Row {
                RadioButton(selected = paymentType == "Cash", onClick = { setPaymentType("Cash") })
                Text("Cash")
                Spacer(modifier = Modifier.width(16.dp))
                RadioButton(selected = paymentType == "Wire Transfer", onClick = { setPaymentType("Wire Transfer") })
                Text("Wire Transfer")
            }
        }

        item {
            OutlinedTextField(
                value = whatsappNumber,
                onValueChange = setWhatsappNumber,
                label = { Text("Customer's WhatsApp Number") },
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        item {
            Button(
                onClick = {
                    selectedDriver?.let { driver ->

                        tripViewModel.createTrip(
                            driver.id,
                            driver.name,
                            truck,
                            box,
                            customerName,
                            origin,
                            destination,
                            kilometers,
                            cargo,
                            startDate,
                            deadline,
                            charge,
                            expenses,
                            paymentType,
                            whatsappNumber,
                            adminId
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Create Trip")
            }
        }
    }
}
