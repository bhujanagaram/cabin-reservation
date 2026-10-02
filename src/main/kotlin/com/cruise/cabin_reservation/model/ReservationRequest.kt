package com.cruise.cabin_reservation.model

data class ReservationRequest(
    val cabinNumber: String,
    val passengers: List<PassengerRequest>
)