package com.example.caobatruckscontrol.data.model

import com.google.firebase.Timestamp

data class CustomerPayment(
    val amount: Double = 0.0,
    val timestamp: Timestamp = Timestamp.now()
)

