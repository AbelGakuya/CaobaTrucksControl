package com.example.caobatruckscontrol.presentation


import AdministratorScreen
import DriverDetailScreen
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.caobatruckscontrol.data.model.Trip
//import com.example.caobatruckscontrol.presentation.composables.sign_in.AdminSignUpScreen
import com.example.caobatruckscontrol.presentation.composables.admin.PinScreen
import com.example.caobatruckscontrol.presentation.composables.RoleSelectionScreen
import com.example.caobatruckscontrol.presentation.composables.WelcomeScreen
import com.example.caobatruckscontrol.presentation.composables.admin.AddBoxScreen
import com.example.caobatruckscontrol.presentation.composables.admin.AddDriverScreen
import com.example.caobatruckscontrol.presentation.composables.admin.AddTruckScreen
import com.example.caobatruckscontrol.presentation.composables.admin.AdministratorRoleSelectionScreen
import com.example.caobatruckscontrol.presentation.composables.admin.BoxDetailScreen
import com.example.caobatruckscontrol.presentation.composables.admin.BoxListScreen
import com.example.caobatruckscontrol.presentation.composables.admin.CreateTripScreen
import com.example.caobatruckscontrol.presentation.composables.admin.DriverListScreen
import com.example.caobatruckscontrol.presentation.composables.admin.ReportScreen
//import com.example.caobatruckscontrol.presentation.composables.admin.Trip
import com.example.caobatruckscontrol.presentation.composables.admin.TripsScreen
import com.example.caobatruckscontrol.presentation.composables.admin.TruckDetailScreen
import com.example.caobatruckscontrol.presentation.composables.admin.TruckListScreen
import com.example.caobatruckscontrol.presentation.composables.driver.DriverDashboardScreen
import com.example.caobatruckscontrol.presentation.composables.driver.DriverExpenseScreen
import com.example.caobatruckscontrol.presentation.composables.driver.DriverSignInScreen
import com.example.caobatruckscontrol.presentation.composables.sign_in.AdminSignUpScreen
//import com.example.caobatruckscontrol.presentation.composables.sign_in.AdminSignUpScreen
import com.example.caobatruckscontrol.presentation.composables.sign_in.DriverSignUpScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Navigation(
    context: Context,
    navController: NavHostController,
    modifier: Modifier
) {



    val coroutineScope = rememberCoroutineScope()
    // Sample list of trips
//    val trips = listOf(
//        Trip("CTC0001", "John Doe", "Truck A", "Box 1", "2 days left", emptyList(), emptyList(), 0.0, 0.0, null, false),
//        Trip("CTC0002", "Jane Smith", "Truck B", "Box 2", "1 day left", emptyList(), emptyList(), 0.0, 0.0, null, false),
//        // Add more sample trips as needed
//    )

    NavHost(navController = navController, startDestination = NavItem.Welcome.route

    ) {
        composable(route = NavItem.Welcome.route) {
            WelcomeScreen(navController,modifier)
        }
        composable(route = NavItem.RoleSelection.route) {
            RoleSelectionScreen(navController)
        }

        composable(route = NavItem.AdministratorRoleSelection.route){
            AdministratorRoleSelectionScreen(navController)
        }

        composable(NavItem.Pin.route){
            PinScreen(navController)
        }

        composable(NavItem.AdminSignUp.route){
            AdminSignUpScreen(navController)
        }

        composable(NavItem.Admin.route){
            AdministratorScreen(navController)
        }

        composable(NavItem.DriverSignUp.route){
            DriverSignUpScreen(navController = navController)
        }

        composable(NavItem.DriverSignIn.route){
            DriverSignInScreen(navController)
        }

        composable(NavItem.DriverDashboard.route){
            DriverDashboardScreen(navController)
        }

        composable(route = NavItem.DriverExpense.route + "/{tripId}",
            arguments = listOf(navArgument("tripId") { type = NavType.StringType })){
            backStackEntry ->
            val tripId = backStackEntry.arguments?.getString("tripId")
            DriverExpenseScreen(tripId,navController)
        }

        composable(NavItem.Trips.route){
            TripsScreen(navController, onTripClicked = { /* Handle trip click */ })
        }

        composable(NavItem.CreateTrip.route){
            CreateTripScreen(navController,onTripCreated = { navController.popBackStack() })
        }

        composable(NavItem.DriverList.route){
            DriverListScreen(navController)
        }

        composable(
            route = NavItem.DriverDetails.route + "/{driverId}",
            arguments = listOf(navArgument("driverId") { type = NavType.StringType })
        ) { backStackEntry ->
            val driverId = backStackEntry.arguments?.getString("driverId")
            DriverDetailScreen(driverId = driverId, navController = navController)
        }

        composable(NavItem.AddDriver.route){
            AddDriverScreen()
        }

        composable(NavItem.TruckList.route){
            TruckListScreen(navController)
        }

        composable(
            route = NavItem.TruckDetails.route + "/{truckId}",
            arguments = listOf(navArgument("truckId") { type = NavType.StringType })
        ) { backStackEntry ->
            val truckId = backStackEntry.arguments?.getString("truckId")
            TruckDetailScreen(truckId = truckId, navController = navController)
        }

        composable(NavItem.AddTruck.route){
            AddTruckScreen(navController)
        }

        composable(NavItem.BoxList.route){
            BoxListScreen(navController)
        }

        composable(
            route = NavItem.BoxDetails.route + "/{boxId}",
            arguments = listOf(navArgument("boxId") { type = NavType.StringType })
        ) { backStackEntry ->
            val boxId = backStackEntry.arguments?.getString("boxId")
            BoxDetailScreen(boxId = boxId, navController = navController)
        }


        composable(NavItem.AddBox.route){
            AddBoxScreen(navController)
        }

        composable(NavItem.Report.route){
            ReportScreen()
        }


    }

}