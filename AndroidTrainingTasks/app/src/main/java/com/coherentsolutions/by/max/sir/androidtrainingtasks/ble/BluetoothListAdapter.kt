package com.coherentsolutions.by.max.sir.androidtrainingtasks.ble

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.coherentsolutions.by.max.sir.androidtrainingtasks.R
import kotlinx.android.synthetic.main.item_bluetooth_device.view.*

class BluetoothListAdapter(private var item: ArrayList<BluetoothDeviceModel>) :
    ListAdapter<BluetoothDeviceModel, BluetoothListAdapter.ViewHolder>(DiffBLECallback) {


    companion object {
        const val BLE_ADAPTER_TAG = "BLE-adapter"

        object DiffBLECallback : DiffUtil.ItemCallback<BluetoothDeviceModel>() {
            override fun areItemsTheSame(
                oldItem: BluetoothDeviceModel,
                newItem: BluetoothDeviceModel
            ): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(
                oldItem: BluetoothDeviceModel,
                newItem: BluetoothDeviceModel
            ): Boolean {
                return oldItem.macNumber == newItem.macNumber
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bluetooth_device, parent, false)
        return ViewHolder(view as ConstraintLayout)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.view.apply {
            device_name.text = getItem(position).name
            macAddress.text = getItem(position).macNumber
        }
    }

    class ViewHolder(val view: ConstraintLayout) : RecyclerView.ViewHolder(view)
}