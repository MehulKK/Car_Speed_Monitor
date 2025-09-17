package com.mehul.carspeedmonitor.data.repository

import com.mehul.carspeedmonitor.data.model.CommunicationChannel
import com.mehul.carspeedmonitor.data.model.Customer

class InMemorySpeedRepository: SpeedRepository {
    // mock data
    private val customers = listOf(
        Customer("1", "Mehul", 40f, CommunicationChannel.FIREBASE),
        Customer("2", "Kabaria", 50f, CommunicationChannel.AWS)
    )

    override fun getCustomer(customerId: String): Customer {
        return customers.first { it.id == customerId }
    }
}