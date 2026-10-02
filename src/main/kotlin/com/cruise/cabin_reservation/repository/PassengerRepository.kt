package com.cruise.cabin_reservation.repository

import com.cruise.cabin_reservation.model.Passenger
import org.springframework.data.jpa.repository.JpaRepository

interface PassengerRepository : JpaRepository<Passenger, Long>