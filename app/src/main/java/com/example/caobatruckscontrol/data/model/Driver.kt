package com.example.caobatruckscontrol.data.model

import java.time.LocalDateTime

data class Address(
    val street: String = "",
    val number: String = "",
    val interiorNumber: String? = null,
    val neighborhood: String = "",
    val zipCode: String = "",
    val city: String = "",
    val state: String = "",
    val imageUrl: String? = null // URL of the proof of address image
)

data class Driver(
    val id: String? = null, // Firestore document ID
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val identificationType: String = "",
    val identificationNumber: String = "",
    val curp: String = "",
    val proofOfAddress: Address = Address(),
    val licenseType: String = "",
    val licenseNumber: String = "",
    val licenseStartDate: String? = "",
    val licenseExpiryDate: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val medicalCertificateUrl: String? = null,
    val active: Boolean = true // Whether the driver is active
)
