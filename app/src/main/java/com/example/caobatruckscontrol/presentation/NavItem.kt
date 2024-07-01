package com.example.caobatruckscontrol.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Welcome: NavItem("welcome", "Welcome", Icons.Filled.Home)
    object RoleSelection: NavItem("role_selection", "Role Selection", Icons.Filled.Home)
    object AdministratorRoleSelection:
        NavItem("administrator_role_selection", "Administrator Role Selection", Icons.Filled.Home)
    object Pin: NavItem("pin", "Pin", Icons.Filled.Home)

    object AdminSignUp: NavItem("admin_sign_up", "Admin Sign Up", Icons.Filled.Home)

    object Admin: NavItem(route = "admin", title = "Admin", icon = Icons.Filled.Home)

    object DriverSignUp: NavItem("driver_sign_up", "Driver Sign Up", Icons.Filled.Home)
    object DriverSignIn: NavItem("driver_sign_in", "Driver Sign In", Icons.Filled.Home)
    object Trips: NavItem("trips", "Trips", Icons.Filled.Home)
    object CreateTrip: NavItem("create_trip", "Create Trip", Icons.Filled.Home)
    object DriverList: NavItem("driver_list", "Driver List", Icons.Filled.Home)
    object AddDriver: NavItem("add_driver", "Add Driver", Icons.Filled.Home)
    object TruckList: NavItem("truck_list", "Truck List", Icons.Filled.Home)
    object AddTruck: NavItem("add_truck", "Add Truck", Icons.Filled.Home)
    object BoxList: NavItem("box_list", "Box List", Icons.Filled.Home)
    object AddBox: NavItem("add_box", "Add Box", Icons.Filled.Home)
    //object DriverDetails: NavItem("driver_details", "Driver Details", Icons.Filled.Home)
    object DriverDetails : NavItem("driver_details", "Driver Details", Icons.Filled.Home) {
        fun createRoute(driverId: String) = "driver_details/$driverId"
    }

    object TruckDetails : NavItem("truck_details", "Truck Details", Icons.Filled.Home) {
        fun createRoute(truckId: String) = "driver_details/$truckId"
    }

    object BoxDetails : NavItem("box_details", "Box Details", Icons.Filled.Home) {
        fun createRoute(boxId: String) = "box_details/$boxId"
    }

    object DriverDashboard : NavItem("driver_dashboard", "Driver Dashboard", Icons.Filled.Home)
    object Report : NavItem("report", "Report", Icons.Filled.Home)
    object DriverExpense : NavItem("driver_expense", "Driver Expense", Icons.Filled.Home){
        fun createRoute(tripId: String) = "driver_expense/$tripId"
    }



    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach {arg ->
                append("/$arg")

            }
        }
    }
}