package com.example.caobatruckscontrol.presentation.composables.admin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.caobatruckscontrol.data.model.TripReport
import com.example.caobatruckscontrol.presentation.composables.admin.components.TruckListItem
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.ReportViewModel
import com.google.firebase.auth.FirebaseAuth


@Composable
fun ReportScreen(
    viewModel: ReportViewModel = hiltViewModel()
) {
    val reports by viewModel.reports.collectAsState()
    val error by viewModel.error.collectAsState()

    // State to track selected report for showing details
    var selectedReport by remember { mutableStateOf<TripReport?>(null) }

    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    // Fetch reports when the screen is first displayed
    LaunchedEffect(Unit) {
        viewModel.fetchReports(currentUserId!!) // Replace with actual adminId or currentUserId logic
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding()
    ) {
        Text(
            text = "Reports List",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (error != null) {
            Text(text = error!!, color = MaterialTheme.colorScheme.error)
        }

        if (reports.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "No reports available")
            }
        } else {
            LazyColumn {
                items(reports) { report ->
                    ReportItem(report = report) {
                        selectedReport = report
                    }
                }
            }
        }

        // Dialog to show detailed report information
        selectedReport?.let { report ->
            ReportDetailsDialog(tripReport = report) {
                selectedReport = null // Close dialog on dismiss
            }
        }
    }
}

@Composable
fun ReportItem(
    report: TripReport,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Report ID: ${report.adminId}")
            Text(text = "Driver Name: ${report.driverName}")
            Text(text = "Truck: ${report.truck}")
            // Add more fields as needed
        }
    }
}

@Composable
fun ReportDetailsDialog(
    tripReport: TripReport,
    onCloseDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCloseDialog,
        title = { Text("Report Details") },
        text = {
            Column {
                Text("Driver's Name: ${tripReport.driverName}")
                Text("Truck and Box: ${tripReport.truck}, ${tripReport.box}")
                Text("Starting City: ${tripReport.origin}")
                Text("Destination City: ${tripReport.destination}")
                Text("Kilometers Traveled: ${tripReport.kilometers}")
                Text("Cargo: ${tripReport.cargo}")
                Text("Journey Time: ${tripReport.journeyTime}")
                Text("Mileage: ${tripReport.mileage}")
                Text("Total Travel Expenses: ${tripReport.totalExpenses}")
                Text("Total Deposits for Travel Expenses: ${tripReport.totalDeposits}")
                Text("Wear Cost: ${tripReport.wearCost}")
                Text("Customer Payment: ${tripReport.customerPayment}")
                Text("Comparison of Travel Expenses Against Deposits: ${tripReport.expenseComparison}")
            }
        },
        confirmButton = {
            Button(
                onClick = onCloseDialog
            ) {
                Text("Close")
            }
        }
    )
}

