package com.coherentsolutions.by.max.sir.androidtrainingtasks.ble

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BLEViewModel : ViewModel() {
    val itemList by lazy { MutableLiveData(ArrayList<BluetoothDeviceModel>()) }
    // enum class

}