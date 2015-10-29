package com.fyp.waqasansari.doorlock;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity {

    String address;
    BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private BluetoothAdapter myBluetooth = null;
    private Set<BluetoothDevice> pairedDevices;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://homeiot.firebaseio.com/");

        Button btnStartCode = (Button) findViewById(R.id.btnStartCode);
        btnStartCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectAndUnlockTheDoor();
            }
        });

        Button btnStartService = (Button) findViewById(R.id.btnStartService);
        btnStartService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startService(new Intent(MainActivity.this, DoorLockService.class));
            }
        });

        Button btnStopService = (Button) findViewById(R.id.btnStopService);
        btnStopService.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, DoorLockService.class));
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }





    private void ConnectAndUnlockTheDoor(){
        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        while(myBluetooth.getState() != BluetoothAdapter.STATE_ON) {    myBluetooth = BluetoothAdapter.getDefaultAdapter();  Log.d("State", "Waiting for state to be ON..."); }
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

        Log.d("OK", "Working");
        pairedDevices = myBluetooth.getBondedDevices();
        if(pairedDevices.size() > 0){
            Log.d("OK", "Working");
            for(BluetoothDevice bt : pairedDevices){
                if(bt.getAddress().equals("98:D3:31:90:49:B3")){
                    Log.d("OK", "Working");
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
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
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



}
