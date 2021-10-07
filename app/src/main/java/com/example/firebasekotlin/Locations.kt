package com.example.firebasekotlin

class Locations {
    var latitude: Double? = null
    var longitude: Double? = null


    constructor(
        latitude: Double,
        longitude: Double

        ) {
        this.latitude = latitude
        this.longitude = longitude

    }

    constructor()
}