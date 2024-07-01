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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavController
import com.example.caobatruckscontrol.presentation.composables.admin.components.BoxListItem
import com.example.caobatruckscontrol.presentation.composables.admin.viewmodel.BoxViewModel

@Composable
fun BoxListScreen(
    navController: NavController,
    viewModel: BoxViewModel = hiltViewModel(),

    ) {
    val boxes by viewModel.boxes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(WindowInsets.statusBars.asPaddingValues())
            .navigationBarsPadding()
    ) {
        Text(
            text = "Boxes List",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (boxes.isEmpty()) {
            Text(
                text = "No boxes found",
                style = TextStyle(fontSize = 18.sp),
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {

            LazyColumn {
                items(boxes) { box ->
                    BoxListItem(box = box){
                        navController.navigate("box_details/${box.id}")
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
                navController.navigate("add_box")
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
