package com.example.caobatruckscontrol.presentation.composables.admin

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.caobatruckscontrol.presentation.composables.admin.components.DriverListItem
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.DriverViewModel
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController

@Composable
fun DriverListScreen(
    navController: NavController,
    viewModel: DriverViewModel = hiltViewModel()
) {
    val drivers by viewModel.drivers.collectAsState()

    // Effect for initializing or refreshing drivers list
    viewModel.fetchDrivers()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding()
    ) {
        Text(
            text = "Drivers List",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (drivers.isEmpty()) {
            Text(
                text = "No drivers found",
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn {
                items(drivers) { driver ->
                    DriverListItem(driver = driver) {
                        navController.navigate("driver_details/${driver.id}")
                    }
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
                navController.navigate("add_driver")
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
