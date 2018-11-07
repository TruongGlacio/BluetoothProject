package com.example.gabu.datn_bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.getbase.floatingactionbutton.FloatingActionButton;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    /*
    Các biến đánh dấu dữ liệu gửi về từ các luồng
     */
    public static final int FIREBASE_CONTROL = 1;
    public static final int FIREBASE_STATE = 2;
    public static final int BLUETOOTH_READ = 3;
    public static final int BLUETOOTH_CONNECT = 4;
    public static final int BLUETOOTH_DISCONNECT = 5;
    public static final int BLUETOTOH_WRITE = 6;
    public static final int BEACON_DISTANCE = 7;
    /*
    Các biến để xử lý luồng tập trung tại Main
     */
    private BluetoothAdapter mAdapter;
    private BluetoothService mBluetoothService;
    private FirebaseService mFirebaseService;
    /*
    Các biến giao diện
     */
    private TextView tvCloud, tvRobocar;
    private FloatingActionButton fabGo, fabRight, fabBack, fabLeft, fabStop, fabQuit,
            fabConnect, fabDisconnect, fabMap, fabStream;
    /*
    Các biến xử lý Beacons
     */
    private BeaconManager mBeaconManager;
    private static final String ADDRESS_BEACON_1 = "00:07:80:1F:A9:63";
    private static final String ADDRESS_BEACON_2 = "00:07:80:1F:BC:BC";
    private float mDistanceBea1,mDistanceBea2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Khởi tạo bộ thu phát bluetooth
        mAdapter = BluetoothAdapter.getDefaultAdapter();
        //Khởi tạo firebase
        Firebase.setAndroidContext(this);
        //Gọi luồng xử lý firebase
        mFirebaseService = new FirebaseService(this, mHandler);
        mFirebaseService.start();
        //Gọi luồng xử lý bluetooth
        mBluetoothService = new BluetoothService(mHandler);
        //Gán giá trị cho các control
        tvCloud = (TextView) findViewById(R.id.tvCloud);
        tvRobocar = (TextView) findViewById(R.id.tvRobocar);
        fabGo = (FloatingActionButton) findViewById(R.id.fabGo);
        fabRight = (FloatingActionButton) findViewById(R.id.fabRight);
        fabBack = (FloatingActionButton) findViewById(R.id.fabBack);
        fabLeft = (FloatingActionButton) findViewById(R.id.fabLeft);
        fabStop = (FloatingActionButton) findViewById(R.id.fabStop);
        fabQuit = (FloatingActionButton) findViewById(R.id.fabQuit);
        fabMap = (FloatingActionButton) findViewById(R.id.fabMap);
        fabStream = (FloatingActionButton) findViewById(R.id.fabStream);
        fabConnect = (FloatingActionButton) findViewById(R.id.fabConnect);
        fabDisconnect = (FloatingActionButton) findViewById(R.id.fabDisconnect);
        //Gán sự kiện cho các button
        fabGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothService.setControl("1");
                if (mBluetoothService.getState() == BluetoothService.STATE_CONNTECTED) {
                    chooseButton(fabGo);
                    notChooseButton(fabRight, fabBack, fabLeft, fabStop);
                }
            }
        });
        fabRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothService.setControl("2");
                if (mBluetoothService.getState() == BluetoothService.STATE_CONNTECTED) {
                    chooseButton(fabRight);
                    notChooseButton(fabGo, fabBack, fabLeft, fabStop);
                }
            }
        });
        fabBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothService.setControl("3");
                if (mBluetoothService.getState() == BluetoothService.STATE_CONNTECTED) {
                    chooseButton(fabBack);
                    notChooseButton(fabGo, fabRight, fabLeft, fabStop);
                }
            }
        });
        fabLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothService.setControl("4");
                if (mBluetoothService.getState() == BluetoothService.STATE_CONNTECTED) {
                    chooseButton(fabLeft);
                    notChooseButton(fabGo, fabRight, fabBack, fabStop);
                }
            }
        });
        fabStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothService.setControl("0");
                if (mBluetoothService.getState() == BluetoothService.STATE_CONNTECTED) {
                    chooseButton(fabStop);
                    notChooseButton(fabGo, fabRight, fabBack, fabLeft);
                }
            }
        });
        fabQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothService.disconnect();
                finish();
            }
        });
        fabConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter.isEnabled()) {
                    mBluetoothService.connect();
                } else
                    Toast.makeText(getApplicationContext(), "Chưa bật Bluetooth", Toast.LENGTH_SHORT).show();
            }
        });
        fabDisconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothService.disconnect();
            }
        });
        fabMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ActivityMap.class);
               intent.putExtra("Beacon1",mDistanceBea1);
               intent.putExtra("Beacon2",mDistanceBea2);
                startActivity(intent);
            }
        });
        /**
         * Xử lý Beacons
         */
        mBeaconManager = BeaconManager.getInstanceForApplication(this);
        mBeaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        mBeaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBeaconManager.unbind(this);
    }

    private void chooseButton(FloatingActionButton fab) {
        fab.setColorNormal(getResources().getColor(R.color.colorButtonPressed));
    }

    private void notChooseButton(FloatingActionButton fab1, FloatingActionButton fab2,
                                 FloatingActionButton fab3, FloatingActionButton fab4) {
        fab1.setColorNormal(getResources().getColor(R.color.colorButtonNormal));
        fab2.setColorNormal(getResources().getColor(R.color.colorButtonNormal));
        fab3.setColorNormal(getResources().getColor(R.color.colorButtonNormal));
        fab4.setColorNormal(getResources().getColor(R.color.colorButtonNormal));
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FIREBASE_CONTROL:
                    String txt = (String) msg.obj;
                    mBluetoothService.setControl(txt);
                    break;
                case FIREBASE_STATE:
                    int x = (int) msg.obj;
                    if (x == 1) {
                        tvCloud.setText("ĐÃ KẾT NỐI");
                        tvCloud.setTextColor(getResources().getColor(R.color.colorEnable));
                    }
                    break;
                case BLUETOOTH_CONNECT:
                    switch (msg.arg1) {
                        case -1:
                            tvRobocar.setText("ĐÃ KẾT NỐI");
                            tvRobocar.setTextColor(getResources().getColor(R.color.colorEnable));
                            Toast.makeText(getApplicationContext(), "Kết nối thành công", Toast.LENGTH_SHORT).show();
                            chooseButton(fabConnect);
                            mFirebaseService.write("Robocar", "1");
                            break;
                        case 0:
                            Toast.makeText(getApplicationContext(), "Đang có một kết nối", Toast.LENGTH_SHORT).show();
                            break;
                        case 1:
                            tvRobocar.setText("CHƯA KẾT NỐI");
                            tvRobocar.setTextColor(getResources().getColor(R.color.colorDisable));
                            Toast.makeText(getApplicationContext(), "Không thể kết nối", Toast.LENGTH_SHORT).show();
                            break;
                    }
                    break;
                case BLUETOOTH_DISCONNECT:
                    if (msg.arg1 == -1) {
                        tvRobocar.setText("CHƯA KẾT NỐI");
                        tvRobocar.setTextColor(getResources().getColor(R.color.colorDisable));
                        Toast.makeText(getApplicationContext(), "Ngắt kết nối thành công", Toast.LENGTH_SHORT).show();
                        fabConnect.setColorNormal(getResources().getColor(R.color.colorButtonNormal));
                    } else {
                        Toast.makeText(getApplicationContext(), "Không tồn tại kết nối", Toast.LENGTH_SHORT).show();
                    }
                    mFirebaseService.write("Robocar", "0");
                    break;
                case BLUETOOTH_READ:
                    byte[] buffer = (byte[]) msg.obj;
                    String read = new String(buffer, 0, msg.arg1).trim();
                    mFirebaseService.write("Distance", read);
                    break;
                case BLUETOTOH_WRITE:
                    if (msg.arg1 == 1)
                        Toast.makeText(getApplicationContext(), "Không tồn tại kết nối", Toast.LENGTH_SHORT).show();
                    break;
                case BEACON_DISTANCE:
                    if(msg.arg1 == 1){
                        mDistanceBea1 = (float) msg.obj;
                    //    MapActivity.d1 = mDistanceBea1;
                        mFirebaseService.write("Beacons","Bea1",mDistanceBea1+"");
                    }
                    else {
                        mDistanceBea2 = (float) msg.obj;
                        //MapActivity.d2 = mDistanceBea2;
                        mFirebaseService.write("Beacons", "Bea2", mDistanceBea2 + "");
                    }
                    break;
            }
        }
    };

    /**
     * Xử lý Beacons
     */
    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("myBeaons", null, null, null);
        mBeaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                try {
                    mBeaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    mBeaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {

            }
        });
        mBeaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> collection, Region region) {
                for (final Beacon beacon : collection) {
                    final float distance = (float) beacon.getDistance();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            switch (beacon.getBluetoothAddress()) {
                                case ADDRESS_BEACON_1:
                                    mHandler.obtainMessage(MainActivity.BEACON_DISTANCE, 1, 0, distance)
                                            .sendToTarget();
                                    break;
                                case ADDRESS_BEACON_2:
                                    mHandler.obtainMessage(MainActivity.BEACON_DISTANCE, 2, 0, distance)
                                            .sendToTarget();
                                    break;
                            }
                        }
                    });
                }
            }
        });
        try {
            mBeaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
