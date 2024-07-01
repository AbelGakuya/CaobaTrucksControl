package com.example.caobatruckscontrol.data.model

data class Truck(
    val id: String? = null,
    val brand: String = "",
    val model: String = "",
    val modelYear: String = "",
    val licensePlates: String = "",
    val color: String = "",
    val circulationCardFolio: String = "",
    val insurancePolicy: String = "",
    val insuranceCompany: String = "",
    val active: Boolean = true,
    val frontLicensePlatePhotoUrl: String? = null,
    val rearLicensePlatePhotoUrl: String? = null,
    val truckPhotos: List<String> = emptyList(),
    val circulationCardPhotoUrl: String? = null,
    val insurancePolicyPhotoUrl: String? = null,
    val expenses: List<Expense> = emptyList(),
    val truckPhotosUrl: String? = null,
   // val photos: List<String> = emptyList()
)

data class Expense(
    val category: String = "",
    val amount: Double = 0.0,
    val description: String = "",
    val verificationType: String = "",
    val verificationDetails: VerificationDetails = VerificationDetails()
)

data class VerificationDetails(
    val invoiceFolio: String? = null,
    val companyName: String? = null,
    val photoUrl: String? = null,
    val noteIssuer: String? = null,
    val mileage: Int? = null
)
