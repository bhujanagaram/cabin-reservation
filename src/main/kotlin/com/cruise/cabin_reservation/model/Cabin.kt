package com.cruise.cabin_reservation.model

import jakarta.persistence.CascadeType
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "cabins")
data class Cabin(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val cabinNumber: String,

    val deckNumber: Int,

    val capacity: Int = 2,

    @OneToMany(
        mappedBy = "cabin",
        cascade = [CascadeType.ALL]
    )
    val passengers: MutableList<Passenger> = mutableListOf()
)