package com.example.gabu.datn_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

/**
 * Created by gabu on 5/9/2016.
 */
public class BluetoothService {

    private BluetoothAdapter mAdapter;
    private Handler mHandler;
    private ConnectedThread mConnected;

    private static final UUID MY_UUID = UUID.fromString("001101-0000-1000-8000-00805F9B34FB");
    private static final String HC06_ADDRESS = "98:D3:31:FD:1D:CD";
    public static final int STATE_NONE = 0;
    public static final int STATE_CONNTECTED = 1;
    private int mState;
    private String mControl = "0";

    public BluetoothService(Handler handler) {
        mHandler = handler;
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        mState = STATE_NONE;
    }

    public void setControl(String v){
        mControl = v;
    }

    public int getState() {
        return mState;
    }

    public void connect() {
        if (mState == STATE_NONE) {
            BluetoothDevice device = mAdapter.getRemoteDevice(HC06_ADDRESS);
            BluetoothSocket socket;
            try {
                socket = device.createRfcommSocketToServiceRecord(MY_UUID);
                socket.connect();
            } catch (IOException e) {
                mHandler.obtainMessage(MainActivity.BLUETOOTH_CONNECT, 1, 1).sendToTarget();
                return;
            }
            mHandler.obtainMessage(MainActivity.BLUETOOTH_CONNECT, -1, 1).sendToTarget();
            mState = STATE_CONNTECTED;
            mConnected = new ConnectedThread(socket);
            mConnected.start();
        } else
            mHandler.obtainMessage(MainActivity.BLUETOOTH_CONNECT,0,1).sendToTarget();
    }

    public void disconnect() {
        if (mState == STATE_CONNTECTED) {
            mConnected.cancel();
            mState = STATE_NONE;
            mHandler.obtainMessage(MainActivity.BLUETOOTH_DISCONNECT, -1, 1).sendToTarget();
        } else
            mHandler.obtainMessage(MainActivity.BLUETOOTH_DISCONNECT, 1, 1).sendToTarget();
    }

    private class ConnectedThread extends Thread {
        private BluetoothSocket mmSocket;
        private InputStream mmInStream;
        private OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            while (mState == STATE_CONNTECTED) {
                byte[] readBuf = new byte[1024];
                int bytes;
                byte[] writeBuf = mControl.getBytes();
                try {
                    bytes = mmInStream.read(readBuf);
                    mHandler.obtainMessage(MainActivity.BLUETOOTH_READ, bytes, -1, readBuf).sendToTarget();
                    write(writeBuf);
                } catch (IOException e) {}
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
            } catch (IOException e) {}
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
