package com.cruise.cabin_reservation
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {

    @GetMapping("/hello")
    fun hello(): String {
        return "Cabin Reservation application is running! Joshua 1:9 Have i not commanded be strong and courageous, do not be dismayed for i the lord your GOD is with you wherever you go!!!"
    }
    @GetMapping("/cruise")
    fun hello_cruise(): String {
        return "Welcome to Cruise Reservation Application!"
    }
}