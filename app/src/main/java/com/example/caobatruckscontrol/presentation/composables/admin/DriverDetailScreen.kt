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
import com.example.caobatruckscontrol.presentation.NavItem
import com.example.caobatruckscontrol.presentation.composables.admin.components.DropdownField
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.DriverDetailViewModel
import kotlinx.coroutines.flow.collect

@Composable
fun DriverDetailScreen(
    driverId: String?,
    navController: NavController,
    viewModel: DriverDetailViewModel = hiltViewModel()
) {
    val driverDetail by viewModel.driverDetail.collectAsState()
    val toggleStatusResult by viewModel.toggleStatusResult.collectAsState()
    val updateDriverResult by viewModel.updateDriverResult.collectAsState()

    // Fetch driver details initially
    LaunchedEffect(driverId) {
        viewModel.fetchDriverDetail(driverId!!)
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
            viewModel.fetchDriverDetail(driverId!!) // Refresh driver detail after status change
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
            DriverDetailsSection(driverDetail, viewModel)
        }

        // Divider
        item {
            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}

@Composable
private fun DriverDetailsSection(driverDetail: Driver?, viewModel: DriverDetailViewModel) {
    var firstName by remember { mutableStateOf(driverDetail?.firstName ?: "") }
    var lastName by remember { mutableStateOf(driverDetail?.lastName ?: "") }
    var dateOfBirth by remember { mutableStateOf(driverDetail?.dateOfBirth ?: "") }
    var identificationType by remember { mutableStateOf(driverDetail?.identificationType ?: "") }
    var identificationNumber by remember { mutableStateOf(driverDetail?.identificationNumber ?: "") }
    var curp by remember { mutableStateOf(driverDetail?.curp ?: "") }
    var licenseType by remember { mutableStateOf(driverDetail?.licenseType ?: "") }
    var licenseNumber by remember { mutableStateOf(driverDetail?.licenseNumber ?: "") }
    var licenseStartDate by remember { mutableStateOf(driverDetail?.licenseStartDate ?: "") }
    var licenseExpiryDate by remember { mutableStateOf(driverDetail?.licenseExpiryDate ?: "") }
    var phoneNumber by remember { mutableStateOf(driverDetail?.phoneNumber ?: "") }
    var email by remember { mutableStateOf(driverDetail?.email ?: "") }
    var isActive by remember { mutableStateOf(driverDetail?.active ?: false) }

    // Update local state when driverDetail changes
    LaunchedEffect(driverDetail) {
        firstName = driverDetail?.firstName ?: ""
        lastName = driverDetail?.lastName ?: ""
        dateOfBirth = driverDetail?.dateOfBirth ?: ""
        identificationType = driverDetail?.identificationType ?: ""
        identificationNumber = driverDetail?.identificationNumber ?: ""
        curp = driverDetail?.curp ?: ""
        licenseType = driverDetail?.licenseType ?: ""
        licenseNumber = driverDetail?.licenseNumber ?: ""
        licenseStartDate = driverDetail?.licenseStartDate ?: ""
        licenseExpiryDate = driverDetail?.licenseExpiryDate ?: ""
        phoneNumber = driverDetail?.phoneNumber ?: ""
        email = driverDetail?.email ?: ""
        isActive = driverDetail?.active ?: false
    }

    val dropdownItems = listOf("True", "False")

    Column {
        Text("Driver Details", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))

        driverDetail?.let { driver ->
            OutlinedTextField(
                value = firstName,
                onValueChange = { newValue ->
                    firstName = newValue
                },
                label = { Text("First Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = lastName,
                onValueChange = { newValue ->
                    lastName = newValue
                },
                label = { Text("Last Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = dateOfBirth,
                onValueChange = { newValue ->
                    dateOfBirth = newValue
                },
                label = { Text("Date of Birth") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = identificationType,
                onValueChange = { newValue ->
                    identificationType = newValue
                },
                label = { Text("Identification Type") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = identificationNumber,
                onValueChange = { newValue ->
                    identificationNumber = newValue
                },
                label = { Text("Identification Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = curp,
                onValueChange = { newValue ->
                    curp = newValue
                },
                label = { Text("CURP") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = licenseType,
                onValueChange = { newValue ->
                    licenseType = newValue
                },
                label = { Text("License Type") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = licenseNumber,
                onValueChange = { newValue ->
                    licenseNumber = newValue
                },
                label = { Text("License Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
//            OutlinedTextField(
//                value = licenseStartDate,
//                onValueChange = { newValue ->
//                    licenseStartDate = newValue
//                },
//                label = { Text("License Start Date") },
//                modifier = Modifier.fillMaxWidth()
//            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = licenseExpiryDate,
                onValueChange = { newValue ->
                    licenseExpiryDate = newValue
                },
                label = { Text("License Expiry Date") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { newValue ->
                    phoneNumber = newValue
                },
                label = { Text("Phone Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { newValue ->
                    email = newValue
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for "Is Active"
            DropdownField(
                label = "Is Active",
                options = dropdownItems,
                selectedOption = driverDetail.active.toString(),
                onOptionSelected = { selectedOption ->
                    viewModel.toggleDriverStatus(driver.id, selectedOption.toBoolean())
                },
               // modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Prepare updated driver object
                    val updatedDriver = driver.copy(
                        firstName = firstName,
                        lastName = lastName,
                        dateOfBirth = dateOfBirth,
                        identificationType = identificationType,
                        identificationNumber = identificationNumber,
                        curp = curp,
                        licenseType = licenseType,
                        licenseNumber = licenseNumber,
                        licenseStartDate = licenseStartDate,
                        licenseExpiryDate = licenseExpiryDate,
                        phoneNumber = phoneNumber,
                        email = email,
                        active = isActive// Include isActive in the updated driver
                    )
                    viewModel.updateDriver(driver.id, updatedDriver)
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





