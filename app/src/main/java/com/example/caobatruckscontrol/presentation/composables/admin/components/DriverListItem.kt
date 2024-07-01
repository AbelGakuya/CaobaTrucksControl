package com.example.caobatruckscontrol.presentation.composables.admin.components

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.caobatruckscontrol.data.model.Driver

@Composable
fun DriverListItem(
    driver: Driver,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable(onClick = onItemClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "${driver.firstName} ${driver.lastName}",
                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Phone: ${driver.phoneNumber}",
                style = TextStyle(fontSize = 14.sp, color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Email: ${driver.email}",
                style = TextStyle(fontSize = 14.sp)
            )
            Spacer(
                modifier = Modifier.height(4.dp)
            )

        }
    }
}
