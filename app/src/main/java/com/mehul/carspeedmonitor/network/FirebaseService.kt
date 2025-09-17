package com.mehul.carspeedmonitor.network

import com.mehul.carspeedmonitor.data.model.Customer
import com.google.firebase.database.FirebaseDatabase

class FirebaseService{
    private val database = FirebaseDatabase.getInstance().reference

    fun sendAlert(customer: Customer, currentSpeed: Float) {
        val alert = mapOf(
            "customerId" to customer.id,
            "customerName" to customer.name,
            "speed" to currentSpeed,
            "limit" to customer.maxSpeed,
            "timestamp" to System.currentTimeMillis()
        )

        database.child("speed_alerts")
            .child(customer.id)
            .push()
            .setValue(alert)
            .addOnSuccessListener {
                println(" Firebase alert logged for ${customer.name}")
            }
            .addOnFailureListener { e ->
                println("Firebase push failed: ${e.message}")
            }
    }
}