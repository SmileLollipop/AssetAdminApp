package com.tzq.assetadminapp.service;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

/**
 * Created by Rachel on 2016/3/26.
 */
public class BTLink {
    private static Context mContext;
    public static BluetoothSocket btSocket = null;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private String address = "98:D3:31:20:72:04";
    private BluetoothAdapter mBluetoothAdapter = null;
    private Set<BluetoothDevice> pairedDevices;
    private  BluetoothDevice RFIDBluetoothDevice=null;
    public   static boolean isConnceted = false;

    public BTLink (Context context) {
        mContext= context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter==null ||(!mBluetoothAdapter.isEnabled())){
            Toast.makeText(mContext, "请打开蓝牙设备！", Toast.LENGTH_LONG).show();
        }
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {

            for (BluetoothDevice device : pairedDevices){
                Log.i("my_info", device.getAddress());
                if(address.equals(device.getAddress())){

                    Log.i("my_info",device.getName());
                    RFIDBluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
                    Log.i("my_info", " RFIDBluetoothDevice ....");
                    try {
                        btSocket = RFIDBluetoothDevice.createRfcommSocketToServiceRecord(MY_UUID);
                        btSocket.connect();
                        isConnceted=true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else {
                    Toast.makeText(mContext, "没有成功配对RFID蓝牙设备！", Toast.LENGTH_LONG).show();
                }
            }
        }else {
            Toast.makeText(mContext, "请配对RFID蓝牙设备！", Toast.LENGTH_LONG).show();

        }

    }
}

