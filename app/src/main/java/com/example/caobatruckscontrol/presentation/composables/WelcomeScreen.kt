package com.example.caobatruckscontrol.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.caobatruckscontrol.R
import kotlinx.coroutines.delay

@Composable
fun WelcomeScreen(
    navController: NavHostController,
    modifier: Modifier
) {
    LaunchedEffect(Unit) {
        delay(2000) // Wait for 2 seconds
        navController.navigate("role_selection")
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.logo3png), // Your background image
            contentDescription = null,
            modifier = modifier.fillMaxSize()
        )
        Image(
            painter = painterResource(id = R.drawable.logo_2_jpeg), // Your logo image
            contentDescription = "Logo",
            modifier = modifier.align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun WelcomeScreenPreview() {
    val navController = rememberNavController()
    //WelcomeScreen(navController)
}