package com.mehul.carspeedmonitor.hardware

import android.car.Car
import android.car.VehiclePropertyIds
import android.car.hardware.property.CarPropertyManager
import android.content.Context


interface SpeedAlertListener {
    fun onSpeedExceeded(currentSpeed: Float)
}
class VehicleSpeedSensor(
    context: Context,
    private val maxSpeed: Float,
    private var listener: SpeedAlertListener? = null
) : IVehicleSpeedSensor.Stub() {

    private val car: Car = Car.createCar(context)
    private val carPropertyManager: CarPropertyManager =
        car.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager

    // Store callback so we can unregister it later
    private val callback = object : CarPropertyManager.CarPropertyEventCallback {
        override fun onChangeEvent(event: CarPropertyManager.CarPropertyEvent<*>) {
            if (event.propertyId == VehiclePropertyIds.PERF_VEHICLE_SPEED) {
                val currentSpeed = event.value as Float
                if (currentSpeed > maxSpeed) {
                    listener?.onSpeedExceeded(currentSpeed)
                }
            }
        }

        override fun onErrorEvent(propId: Int, zone: Int) {
            // Handle errors if needed
        }
    }

    init {
        // Register the callback
        carPropertyManager.registerCallback(
            callback,
            VehiclePropertyIds.PERF_VEHICLE_SPEED,
            CarPropertyManager.SENSOR_RATE_NORMAL
        )
    }

    override fun getCurrentSpeed(): Float =
        try {
            carPropertyManager.getFloatProperty(VehiclePropertyIds.PERF_VEHICLE_SPEED, 0)
        } catch (e: Exception) {
            e.printStackTrace()
            0f
        }

    fun release() {
        // Unregister callback and disconnect from car
        carPropertyManager.unregisterCallback(callback)
        car.disconnect()
    }

    fun setListener(listener: SpeedAlertListener) {
        this.listener = listener
    }
}