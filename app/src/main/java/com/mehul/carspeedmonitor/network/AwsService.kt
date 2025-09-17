package com.mehul.carspeedmonitor.network

import com.mehul.carspeedmonitor.data.model.Customer

class AwsService{
    fun sendAlert(customer: Customer, speed: Float) {

        println("[AWS] Alert sent to ${customer.name} for speed $speed km/h")
    }
}