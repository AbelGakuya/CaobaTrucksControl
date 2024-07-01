package com.example.caobatruckscontrol.presentation.composables.admin

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.caobatruckscontrol.data.model.Address
import com.example.caobatruckscontrol.data.model.Driver
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.DriverViewModel
import java.util.*
import com.example.caobatruckscontrol.common.Result
import com.example.caobatruckscontrol.presentation.composables.sign_in.SignInState
import com.maxkeppeker.sheets.core.models.base.rememberSheetState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("UnrememberedMutableState")
@Composable
fun AddDriverScreen(
    viewModel: DriverViewModel = hiltViewModel()
) {
    val firstName = remember { mutableStateOf(TextFieldValue()) }
    val lastName = remember { mutableStateOf(TextFieldValue()) }
    //val dateOfBirth = remember { mutableStateOf(TextFieldValue()) }
    val identificationType = remember { mutableStateOf(TextFieldValue()) }
    val identificationNumber = remember { mutableStateOf(TextFieldValue()) }
    val curp = remember { mutableStateOf(TextFieldValue()) }
    val proofOfAddressStreet = remember { mutableStateOf(TextFieldValue()) }
    val proofOfAddressNumber = remember { mutableStateOf(TextFieldValue()) }
    val proofOfAddressInteriorNumber = remember { mutableStateOf(TextFieldValue()) }
    val proofOfAddressNeighborhood = remember { mutableStateOf(TextFieldValue()) }
    val proofOfAddressZipCode = remember { mutableStateOf(TextFieldValue()) }
    val proofOfAddressCity = remember { mutableStateOf(TextFieldValue()) }
    val proofOfAddressState = remember { mutableStateOf(TextFieldValue()) }
    val licenseType = remember { mutableStateOf(TextFieldValue()) }
    val licenseNumber = remember { mutableStateOf(TextFieldValue()) }
    //val licenseStartDate = remember { mutableStateOf(TextFieldValue()) }
    var licenseStartDate by remember { mutableStateOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) }
   //val licenseStartDate: MutableState<String>
    //val licenseExpiryDate = remember { mutableStateOf(TextFieldValue()) }
    val phoneNumber = remember { mutableStateOf(TextFieldValue()) }
    val email = remember { mutableStateOf(TextFieldValue()) }

    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val calendarState = rememberSheetState()

    CalendarDialog(state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date {}
    )

    var selectedDateTime by remember { mutableStateOf(LocalDateTime.now()) }

    var dateOfBirth by remember { mutableStateOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) }
    var licenseExpiryDate by remember { mutableStateOf(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))) }



    // State for feedback message
    val context = LocalContext.current
    val showMessage = remember { mutableStateOf(false) }
    val messageText = remember { mutableStateOf("") }

    val proofOfAddressPhoto = mutableStateOf<String?>(null)
    val medicalCertificatePhoto = mutableStateOf<String?>(null)
    val scope = rememberCoroutineScope()

    val proofOfAddressLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            proofOfAddressPhoto.value = uri.toString()
        } else {
            Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    val medicalCertificateLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            medicalCertificatePhoto.value = uri.toString()
        } else {
            Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }




    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding()

    ) {
        Text(
            text = "Add New Driver",
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)

        ) {
            item {
                OutlinedTextField(
                    value = firstName.value,
                    onValueChange = { firstName.value = it },
                    label = { Text("First Name") }
                )
            }
            item {
                OutlinedTextField(
                    value = lastName.value,
                    onValueChange = { lastName.value = it },
                    label = { Text("Last Name") }
                )
            }
            item {
                LabeledDatePicker(
                    label = "Select Date of Birth",
                    dateText = dateOfBirth,
                    initialDate = LocalDateTime.now(),
                    onDateSelected = { selectedDateTime ->
                        dateOfBirth = selectedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) // Update dateText on selection
                    }
                )
            }
            item {
                OutlinedTextField(
                    value = identificationType.value,
                    onValueChange = { identificationType.value = it },
                    label = { Text("Identification Type (INE/Passport)") }
                )
            }
            item {
                OutlinedTextField(
                    value = identificationNumber.value,
                    onValueChange = { identificationNumber.value = it },
                    label = { Text("Identification Number") }
                )
            }
            item {
                OutlinedTextField(
                    value = curp.value,
                    onValueChange = { curp.value = it },
                    label = { Text("CURP") }
                )
            }
            item {
                Text(
                    text = "Proof of Address",
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,

                    )
                )
                OutlinedTextField(
                    value = proofOfAddressStreet.value,
                    onValueChange = { proofOfAddressStreet.value = it },
                    label = { Text("Street") }
                )
                OutlinedTextField(
                    value = proofOfAddressNumber.value,
                    onValueChange = { proofOfAddressNumber.value = it },
                    label = { Text("Number") }
                )
                OutlinedTextField(
                    value = proofOfAddressInteriorNumber.value,
                    onValueChange = { proofOfAddressInteriorNumber.value = it },
                    label = { Text("Interior Number") }
                )
                OutlinedTextField(
                    value = proofOfAddressNeighborhood.value,
                    onValueChange = { proofOfAddressNeighborhood.value = it },
                    label = { Text("Neighborhood") }
                )
                OutlinedTextField(
                    value = proofOfAddressZipCode.value,
                    onValueChange = { proofOfAddressZipCode.value = it },
                    label = { Text("Zip Code") }
                )
                OutlinedTextField(
                    value = proofOfAddressCity.value,
                    onValueChange = { proofOfAddressCity.value = it },
                    label = { Text("City") }
                )
                OutlinedTextField(
                    value = proofOfAddressState.value,
                    onValueChange = { proofOfAddressState.value = it },
                    label = { Text("State") }
                )

                Button(onClick = { proofOfAddressLauncher.launch("image/*") }) {
                    Text("Add Photo of Proof of Address")
                }

                proofOfAddressPhoto.value?.let { uri ->
                    Spacer(modifier = Modifier.height(16.dp))
                    GlideImage(
                        imageModel = uri,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                OutlinedTextField(
                    value = licenseType.value,
                    onValueChange = { licenseType.value = it },
                    label = { Text("License Type") }
                )
            }
            item {
                OutlinedTextField(
                    value = licenseNumber.value,
                    onValueChange = { licenseNumber.value = it },
                    label = { Text("License Number") }
                )
            }
            item {
                LabeledDatePicker(
                    label = "Select License Start Date",
                    dateText = licenseStartDate,
                    initialDate = LocalDateTime.now(),
                    onDateSelected = { selectedDateTime ->
                        licenseStartDate = selectedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) // Update dateText on selection
                    }
                )
            }


            item {
                LabeledDatePicker(
                    label = "Select License Expiry Date",
                    dateText = licenseExpiryDate,
                    initialDate = LocalDateTime.now(),
                    onDateSelected = { selectedDateTime ->
                        licenseExpiryDate = selectedDateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) // Update dateText on selection
                    }
                )
            }
            item {
                OutlinedTextField(
                    value = phoneNumber.value,
                    onValueChange = { phoneNumber.value = it },
                    label = { Text("Phone Number") }
                )
            }
            item {
                OutlinedTextField(
                    value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Email") }
                )
            }

            item {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Medical Certificate",
                    modifier = Modifier.padding(vertical = 8.dp),
                    style = TextStyle(
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                )

                Button(onClick = { medicalCertificateLauncher.launch("image/*") }) {
                    Text("Add Photo of Medical Certificate")
                }

                medicalCertificatePhoto.value?.let { uri ->
                    Spacer(modifier = Modifier.height(16.dp))
                    GlideImage(
                        imageModel = uri,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        Button(
            onClick = {

                coroutineScope.launch {

                    isLoading = true

                    var newDriver = Driver(
                        firstName = firstName.value.text,
                        lastName = lastName.value.text,
                        dateOfBirth = dateOfBirth,
                        identificationType = identificationType.value.text,
                        identificationNumber = identificationNumber.value.text,
                        curp = curp.value.text,
                        proofOfAddress = Address(
                            street = proofOfAddressStreet.value.text,
                            number = proofOfAddressNumber.value.text,
                            interiorNumber = proofOfAddressInteriorNumber.value.text,
                            neighborhood = proofOfAddressNeighborhood.value.text,
                            zipCode = proofOfAddressZipCode.value.text,
                            city = proofOfAddressCity.value.text,
                            state = proofOfAddressState.value.text,
                            imageUrl = null // Initialize with null
                        ),
                        licenseType = licenseType.value.text,
                        licenseNumber = licenseNumber.value.text,
                        licenseStartDate = licenseStartDate,
                        licenseExpiryDate = licenseExpiryDate,
                        phoneNumber = phoneNumber.value.text,
                        email = email.value.text,
                        medicalCertificateUrl = null // Initialize with null
                    )

                    proofOfAddressPhoto.value?.let { addressUriString ->
                        val addressUri = Uri.parse(addressUriString)
                        viewModel.uploadImageToFirebase(
                            addressUri,
                            "proof_of_address"
                        ) { addressImageUrl ->
                            if (addressImageUrl != null) {
                                newDriver = newDriver.copy(
                                    proofOfAddress = newDriver.proofOfAddress.copy(imageUrl = addressImageUrl)
                                )
                                medicalCertificatePhoto.value?.let { medicalUriString ->
                                    val medicalUri = Uri.parse(medicalUriString)
                                    viewModel.uploadImageToFirebase(
                                        medicalUri,
                                        "medical_certificates"
                                    ) { medicalImageUrl ->
                                        if (medicalImageUrl != null) {
                                            newDriver =
                                                newDriver.copy(medicalCertificateUrl = medicalImageUrl)
                                            viewModel.addDriver(newDriver) { result ->
                                                // Handle result from adding driver to Firestore
                                                when (result) {
                                                    is Result.Success -> {
                                                        showMessage.value = true
                                                        messageText.value =
                                                            "Driver added successfully"
                                                        clearFields(
                                                            firstName,
                                                            lastName,
                                                            identificationType,
                                                            identificationNumber,
                                                            curp,
                                                            proofOfAddressStreet,
                                                            proofOfAddressNumber,
                                                            proofOfAddressInteriorNumber,
                                                            proofOfAddressNeighborhood,
                                                            proofOfAddressZipCode,
                                                            proofOfAddressCity,
                                                            proofOfAddressState,
                                                            licenseType,
                                                            licenseNumber,
                                                            phoneNumber,
                                                            email
                                                        )
                                                        showToast(context, messageText.value)
                                                    }

                                                    is Result.Error -> {
                                                        showMessage.value = true
                                                        messageText.value =
                                                            "Failed to add driver: ${result.message}"
                                                        showToast(context, messageText.value)
                                                    }

                                                    else -> {}
                                                }
                                            }
                                        } else {
                                            showMessage.value = true
                                            messageText.value =
                                                "Failed to upload medical certificate"
                                            showToast(context, messageText.value)
                                        }
                                    }
                                } ?: run {
                                    showMessage.value = true
                                    messageText.value = "Medical certificate is required"
                                    showToast(context, messageText.value)
                                }
                            } else {
                                showMessage.value = true
                                messageText.value = "Failed to upload proof of address"
                                showToast(context, messageText.value)
                            }
                        }
                    } ?: run {
                        showMessage.value = true
                        messageText.value = "Proof of address photo is required"
                        showToast(context, messageText.value)
                    }

                    isLoading = false
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {

            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(Icons.Default.Save, contentDescription = "Save Driver")
                Text(
                    text = "Save Driver",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }

        // Set showMessage to display toast
        if (showMessage.value) {
            showToast(context, messageText.value)
            showMessage.value = false // Reset showMessage
        }
    }
}

// Function to show Toast
private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}


// Function to check if the character at the specified index is a delimiter
fun isDelimiter(text: String, index: Int, delimiter: Char): Boolean {
    return text.getOrNull(index) == delimiter
}

@RequiresApi(Build.VERSION_CODES.O)
private fun isValidDate(dateText: String): Boolean {
    return try {
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        LocalDate.parse(dateText, formatter)
        true // Parsing succeeded, date is valid
    } catch (e: DateTimeParseException) {
        false // Parsing failed, date is invalid
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LabeledDatePicker(
    label: String,
    dateText: String,
    initialDate: LocalDateTime,
    onDateSelected: (LocalDateTime) -> Unit
) {
    var selectedDateTime by remember { mutableStateOf(initialDate) }
    val isDateInvalid = !isValidDate(dateText)
    val calendarState = rememberSheetState()

    // Update selectedDateTime and dateText when initialDate changes
    LaunchedEffect(initialDate) {
        selectedDateTime = initialDate
    }

    Column {
        OutlinedTextField(
            value = dateText,
            onValueChange = { newDateText ->
                // Update dateText, but defer validation until blur or selection
                onDateSelected(selectedDateTime) // Notify callback with selected date
            },
            label = { Text(label) },
            isError = isDateInvalid,
            readOnly = true,
            modifier = Modifier,
            trailingIcon = {
                Icon(Icons.Default.CalendarToday, contentDescription = "Select date", modifier = Modifier.clickable {
                    calendarState.show()
                })
            }
        )

        if (isDateInvalid) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "!",
                    color = Color.Red,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(
                    text = "The date you entered is invalid!",
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )
            }
        }
    }

    // Calendar dialog for date selection
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date {
            selectedDateTime = it.atTime(selectedDateTime.toLocalTime()) // Set selected date and time
            onDateSelected(selectedDateTime) // Notify callback with selected date
            calendarState.hide() // Hide calendar after selection
        }
    )
}






// Function to clear all input fields
private fun clearFields(
    firstName: MutableState<TextFieldValue>,
    lastName: MutableState<TextFieldValue>,
    identificationType: MutableState<TextFieldValue>,
    identificationNumber: MutableState<TextFieldValue>,
    curp: MutableState<TextFieldValue>,
    proofOfAddressStreet: MutableState<TextFieldValue>,
    proofOfAddressNumber: MutableState<TextFieldValue>,
    proofOfAddressInteriorNumber: MutableState<TextFieldValue>,
    proofOfAddressNeighborhood: MutableState<TextFieldValue>,
    proofOfAddressZipCode: MutableState<TextFieldValue>,
    proofOfAddressCity: MutableState<TextFieldValue>,
    proofOfAddressState: MutableState<TextFieldValue>,
    licenseType: MutableState<TextFieldValue>,
    licenseNumber: MutableState<TextFieldValue>,
    phoneNumber: MutableState<TextFieldValue>,
    email: MutableState<TextFieldValue>
) {
    firstName.value = TextFieldValue("")
    lastName.value = TextFieldValue("")
    identificationType.value = TextFieldValue("")
    identificationNumber.value = TextFieldValue("")
    curp.value = TextFieldValue("")
    proofOfAddressStreet.value = TextFieldValue("")
    proofOfAddressNumber.value = TextFieldValue("")
    proofOfAddressInteriorNumber.value = TextFieldValue("")
    proofOfAddressNeighborhood.value = TextFieldValue("")
    proofOfAddressZipCode.value = TextFieldValue("")
    proofOfAddressCity.value = TextFieldValue("")
    proofOfAddressState.value = TextFieldValue("")
    licenseType.value = TextFieldValue("")
    licenseNumber.value = TextFieldValue("")
    phoneNumber.value = TextFieldValue("")
    email.value = TextFieldValue("")
}
