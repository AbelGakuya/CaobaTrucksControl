package com.example.caobatruckscontrol.presentation.composables.admin

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.caobatruckscontrol.common.Result
import com.example.caobatruckscontrol.data.model.Box
import com.example.caobatruckscontrol.presentation.composables.admin.components.DropdownField
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.BoxDetailViewModel

@Composable
fun BoxDetailScreen(
    boxId: String?,
    navController: NavController,
    viewModel: BoxDetailViewModel = hiltViewModel()
) {
    val boxDetail by viewModel.boxDetail.collectAsState()
    val toggleStatusResult by viewModel.toggleStatusResult.collectAsState()
    val updateBoxResult by viewModel.updateBoxResult.collectAsState()

    // Fetch box details initially
    LaunchedEffect(boxId) {
        viewModel.fetchBoxDetail(boxId!!)
    }

    // Navigate back if update is successful
    LaunchedEffect(updateBoxResult) {
        if (updateBoxResult is Result.Success) {
            navController.popBackStack()
        }
    }

    // Handle toggle status result
    LaunchedEffect(toggleStatusResult) {
        if (toggleStatusResult is Result.Success) {
            viewModel.fetchBoxDetail(boxId!!) // Refresh box detail after status change
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding()
    ) {
        item {
            // Box details section
            BoxDetailsSection(boxDetail, viewModel)
        }

        // Divider
        item {
            Divider(modifier = Modifier.padding(vertical = 16.dp))
        }
    }
}


@Composable
private fun BoxDetailsSection(boxDetail: Box?, viewModel: BoxDetailViewModel) {
    var mark by remember { mutableStateOf(boxDetail?.mark ?: "") }
    var template by remember { mutableStateOf(boxDetail?.template ?: "") }
    var modelYear by remember { mutableStateOf(boxDetail?.modelYear ?: "") }
    var licensePlate by remember { mutableStateOf(boxDetail?.licensePlate ?: "") }
    var color by remember { mutableStateOf(boxDetail?.color ?: "") }
    var isActive by remember { mutableStateOf(boxDetail?.isActive ?: false) }
    var circulationCardFolio by remember { mutableStateOf(boxDetail?.circulationCardFolio ?: "") }
    var insurancePolicy by remember { mutableStateOf(boxDetail?.insurancePolicy ?: "") }
    var insuranceCompany by remember { mutableStateOf(boxDetail?.insuranceCompany ?: "") }
    var imageUrl by remember { mutableStateOf(boxDetail?.imageUrl ?: "") }
    var insuranceImageUrl by remember { mutableStateOf(boxDetail?.insuranceImageUrl ?: "") }

    // Update local state when boxDetail changes
    LaunchedEffect(boxDetail) {
        mark = boxDetail?.mark ?: ""
        template = boxDetail?.template ?: ""
        modelYear = boxDetail?.modelYear ?: ""
        licensePlate = boxDetail?.licensePlate ?: ""
        color = boxDetail?.color ?: ""
        isActive = boxDetail?.isActive ?: false
        circulationCardFolio = boxDetail?.circulationCardFolio ?: ""
        insurancePolicy = boxDetail?.insurancePolicy ?: ""
        insuranceCompany = boxDetail?.insuranceCompany ?: ""
        imageUrl = boxDetail?.imageUrl ?: ""
        insuranceImageUrl = boxDetail?.insuranceImageUrl ?: ""
    }

    val dropdownItems = listOf("True", "False")

    Column {
        Text("Box Details", style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))

        boxDetail?.let { box ->
            OutlinedTextField(
                value = mark,
                onValueChange = { newValue ->
                    mark = newValue
                },
                label = { Text("Mark") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = template,
                onValueChange = { newValue ->
                    template = newValue
                },
                label = { Text("Template") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = modelYear,
                onValueChange = { newValue ->
                    modelYear = newValue
                },
                label = { Text("Model Year") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = licensePlate,
                onValueChange = { newValue ->
                    licensePlate = newValue
                },
                label = { Text("License Plate") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = color,
                onValueChange = { newValue ->
                    color = newValue
                },
                label = { Text("Color") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = circulationCardFolio,
                onValueChange = { newValue ->
                    circulationCardFolio = newValue
                },
                label = { Text("Circulation Card Folio") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = insurancePolicy,
                onValueChange = { newValue ->
                    insurancePolicy = newValue
                },
                label = { Text("Insurance Policy") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = insuranceCompany,
                onValueChange = { newValue ->
                    insuranceCompany = newValue
                },
                label = { Text("Insurance Company") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = imageUrl,
                onValueChange = { newValue ->
                    imageUrl = newValue
                },
                label = { Text("Image URL") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = insuranceImageUrl,
                onValueChange = { newValue ->
                    insuranceImageUrl = newValue
                },
                label = { Text("Insurance Image URL") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Dropdown for "Is Active"
            DropdownField(
                label = "Is Active",
                options = dropdownItems,
                selectedOption = box.isActive.toString(),
                onOptionSelected = { selectedOption ->
                    viewModel.toggleBoxStatus(box.id, selectedOption.toBoolean())
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Prepare updated box object
                    val updatedBox = box.copy(
                        mark = mark,
                        template = template,
                        modelYear = modelYear,
                        licensePlate = licensePlate,
                        color = color,
                        isActive = isActive,
                        circulationCardFolio = circulationCardFolio,
                        insurancePolicy = insurancePolicy,
                        insuranceCompany = insuranceCompany,
                        imageUrl = imageUrl,
                        insuranceImageUrl = insuranceImageUrl
                    )
                    viewModel.updateBox(box.id, updatedBox)
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Save Changes")
            }
        } ?: run {
            Text("Loading box details...")
        }
    }
}
