package com.example.caobatruckscontrol.presentation.composables.admin

import android.annotation.SuppressLint
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.BoxViewModel
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import com.example.caobatruckscontrol.data.model.Box
import com.example.caobatruckscontrol.common.Result
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnrememberedMutableState")
@Composable
fun AddBoxScreen(navController: NavController, viewModel: BoxViewModel = hiltViewModel()) {
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    // State to hold user inputs
    var mark by remember { mutableStateOf("") }
    var template by remember { mutableStateOf("") }
    var modelYear by remember { mutableStateOf("") }
    var licensePlate by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var circulationCardFolio by remember { mutableStateOf("") }
    var insurancePolicy by remember { mutableStateOf("") }
    var insuranceCompany by remember { mutableStateOf("") }

    // Loading state
    var isLoading by remember { mutableStateOf(false) }

    // State for photo URLs
    val licensePlatePhotoUrl = mutableStateOf<String?>(null)
    val boxPhotoUrl = mutableStateOf<String?>(null)
    val circulationCardFolioUrl = mutableStateOf<String?>(null)
    val insurancePolicyUrl = mutableStateOf<String?>(null)

    // Activity result launchers for photo selection
    val licensePlateLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            licensePlatePhotoUrl.value = uri.toString()
        } else {
            Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    val boxPhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            boxPhotoUrl.value = uri.toString()
        } else {
            Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    val circulationCardFolioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            circulationCardFolioUrl.value = uri.toString()
        } else {
            Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    val insurancePolicyLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            insurancePolicyUrl.value = uri.toString()
        } else {
            Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to handle saving box data to Firestore
    val saveBoxData = {
        coroutineScope.launch {
            isLoading = true
            try {
                // Upload all images concurrently using async
                val licensePlateImageUrlDeferred = async {
                    licensePlatePhotoUrl.value?.let { uriString ->
                        viewModel.uploadImage(Uri.parse(uriString), "license_plate.jpg")
                    }
                }

                val boxImageUrlDeferred = async {
                    boxPhotoUrl.value?.let { uriString ->
                        viewModel.uploadImage(Uri.parse(uriString), "box_photo.jpg")
                    }
                }

                val circulationCardFolioImageUrlDeferred = async {
                    circulationCardFolioUrl.value?.let { uriString ->
                        viewModel.uploadImage(Uri.parse(uriString), "circulation_card_folio.jpg")
                    }
                }

                val insurancePolicyImageUrlDeferred = async {
                    insurancePolicyUrl.value?.let { uriString ->
                        viewModel.uploadImage(Uri.parse(uriString), "insurance_policy.jpg")
                    }
                }

                // Await for all image uploads to complete
                val licensePlateImageUrl = licensePlateImageUrlDeferred.await()
                val boxImageUrl = boxImageUrlDeferred.await()
                val circulationCardFolioImageUrl = circulationCardFolioImageUrlDeferred.await()
                val insurancePolicyImageUrl = insurancePolicyImageUrlDeferred.await()

                // Create Box object with uploaded image URLs
                val newBox = Box(
                    mark = mark,
                    template = template,
                    modelYear = modelYear,
                    licensePlate = licensePlate,
                    color = color,
                    circulationCardFolio = circulationCardFolio,
                    insurancePolicy = insurancePolicy,
                    insuranceCompany = insuranceCompany,
                    licensePlatePhotoUrl = licensePlateImageUrl,
                    boxPhotoUrl = boxImageUrl,
                    circulationCardFolioPhotoUrl = circulationCardFolioImageUrl,
                    insurancePolicyPhotoUrl = insurancePolicyImageUrl
                )

                // Add the box data to Firestore
                viewModel.addBox(newBox).let { result ->
                    when (result) {
                        is Result.Success -> {
                            Toast.makeText(context, "Box data saved successfully!", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        is Result.Error -> {
                            Toast.makeText(context, "Error saving box data: ${result.message}", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            Toast.makeText(context, "Unexpected result: $result", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error uploading images: ${e.message}", Toast.LENGTH_SHORT).show()
            }

            isLoading = false
        }
    }

    // UI layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Add New Box",
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
                    value = mark,
                    onValueChange = { mark = it },
                    label = { Text("Box Mark (Required)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = template,
                    onValueChange = { template = it },
                    label = { Text("Box Template (Required)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = modelYear,
                    onValueChange = { modelYear = it },
                    label = { Text("Box Model Year (Required)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = licensePlate,
                    onValueChange = { licensePlate = it },
                    label = { Text("License Plate of the Box (Mandatory)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Box Color (Required)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = circulationCardFolio,
                    onValueChange = { circulationCardFolio = it },
                    label = { Text("Circulation Card Folio (Mandatory)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = insurancePolicy,
                    onValueChange = { insurancePolicy = it },
                    label = { Text("Insurance Policy of the Box (Mandatory)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            item {
                OutlinedTextField(
                    value = insuranceCompany,
                    onValueChange = { insuranceCompany = it },
                    label = { Text("Insurance Company (Mandatory)") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = Color.Blue,
                        unfocusedBorderColor = Color.Gray
                    )
                )
            }

            item {
                Button(onClick = { licensePlateLauncher.launch("image/*") }) {
                    Text("Add Photo of License Plate")
                }

                licensePlatePhotoUrl.value?.let { uri ->
                    Spacer(modifier = Modifier.height(16.dp))
                    GlideImage(
                        imageModel = uri,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            item {
                Button(onClick = { boxPhotoLauncher.launch("image/*") }) {
                    Text("Add Photo of the Box")
                }

                boxPhotoUrl.value?.let { uri ->
                    Spacer(modifier = Modifier.height(16.dp))
                    GlideImage(
                        imageModel = uri,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            item {
                Button(onClick = { circulationCardFolioLauncher.launch("image/*") }) {
                    Text("Add Photo of Circulation Card Folio")
                }

                circulationCardFolioUrl.value?.let { uri ->
                    Spacer(modifier = Modifier.height(16.dp))
                    GlideImage(
                        imageModel = uri,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }

            item {
                Button(onClick = { insurancePolicyLauncher.launch("image/*") }) {
                    Text("Add Photo of Insurance Policy")
                }

                insurancePolicyUrl.value?.let { uri ->
                    Spacer(modifier = Modifier.height(16.dp))
                    GlideImage(
                        imageModel = uri,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        }

        Button(
            onClick = {
                saveBoxData()
                      },
            modifier = Modifier
                .align(Alignment.End)
                .padding(top = 16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(Icons.Default.Save, contentDescription = "Save Box")
                Text(
                    text = "Save Box",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

