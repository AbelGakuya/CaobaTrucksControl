package com.example.caobatruckscontrol.presentation.composables.admin.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberImagePainter
import com.example.caobatruckscontrol.R
import com.example.caobatruckscontrol.data.model.Box

@Composable
fun BoxListItem(
    box: Box,
    onItemClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable {
                onItemClick()
                       },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberImagePainter(
                    data = box.boxPhotoUrl,
                    builder = {
                        placeholder(R.drawable.logo_1_png) // Optional placeholder
                        error(R.drawable.logo_1_png) // Optional error image
                    }
                ), // Replace with actual drawable resource
                contentDescription = "Box Image",
                modifier = Modifier.size(72.dp),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .padding(horizontal = 16.dp)
                    .weight(1f)
            ) {
                Text(
                    text = "${box.modelYear} ${box.modelYear}",
                    style = MaterialTheme.typography.h6,
                    color = MaterialTheme.colors.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Model Year: ${box.modelYear}",
                    style = MaterialTheme.typography.body1,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "License Plate: ${box.licensePlate}",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Status: ${if (box.isActive) "Active" else "Paused"}",
                    style = MaterialTheme.typography.body2,
                    color = if (box.isActive) Color.Green else Color.Red
                )
            }
        }
    }
}
