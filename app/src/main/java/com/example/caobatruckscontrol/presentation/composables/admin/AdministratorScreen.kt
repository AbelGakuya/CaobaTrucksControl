import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.WindowInsets
//import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.statusBars
import androidx.navigation.NavHostController

@Composable
fun AdministratorScreen(
    navController: NavHostController
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.statusBars.asPaddingValues())
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = "Administrator Dashboard",
            style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 16.dp)
        )

        GridButtons(navController)
    }
}

@Composable
fun GridButtons(
    navController: NavHostController
) {
    val buttons = listOf(
        "Trips", "Drivers", "Trucks", "Boxes", "Reports", "Administrator"
    )

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(buttons.size) { index ->
            Button(
                onClick = {
                          /* Handle button click */
                    when (buttons[index]) {
                        "Trips" -> navController.navigate("trips") // Navigate to TripsScreen
                        // Handle other buttons similarly if needed
                        "Drivers" -> navController.navigate("driver_list")

                        "Trucks" -> navController.navigate("truck_list")

                        "Boxes" -> navController.navigate("box_list")

                        "Reports" -> navController.navigate("report")
                    }
                          },
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.LightGray,
                    contentColor = Color.Black
                )
            ) {
                Text(
                    text = buttons[index],
                    style = TextStyle(fontSize = 18.sp),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}
