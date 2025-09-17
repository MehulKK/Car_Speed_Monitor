package com.mehul.carspeedmonitor.data.repository

import com.mehul.carspeedmonitor.data.model.Customer

interface SpeedRepository{fun getCustomer(customerId:String):Customer}