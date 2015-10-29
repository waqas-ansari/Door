package com.fyp.waqasansari.doorlock;

import android.app.Service;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.widget.Toast;
import android.content.Intent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

/**
 * Created by Linta Ansari on 8/9/2015.
 */
public class DoorLockService extends Service {

    String address;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);

        //final Handler handler = new Handler();
        //final int delay = 5000;
        //handler.postDelayed(new Runnable() {
        //    @Override
        //    public void run() {
                //ConnectAndUnlockTheDoor();


        //stopService(new Intent(this, DoorLockService.class));
        //        handler.postDelayed(this, delay);
        //    }
        //}, delay);


        /*new Timer().scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                ConnectAndUnlockTheDoor();
            }
        }, 0, 5000);*/

        /*ScheduledExecutorService scheduleTask = Executors.newScheduledThreadPool(4);

        Log.d("Start", "Entering in Scheduler");
        scheduleTask.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                ConnectAndUnlockTheDoor();
            }
        }, 0, 20, TimeUnit.SECONDS);*/

    }


    private void ConnectAndUnlockTheDoor(){
        myBluetooth = BluetoothAdapter.getDefaultAdapter();
        if(myBluetooth == null)
        {
            //Show a mensag. that thedevice has no bluetooth adapter
            Toast.makeText(getApplicationContext(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
        }
        else
        {
            if (!myBluetooth.isEnabled())
            { Log.d("Bluetooth", "Enabled");  myBluetooth.enable(); }
        }
        pairedDevices = myBluetooth.getBondedDevices();
        if(pairedDevices.size() > 0){
            for(BluetoothDevice bt : pairedDevices){
                if(bt.getAddress().equals("98:D3:31:90:49:B3")){
                    address = bt.getAddress();
                    try
                    {

                        if (btSocket == null || !isBtConnected)
                        {
                            myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
                            BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
                            btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
                            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                            btSocket.connect();//start connection
                            msg("Connected.");
                            Log.d("Connected", "To device");
                        }
                    }
                    catch (IOException e) { msg("Something goes wrong");  }

                    isBtConnected = true;
                    unlockTheDoor();
                }
            }
        }
    }

    private void msg(String s)
    {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    private void unlockTheDoor()
    {
        String jsonString = "{'unlocker' : \"Waqas Ansari\"}";
        if (btSocket!=null)
        {
            try
            {
                btSocket.getOutputStream().write("TO".getBytes());
                btSocket.getOutputStream().write(jsonString.getBytes());

                Log.d("TAG", "Unlocking the door...");
            }
            catch (IOException e)
            {
                Log.d("ERROR", e.toString());
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        msg("Service Destroyed");
        //Intent intent = new Intent("com.android.techtrainner");
        //sendBroadcast(intent);
    }

}
