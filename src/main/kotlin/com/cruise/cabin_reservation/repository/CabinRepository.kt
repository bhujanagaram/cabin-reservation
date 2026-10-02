package com.cruise.cabin_reservation.repository

import com.cruise.cabin_reservation.model.Cabin
import org.springframework.data.jpa.repository.JpaRepository

interface CabinRepository : JpaRepository<Cabin, Long> {

    fun findByCabinNumber(cabinNumber: String): Cabin?

    fun findByDeckNumber(deckNumber: Int): List<Cabin>
}