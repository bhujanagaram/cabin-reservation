package com.cruise.cabin_reservation.controller

import com.cruise.cabin_reservation.model.Cabin
import com.cruise.cabin_reservation.model.Passenger
import com.cruise.cabin_reservation.model.ReservationRequest
import com.cruise.cabin_reservation.service.CabinReservationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class CabinReservationController(
    private val cabinReservationService: CabinReservationService
) {

    @GetMapping("/cabins")
    fun getCabins(): List<Cabin> {
        return cabinReservationService.getAllCabins()
    }

    @GetMapping("/cabins/available")
    fun getAvailableCabins(
        @RequestParam deckNumber: Int
    ): List<Cabin> {

        return cabinReservationService
            .getAvailableCabins(deckNumber)
    }

    @PostMapping("/reservations")
    fun createReservation(
        @RequestBody request: ReservationRequest
    ): ResponseEntity<Any> {

        return try {

            val passengers = request.passengers.map {
                Passenger(
                    name = it.name,
                    age = it.age
                )
            }

            val reservedCabins =
                cabinReservationService.assignPassengers(
                    request.cabinNumber,
                    passengers
                )

            ResponseEntity.ok(reservedCabins)

        } catch (e: IllegalArgumentException) {

            ResponseEntity
                .badRequest()
                .body(
                    mapOf(
                        "message" to (
                                e.message
                                    ?: "Reservation failed"
                                )
                    )
                )
        }
    }
}