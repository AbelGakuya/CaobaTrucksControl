package com.example.caobatruckscontrol.data.model

data class TripReport(
    val driverName: String = "",
    val adminId: String = "",
    val truck: String = "",
    val box: String = "",
    val origin: String = "",
    val destination: String = "",
    val kilometers: String = "",
    val cargo: String = "",
    val journeyTime: String = "",
    val mileage: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val totalDeposits: Double = 0.0,
    val wearCost: Double = 0.0,
    val customerPayment: String = "",
    val expenseComparison: Double = 0.0
)

