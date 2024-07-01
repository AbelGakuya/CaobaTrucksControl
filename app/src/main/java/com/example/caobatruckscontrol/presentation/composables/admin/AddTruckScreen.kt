package com.example.caobatruckscontrol.presentation.composables.admin

import android.annotation.SuppressLint
import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.caobatruckscontrol.data.model.Truck
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.TruckViewModel
import androidx.compose.foundation.Image
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.caobatruckscontrol.common.Result
import com.skydoves.landscapist.glide.GlideImage
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


@SuppressLint("UnrememberedMutableState")
@Composable
fun AddTruckScreen(
    navController: NavController,
    viewModel: TruckViewModel = hiltViewModel()
) {
    var brand by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var modelYear by remember { mutableStateOf("") }
    var licensePlates by remember { mutableStateOf("") }
    var color by remember { mutableStateOf("") }
    var circulationCardFolio by remember { mutableStateOf("") }
    var insurancePolicy by remember { mutableStateOf("") }
    var insuranceCompany by remember { mutableStateOf("") }

    val addTruckError by viewModel.error.collectAsState()

    // Loading state
    var isLoading by remember { mutableStateOf(false) }

    // Coroutine scope for async operations
    val coroutineScope = rememberCoroutineScope()

    // State for feedback message
    val context = LocalContext.current
    val showMessage = remember { mutableStateOf(false) }
    val messageText = remember { mutableStateOf("") }


    val rearLicensePlatePhotoUrl = mutableStateOf<String?>(null)
    val circulationCardPhotoUrl = mutableStateOf<String?>(null)
    val insurancePolicyPhotoUrl = mutableStateOf<String?>(null)

    var truckPhotoUrl = mutableStateOf<String?>(null)

    val truckPhotoUrlLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            truckPhotoUrl.value = uri.toString()
        } else {
            Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    val frontLicensePlatePhotoUrl = mutableStateOf<String?>(null)

    val frontLicensePlateLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            frontLicensePlatePhotoUrl.value = uri.toString()
        } else {
            Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    val rearLicensePlateLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            rearLicensePlatePhotoUrl.value = uri.toString()
        } else {
            Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    val circulationCardPhotoLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            circulationCardPhotoUrl.value = uri.toString()
        } else {
            Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    val insurancePolicyPhotoUrlLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            insurancePolicyPhotoUrl.value = uri.toString()
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
        Text("Add Truck", style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(bottom = 16.dp))

        if (addTruckError != null) {
            Text(text = addTruckError!!, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)

        ) {

            item {
                OutlinedTextField(
                value = brand,
                onValueChange = { brand = it },
                label = { Text("Truck Brand") },
                modifier = Modifier.fillMaxWidth()
            ) }


            item {
                OutlinedTextField(
                    value = model,
                    onValueChange = { model = it },
                    label = { Text("Truck Model") },
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {
                OutlinedTextField(
                    value = modelYear,
                    onValueChange = { modelYear = it },
                    label = { Text("Truck Model Year") },
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {
                OutlinedTextField(
                    value = licensePlates,
                    onValueChange = { licensePlates = it },
                    label = { Text("Truck License Plates") },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {

                Button(onClick = { frontLicensePlateLauncher.launch("image/*") }) {
                    Text("Add Photo of Front License Photos")
                }

                frontLicensePlatePhotoUrl.value?.let { uri ->
                    Spacer(modifier = Modifier.height(16.dp))
                    GlideImage(
                        imageModel = uri,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Button(onClick = { rearLicensePlateLauncher.launch("image/*") }) {
                    Text("Add Photo of Rear License Photos")
                }

                rearLicensePlatePhotoUrl.value?.let { uri ->
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
                OutlinedTextField(
                    value = color,
                    onValueChange = { color = it },
                    label = { Text("Truck Color") },
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {
                Button(onClick = { truckPhotoUrlLauncher.launch("image/*") }) {
                    Text("Add a photo of the truck")
                }

                truckPhotoUrl.value?.let { uri ->
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
                OutlinedTextField(
                    value = circulationCardFolio,
                    onValueChange = { circulationCardFolio = it },
                    label = { Text("Truck Circulation Card Folio") },
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {
                Button(onClick = { circulationCardPhotoLauncher.launch("image/*") }) {
                    Text("Add Photo of the circulation card")
                }

                circulationCardPhotoUrl.value?.let { uri ->
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
                OutlinedTextField(
                    value = insurancePolicy,
                    onValueChange = { insurancePolicy = it },
                    label = { Text("Truck Insurance Policy") },
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {
                OutlinedTextField(
                    value = insuranceCompany,
                    onValueChange = { insuranceCompany = it },
                    label = { Text("Insurance Company") },
                    modifier = Modifier.fillMaxWidth()
                )
            }


            item {
                Button(onClick = { insurancePolicyPhotoUrlLauncher.launch("image/*") }) {
                    Text("Add Photo of insurance Policy Photos")
                }

                insurancePolicyPhotoUrl.value?.let { uri ->
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

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true

                    // Use async to upload all images concurrently
                    val frontLicensePlateImageUrlDeferred = async { frontLicensePlatePhotoUrl.value?.let { uriString ->
                        viewModel.uploadImage(Uri.parse(uriString), "front_license_plate.jpg")
                    } }

                    val rearLicensePlateImageUrlDeferred = async { rearLicensePlatePhotoUrl.value?.let { uriString ->
                        viewModel.uploadImage(Uri.parse(uriString), "rear_license_plate.jpg")
                    } }

                    val circulationCardImageUrlDeferred = async { circulationCardPhotoUrl.value?.let { uriString ->
                        viewModel.uploadImage(Uri.parse(uriString), "circulation_card.jpg")
                    } }

                    val insurancePolicyImageUrlDeferred = async { insurancePolicyPhotoUrl.value?.let { uriString ->
                        viewModel.uploadImage(Uri.parse(uriString), "insurance_policy.jpg")
                    } }

                    val truckImageUrlDeferred = async { truckPhotoUrl.value?.let { uriString ->
                        viewModel.uploadImage(Uri.parse(uriString), "truck_photo.jpg")
                    } }

                    // Await for all uploads to complete
                    val frontLicensePlateImageUrl = frontLicensePlateImageUrlDeferred.await()
                    val rearLicensePlateImageUrl = rearLicensePlateImageUrlDeferred.await()
                    val circulationCardImageUrl = circulationCardImageUrlDeferred.await()
                    val insurancePolicyImageUrl = insurancePolicyImageUrlDeferred.await()
                    val truckImageUrl = truckImageUrlDeferred.await()

                    // Construct truck object with uploaded image URLs
                    val truck = Truck(
                        brand = brand,
                        model = model,
                        modelYear = modelYear,
                        licensePlates = licensePlates,
                        color = color,
                        circulationCardFolio = circulationCardFolio,
                        insurancePolicy = insurancePolicy,
                        insuranceCompany = insuranceCompany,
                        frontLicensePlatePhotoUrl = frontLicensePlateImageUrl,
                        rearLicensePlatePhotoUrl = rearLicensePlateImageUrl,
                        circulationCardPhotoUrl = circulationCardImageUrl,
                        insurancePolicyPhotoUrl = insurancePolicyImageUrl,
                        truckPhotosUrl = truckImageUrl
                    )

                    // Add truck to Firestore
                    viewModel.addTruck(truck).let { result ->
                        when (result) {
                            is Result.Success -> {
                                showMessage.value = true
                                messageText.value = "Truck added successfully"
                                showToast(context, messageText.value)
                            }
                            is Result.Error -> {
                                showMessage.value = true
                                messageText.value = "Failed to add truck: ${result.message}"
                                showToast(context, messageText.value)
                            }

                            else -> {
                                //Toast.makeText(context, "Unexpected result: $result", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    isLoading = false
                    navController.popBackStack()
                }
            },
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(16.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(Icons.Default.Save, contentDescription = "Save Truck")
                Text(
                    text = "Save Truck",
                    color = Color.White,
                    fontSize = 16.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

// Function to show Toast
private fun showToast(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

@Composable
fun PhotoPicker(label: String, photoUri: Uri?, onPhotoSelected: (Uri) -> Unit) {
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let { onPhotoSelected(it) } }
    )

    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(text = label, style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(8.dp))
        if (photoUri != null) {
            // Display the selected photo (in a real app you might want to use an image loading library)
            Text(text = "Photo selected: $photoUri")
        } else {
            Button(onClick = { launcher.launch("image/*") }) {
                Text("Pick Photo")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TruckPhotoPicker(truckPhotos: List<Uri>, onPickPhotos: (List<Uri>) -> Unit) {
    val context = LocalContext.current
    val photoPickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetMultipleContents()) { uris: List<Uri> ->
        if (uris.isNotEmpty()) {
            onPickPhotos(uris)
        } else {
            Toast.makeText(context, "Photo selection cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    Column {
        Button(onClick = { photoPickerLauncher.launch("image/*") }) {
            Text("Add Photos of Truck")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp),
            modifier = Modifier.fillMaxWidth().height(400.dp)
        ) {
            items(truckPhotos.size) { index ->
                val uri = truckPhotos[index]
                Image(
                    painter = rememberImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .size(128.dp)
                        .padding(4.dp)
                )
            }
        }
    }
}


@Composable
fun PhotoItem(uri: Uri) {
    Image(
        painter = rememberImagePainter(uri),
        contentDescription = null,
        modifier = Modifier
            .size(100.dp)
            .padding(4.dp)
    )
}
