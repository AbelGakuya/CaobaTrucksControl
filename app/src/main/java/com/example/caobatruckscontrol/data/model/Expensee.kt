package com.example.caobatruckscontrol.data.model

import com.google.firebase.Timestamp

data class Expensee(
    val isTruckExpense: Boolean = true,
    val category: String = "",
    val expenseType: String = "",
    val invoiceFolio: String? = null,
    val issuingCompany: String = "",
    val amount: Double = 0.0,
    val odometer: Double? = null,
    val description: String? = null,
    val photoUri: String? = null,
    val timestamp: Timestamp? = null
)
