package com.mehul.carspeedmonitor

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.mehul.carspeedmonitor.data.repository.InMemorySpeedRepository
import com.mehul.carspeedmonitor.domain.usecase.CheckSpeedUseCase
import com.mehul.carspeedmonitor.hardware.VehicleSpeedSensor
import com.mehul.carspeedmonitor.network.AwsService
import com.mehul.carspeedmonitor.network.FirebaseService
import com.mehul.carspeedmonitor.notification.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class Main:Service(){
    private lateinit var checkSpeedUseCase: CheckSpeedUseCase
    private lateinit var sensor: VehicleSpeedSensor
    private lateinit var notificationHelper: NotificationHelper
    override fun onCreate() {
        super.onCreate()

        val firebaseService = FirebaseService()
        val awsService = AwsService()
        val repository = InMemorySpeedRepository()
         notificationHelper = NotificationHelper(this)

        // Fetch customer dynamically
        val customer1 = repository.getCustomer("1")
        val customer2 = repository.getCustomer("2")

        sensor = VehicleSpeedSensor(
            context = this,
            maxSpeed = maxOf(customer1.maxSpeed, customer2.maxSpeed)
        )

        checkSpeedUseCase = CheckSpeedUseCase(
            sensor = sensor,
            repository = repository,
            firebaseService = firebaseService,
            awsService = awsService,
            notificationHelper = notificationHelper
        )

        // Start monitoring in a coroutine
        CoroutineScope(Dispatchers.Default).launch {
            checkSpeedUseCase.checkSpeedAndNotify(customer1)
            checkSpeedUseCase.checkSpeedAndNotify(customer2)
        }
    }
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        sensor.release()
        notificationHelper.clearNotification()
    }
}
