package com.cruise.cabin_reservation.service

import com.cruise.cabin_reservation.model.Cabin
import com.cruise.cabin_reservation.model.Passenger
import com.cruise.cabin_reservation.repository.CabinRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CabinReservationService(
    private val cabinRepository: CabinRepository
) {

    @PostConstruct
    fun initializeCabins() {

        if (cabinRepository.count() == 0L) {

            val cabins = mutableListOf<Cabin>()

            for (deck in 1..3) {
                for (cabin in 1..10) {

                    cabins.add(
                        Cabin(
                            cabinNumber = "D${deck}C$cabin",
                            deckNumber = deck
                        )
                    )
                }
            }

            cabinRepository.saveAll(cabins)
        }
    }

    fun getAllCabins(): List<Cabin> {
        return cabinRepository.findAll()
    }

    fun getAvailableCabins(
        deckNumber: Int
    ): List<Cabin> {

        return cabinRepository
            .findByDeckNumber(deckNumber)
            .filter { cabin ->
                cabin.passengers.size < cabin.capacity
            }
    }

    @Transactional
    fun assignPassengers(
        cabinNumber: String,
        passengers: List<Passenger>
    ): List<Cabin> {

        validatePassengers(passengers)

        val cabin = cabinRepository
            .findByCabinNumber(cabinNumber)
            ?: throw IllegalArgumentException(
                "Cabin $cabinNumber does not exist."
            )

        if (passengers.size > cabin.capacity) {
            throw IllegalArgumentException(
                "Reservation unsuccessful. " +
                        "Cabin $cabinNumber can hold only " +
                        "${cabin.capacity} passengers."
            )
        }

        val occupiedSeats = cabin.passengers.size

        val availableSeats =
            cabin.capacity - occupiedSeats

        if (passengers.size > availableSeats) {
            throw IllegalArgumentException(
                "Reservation unsuccessful. " +
                        "Cabin $cabinNumber does not have enough available space."
            )
        }

        passengers.forEach { passenger ->

            passenger.cabin = cabin

            cabin.passengers.add(passenger)
        }

        cabinRepository.save(cabin)

        return listOf(cabin)
    }

    private fun validatePassengers(
        passengers: List<Passenger>
    ) {

        if (passengers.isEmpty()) {
            throw IllegalArgumentException(
                "Reservation unsuccessful. Passenger list cannot be empty."
            )
        }

        val childrenUnder14 = passengers.count {
            it.age < 14
        }

        val adults = passengers.count {
            it.age >= 18
        }

        if (childrenUnder14 > 0 && adults == 0) {
            throw IllegalArgumentException(
                "Reservation unsuccessful. Age requirement not met. " +
                        "At least one adult aged 18 or older must accompany " +
                        "any passenger under 14 years of age."
            )
        }
    }
}