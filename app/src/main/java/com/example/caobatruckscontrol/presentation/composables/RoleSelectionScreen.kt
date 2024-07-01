package com.example.caobatruckscontrol.presentation.composables

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun RoleSelectionScreen(
    navController: NavHostController
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Select your assigned role")
            Spacer(modifier = Modifier.height(32.dp))
            Button(onClick = {
            /* Navigate to Administrator screen */
                navController.navigate("administrator_role_selection")
            }) {
                Text(text = "Administrator")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = {
            /* Navigate to Driver screen */
                navController.navigate("driver_sign_in")
            }) {
                Text(text = "Driver")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RoleSelectionScreenPreview() {
    val navController = rememberNavController()
    RoleSelectionScreen(navController)
}