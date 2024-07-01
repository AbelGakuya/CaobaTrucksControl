package com.example.caobatruckscontrol.presentation.composables.driver

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.caobatruckscontrol.presentation.composables.driver.viewmodel.DriverExpenseViewModel
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment


import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.*

import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import coil.compose.rememberImagePainter
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DriverExpenseScreen(
    tripId: String?,
    navController: NavHostController,
    viewModel: DriverExpenseViewModel = hiltViewModel()
) {
    var isTruckExpense by remember { mutableStateOf(true) }
    var category by remember { mutableStateOf("") }
    var expenseType by remember { mutableStateOf("") }
    var invoiceFolio by remember { mutableStateOf("") }
    var issuingCompany by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var odometer by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var expenseCheckedBy by remember { mutableStateOf("") }

    val context = LocalContext.current
    val categories = listOf(
        "Diesel", "Mechanical", "Spare Parts", "Tires", "Purchase of Equipment",
        "Procedures", "Insurance", "Commissions", "Taxes", "Pension", "Gratuities"
    )
    val expenseTypes = listOf("Invoice", "Note", "Photo")

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        photoUri = uri
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Expense") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.saveExpense(
                    tripId!!,
                    isTruckExpense = isTruckExpense,
                    category = category,
                    expenseType = expenseCheckedBy,
                    invoiceFolio = invoiceFolio.ifEmpty { null },
                    issuingCompany = issuingCompany,
                    amount = amount.toDoubleOrNull() ?: 0.0,
                    odometer = odometer.toDoubleOrNull(),
                    description = description.ifEmpty { null },
                    photoUri = photoUri
                )
                navController.popBackStack() // Navigate back after saving the expense
            }) {
                Icon(Icons.Filled.Check, contentDescription = "Save Expense")
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Is this an expense for the Truck or Box?")
                Row {
                    RadioButton(selected = isTruckExpense, onClick = { isTruckExpense = true })
                    Text("Truck", modifier = Modifier.padding(start = 8.dp))
                    Spacer(modifier = Modifier.width(16.dp))
                    RadioButton(selected = !isTruckExpense, onClick = { isTruckExpense = false })
                    Text("Box", modifier = Modifier.padding(start = 8.dp))
                }

                // Category Dropdown
                var categoryExpanded by remember { mutableStateOf(false) }
                Box {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category of Expenditure") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true,
                        trailingIcon = {
                            Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown", Modifier.clickable { categoryExpanded = true })
                        }
                    )
                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(onClick = {
                                category = cat
                                categoryExpanded = false
                            }) {
                                Text(cat)
                            }
                        }
                    }
                }

                // Expense Checked By Dropdown
                var expenseCheckedByExpanded by remember { mutableStateOf(false) }
                if (category.isNotEmpty()) {
                    Box {
                        OutlinedTextField(
                            value = expenseCheckedBy,
                            onValueChange = { expenseCheckedBy = it },
                            label = { Text("Expense Checked By") },
                            modifier = Modifier.fillMaxWidth(),
                            readOnly = true,
                            trailingIcon = {
                                Icon(Icons.Filled.ArrowDropDown, contentDescription = "Dropdown", Modifier.clickable { expenseCheckedByExpanded = true })
                            }
                        )
                        DropdownMenu(
                            expanded = expenseCheckedByExpanded,
                            onDismissRequest = { expenseCheckedByExpanded = false }
                        ) {
                            expenseTypes.forEach { type ->
                                DropdownMenuItem(onClick = {
                                    expenseCheckedBy = type
                                    expenseCheckedByExpanded = false
                                }) {
                                    Text(type)
                                }
                            }
                        }
                    }
                }

                // Conditional Inputs
                when (expenseCheckedBy) {
                    "Invoice" -> {
                        OutlinedTextField(
                            value = invoiceFolio,
                            onValueChange = { invoiceFolio = it },
                            label = { Text("Invoice Folio") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = issuingCompany,
                            onValueChange = { issuingCompany = it },
                            label = { Text("Issuing Company") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Amount") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        if (category == "Diesel") {
                            OutlinedTextField(
                                value = odometer,
                                onValueChange = { odometer = it },
                                label = { Text("Odometer") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                            )
                        }
                        Button(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upload Invoice Photo")
                        }
                        photoUri?.let {
                            Image(
                                painter = rememberImagePainter(it),
                                contentDescription = "Invoice Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    "Note" -> {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Amount") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = issuingCompany,
                            onValueChange = { issuingCompany = it },
                            label = { Text("Issuing Company") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (category == "Diesel") {
                            OutlinedTextField(
                                value = odometer,
                                onValueChange = { odometer = it },
                                label = { Text("Odometer") },
                                modifier = Modifier.fillMaxWidth(),
                                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                            )
                        } else {
                            OutlinedTextField(
                                value = description,
                                onValueChange = { description = it },
                                label = { Text("Description") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        Button(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upload Note Photo")
                        }
                        photoUri?.let {
                            Image(
                                painter = rememberImagePainter(it),
                                contentDescription = "Note Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                    "Photo" -> {
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            label = { Text("Amount") },
                            modifier = Modifier.fillMaxWidth(),
                            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                        )
                        OutlinedTextField(
                            value = description,
                            onValueChange = { description = it },
                            label = { Text("Description") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        Button(
                            onClick = { launcher.launch("image/*") },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Upload Expense Photo")
                        }
                        photoUri?.let {
                            Image(
                                painter = rememberImagePainter(it),
                                contentDescription = "Expense Photo",
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
    )
}

