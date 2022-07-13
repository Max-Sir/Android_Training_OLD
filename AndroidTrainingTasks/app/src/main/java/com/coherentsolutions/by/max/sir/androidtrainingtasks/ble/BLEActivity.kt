package com.coherentsolutions.by.max.sir.androidtrainingtasks.ble

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import c.tlgbltcn.library.BluetoothHelper
import c.tlgbltcn.library.BluetoothHelperListener
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import kotlinx.android.synthetic.main.activity_bleactivity.*

class BLEActivity : AppCompatActivity(), BluetoothHelperListener {
    private lateinit var bluetoothHelper: BluetoothHelper

    private lateinit var viewAdapter: BluetoothListAdapter

    private var itemList = ArrayList<BluetoothDeviceModel>()

    private val viewModel by lazyOf(BLEViewModel())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bleactivity)


        viewModel.itemList.observe(this, {
            viewAdapter.submitList(it)
            itemList = it
        })

        bluetoothHelper = BluetoothHelper(this@BLEActivity, this@BLEActivity)
            .setPermissionRequired(true)
            .create()

        if (bluetoothHelper.isBluetoothEnabled()) enable_disable.text = getString(R.string.ble_on)
        else enable_disable.text = getString(R.string.ble_off)

        if (bluetoothHelper.isBluetoothScanning()) start_stop.text = getString(R.string.stop)
        else start_stop.text = getString(R.string.start)


        enable_disable.setOnClickListener {
            if (bluetoothHelper.isBluetoothEnabled()) {

                bluetoothHelper.disableBluetooth()
                viewAdapter.submitList(itemList)

            } else {
                bluetoothHelper.enableBluetooth()
            }
        }

        start_stop.setOnClickListener {
            if (bluetoothHelper.isBluetoothScanning()) {
                bluetoothHelper.stopDiscovery()

            } else {
                bluetoothHelper.startDiscovery()
            }
        }

        viewAdapter = BluetoothListAdapter(itemList)
        recycler_view.apply {
            layoutManager = LinearLayoutManager(this@BLEActivity)
            adapter = viewAdapter
        }
    }


    override fun onStartDiscovery() {
        start_stop.text = getString(R.string.stop)
        viewAdapter.submitList(emptyList())

    }

    override fun onFinishDiscovery() {
        start_stop.text = getString(R.string.start)
        viewAdapter.submitList(itemList.also { it.clear() })
    }

    override fun onEnabledBluetooth() {
        enable_disable.text = getString(R.string.ble_on)
    }

    override fun onDisabledBluetooh() {
        enable_disable.text = getString(R.string.ble_off)
        viewAdapter.submitList(emptyList())

    }

    override fun getBluetoothDeviceList(device: BluetoothDevice) {
        itemList.clear()
        itemList.add(BluetoothDeviceModel(device.name, device.address))
        viewAdapter.submitList(itemList)
    }

    override fun onResume() {
        super.onResume()
        bluetoothHelper.registerBluetoothStateChanged()
    }


    override fun onStop() {
        super.onStop()
        bluetoothHelper.unregisterBluetoothStateChanged()
    }
}