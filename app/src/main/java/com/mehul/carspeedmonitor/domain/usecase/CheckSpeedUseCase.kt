package com.mehul.carspeedmonitor.domain.usecase

import com.mehul.carspeedmonitor.data.model.CommunicationChannel
import com.mehul.carspeedmonitor.data.model.Customer
import com.mehul.carspeedmonitor.data.repository.SpeedRepository
import com.mehul.carspeedmonitor.hardware.IVehicleSpeedSensor
import com.mehul.carspeedmonitor.hardware.SpeedAlertListener
import com.mehul.carspeedmonitor.hardware.VehicleSpeedSensor
import com.mehul.carspeedmonitor.network.AwsService
import com.mehul.carspeedmonitor.network.FirebaseService
import com.mehul.carspeedmonitor.notification.NotificationHelper

class CheckSpeedUseCase(
    private val sensor: IVehicleSpeedSensor,
    private val firebaseService: FirebaseService,
    private val awsService: AwsService,
    private val repository: SpeedRepository,
    private val notificationHelper: NotificationHelper
) {

    fun checkSpeedAndNotify(customer: Customer) {
        val customer = repository.getCustomer(customer.id)

        if (sensor is VehicleSpeedSensor) {
            sensor.setListener(object : SpeedAlertListener {
                override fun onSpeedExceeded(currentSpeed: Float) {
                    if (currentSpeed > customer.maxSpeed) {
                        // Show notification
                        notificationHelper.showSpeedAlert(currentSpeed, customer.maxSpeed)
                        // Send backend alert
                        when (customer.preferredChannel) {
                            CommunicationChannel.FIREBASE -> firebaseService.sendAlert(customer, currentSpeed)
                            CommunicationChannel.AWS -> awsService.sendAlert(customer, currentSpeed)
                        }
                    } else {
                        // Clear notification automatically
                        notificationHelper.clearNotification()
                    }
                }
            })
        }
    }
}