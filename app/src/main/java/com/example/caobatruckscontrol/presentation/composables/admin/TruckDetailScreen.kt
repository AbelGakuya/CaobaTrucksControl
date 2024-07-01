package com.example.caobatruckscontrol.presentation.composables.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.caobatruckscontrol.common.Result
import com.example.caobatruckscontrol.data.model.Driver
import com.example.caobatruckscontrol.data.model.Truck
import com.example.caobatruckscontrol.presentation.NavItem
import com.example.caobatruckscontrol.presentation.composables.admin.components.DropdownField
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.DriverDetailViewModel
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.TruckDetailViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun TruckDetailScreen(
    truckId: String?,
    navController: NavController,
    viewModel: TruckDetailViewModel = hiltViewModel()
) {
    val truckDetail by viewModel.truckDetail.collectAsState()
    val toggleStatusResult by viewModel.toggleStatusResult.collectAsState()
    val updateDriverResult by viewModel.updateDriverResult.collectAsState()



    // Fetch driver details initially
    LaunchedEffect(truckId) {
        viewModel.fetchTruckDetail(truckId!!)
    }

    // Navigate back if update is successful
    LaunchedEffect(updateDriverResult) {
        if (updateDriverResult is Result.Success) {
            navController.popBackStack()
        }
    }

    // Handle toggle status result
    LaunchedEffect(toggleStatusResult) {
        if (toggleStatusResult is Result.Success) {
            viewModel.fetchTruckDetail(truckId!!) // Refresh driver detail after status change
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp).padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding()
    ) {
        item {
            // Driver details section
            TruckDetailsSection(truckDetail, viewModel)
        }

        // Divider
        item {
            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }


    }
}

@Composable
private fun TruckDetailsSection(truckDetail: Truck?, viewModel: TruckDetailViewModel) {
    var brand by remember { mutableStateOf(truckDetail?.brand ?: "") }
    var model by remember { mutableStateOf(truckDetail?.model ?: "") }
    var modelYear by remember { mutableStateOf(truckDetail?.modelYear ?: "") }
    var licensePlates by remember { mutableStateOf(truckDetail?.licensePlates ?: "") }
    var color by remember { mutableStateOf(truckDetail?.color ?: "") }
    var circulationCardFolio by remember { mutableStateOf(truckDetail?.circulationCardFolio ?: "") }
    var insurancePolicy by remember { mutableStateOf(truckDetail?.insurancePolicy ?: "") }
    var insuranceCompany by remember { mutableStateOf(truckDetail?.insuranceCompany ?: "") }
    var isActive by remember { mutableStateOf(truckDetail?.active ?: false) }


    // Update local state when driverDetail changes
    LaunchedEffect(truckDetail) {
        brand = truckDetail?.brand ?: ""
        model = truckDetail?.model ?: ""
        modelYear = truckDetail?.modelYear ?: ""
        licensePlates = truckDetail?.licensePlates ?: ""
        color  = truckDetail?.color ?: ""
        circulationCardFolio = truckDetail?.circulationCardFolio ?: ""
        insurancePolicy = truckDetail?.insurancePolicy ?: ""
        insuranceCompany = truckDetail?.insuranceCompany ?: ""
        isActive = truckDetail?.active ?: false
    }

    val dropdownItems = listOf("True", "False")

    Column {
        Text("Truck Details",
            style = TextStyle(
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold))

        Spacer(modifier = Modifier.height(8.dp))

        truckDetail?.let { truck ->
            OutlinedTextField(
                value = brand,
                onValueChange = { newValue ->
                    brand = newValue
                },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = model,
                onValueChange = { newValue ->
                    model = newValue
                },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = modelYear,
                onValueChange = { newValue ->
                    modelYear = newValue
                },
                label = { Text("Date of Birth") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = licensePlates,
                onValueChange = { newValue ->
                    licensePlates = newValue
                },
                label = { Text("Identification Type") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = color,
                onValueChange = { newValue ->
                    color = newValue
                },
                label = { Text("Identification Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = circulationCardFolio,
                onValueChange = { newValue ->
                    circulationCardFolio = newValue
                },
                label = { Text("CURP") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = insurancePolicy,
                onValueChange = { newValue ->
                    insurancePolicy = newValue
                },
                label = { Text("License Type") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = insuranceCompany,
                onValueChange = { newValue ->
                    insuranceCompany = newValue
                },
                label = { Text("License Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))


            // Dropdown for "Is Active"
            DropdownField(
                label = "Is Active",
                options = dropdownItems,
                selectedOption = truckDetail.active.toString(),
                onOptionSelected = { selectedOption ->
                    viewModel.toggleTruckStatus(truck.id, selectedOption.toBoolean())
                },
                // modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Prepare updated driver object
                    val updatedDriver = truck.copy(
                        brand = brand,
                        model = model,
                        modelYear = modelYear,
                        licensePlates = licensePlates,
                        color = color,
                        circulationCardFolio = circulationCardFolio,
                        insurancePolicy = insurancePolicy,
                        insuranceCompany = insuranceCompany,
                        active = isActive// Include isActive in the updated driver
                    )
                    viewModel.updateTruck(truck.id, updatedDriver)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Changes")
            }
        } ?: run {
            Text("Loading driver details...")
        }
    }
}





