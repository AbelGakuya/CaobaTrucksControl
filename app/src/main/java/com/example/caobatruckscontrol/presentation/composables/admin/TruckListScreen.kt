package com.example.caobatruckscontrol.presentation.composables.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.caobatruckscontrol.presentation.composables.admin.components.TruckListItem
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.TruckViewModel

@Composable
fun TruckListScreen(
    navController: NavController,
    viewModel: TruckViewModel = hiltViewModel()
) {
    val trucks by viewModel.trucks.collectAsState()
    val error by viewModel.error.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding()
    ) {
        Text(
            text = "Trucks List",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        if (trucks.isEmpty()) {
            Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text(text = "No trucks available")
            }
        } else {
            LazyColumn {
                items(trucks) { truck ->
                    TruckListItem(truck = truck) {
                        // Navigate to the truck detail screen
                        navController.navigate("truck_details/${truck.id}")
                    }
                }
            }
        }


    }

    Box(
        modifier = Modifier.fillMaxSize().navigationBarsPadding(),
        contentAlignment = Alignment.BottomEnd
    ) {
        FloatingActionButton(
            onClick = { navController.navigate("add_truck") },
            modifier = Modifier.padding(16.dp),
            containerColor = Color.Green // Set background color here

        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Truck")
        }
    }
}
