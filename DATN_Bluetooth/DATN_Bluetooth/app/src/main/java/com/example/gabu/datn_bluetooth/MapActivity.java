package com.example.gabu.datn_bluetooth;

/**
 * Created by Buixu on 25/05/2016.
 */
import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.annotation.TargetApi;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Build;
import android.os.RemoteException;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.util.Collection;
import java.util.List;
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class MapActivity extends AppCompatActivity implements BeaconConsumer
{
    RelativeLayout.LayoutParams params;
    public static final String TAG = "BeaconsEverywhere";
    private BeaconManager beaconManager;
    public  TextView mTextView_UUID,mTextView_Major,mTextView_Minor,mTextView_Address,mTextView_Khoangcach,mTextViewAxisX,mTextViewAxisY,mtextView_beacons1local,mtextView_beacons2local,mtextView_beacons3local,mtextView_beacons4local,mtextView_beacons5local,mtextView_beacons6local;
    private ImageView mImBeacons1,mImBeacons2,mImBeacons3,mImBeacons4,mImBeacons5,mImBeacons6;
    private String UUID,Major,Minor,Adddress,mKhoangcach_string;
    private float mKhoangcach;

    private float DonviX=644/4.5f, DonviY=680/6.5f,VitrimuctieuthucteX,VitrimuctieuthucteY ;
    private int RSSI, mRSSI ,ImMucTieuTouchX=10,ImMuctieuTouchY=10;
    private float AndroidX=0,AndroidY=0,AndroidX_old=0,AndroidY_old=0,Txpower;
    private float d1=0,d2=0,d3=0,d4=0,d5=0,d6=0,d1_old=0,d2_old=0,d3_old=0,d4_old=0,d5_old=0,d6_old=0;
    private float bea1X=0,bea1Y=0,bea2X=0,bea2Y=0,bea3X=0,bea3Y=0,bea4X=0,bea4Y=0,bea5X=0,bea5Y=0,bea6X=0,bea6Y=0;
    private String RadBeacons[]={"bea1","bea2","bea3","bea4","bea5","bea6"};
    private float D[]={d1,d2,d3,d4,d5,d6};
    private float D_old[]={d1_old,d2_old,d3_old,d4_old,d5_old,d6_old};
    RelativeLayout layout;
    ImageView mImageView,mImageViewMuctieu,mImageViewDanHuong;
    BluetoothLeScanner mBluetoothLeScanner;
    ScanFilter mScanFilter;
    ScanSettings mScanSettings;
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter ();
        Context context = null;


        beaconManager = BeaconManager.getInstanceForApplication(this);


        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout("x,s:0-1=feaa,m:2-2=20,d:3-3,d:4-5,d:6-7,d:8-11,d:12-15"));

        beaconManager.bind(this);
        params = new RelativeLayout.LayoutParams ( RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layout = (RelativeLayout)findViewById( R.id.Layout2 );
        mImageViewDanHuong=(ImageView)findViewById(R.id.imageButton_huong);
        mImageView = new ImageView ( this );

        mImageViewMuctieu=new ImageView(this);
        layout.addView(mImageViewMuctieu);
        layout.addView( mImageView );

    }
    // cai dat zoom in, zoom out cho ban do_______
/*    private class TouchHandler implements View.OnTouchListener
    {
        @Override
        public boolean onTouch(View view, MotionEvent event)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {
                params.leftMargin = Math.round(event.getX() * 160f / getBaseContext().getResources().getDisplayMetrics().densityDpi);
                params.topMargin = Math.round(event.getY() * 160f / getBaseContext().getResources().getDisplayMetrics().densityDpi);
                layout.setLayoutParams(params);
                layout.setVisibility( View.VISIBLE);
              //  layout.setPivotX(event.getX());
              //  layout.setPivotY(event.getY());
                layout.setScaleX(5f);
                layout.setScaleY(5f);
            }
            else {
                if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    params.leftMargin = Math.round(event.getX() * 160f / getBaseContext().getResources().getDisplayMetrics().densityDpi);
                    params.topMargin = Math.round(event.getY() * 160f / getBaseContext().getResources().getDisplayMetrics().densityDpi);
                   layout.setLayoutParams(params);
                    layout.setVisibility( View.VISIBLE);
                 //   layout.setPivotX(event.getX());
                  //  layout.setPivotY(event.getY());
                    layout.setScaleX(1f);
                    layout.setScaleY(1f);

                }
            }
            return true;
        }
    }*/
    //________________________________________________
    public void getupGUI(){
        mTextView_UUID=(TextView)findViewById (R.id.textView_UUID);
        mTextView_Major=(TextView) findViewById (R.id.textView_Major);
        mTextView_Minor=(TextView)findViewById (R.id.textView_Minor);
        mTextView_Address=(TextView)findViewById (R.id.textView_Address);
        mTextView_Khoangcach=(TextView)findViewById (R.id.textView_Khoangcach);
        mtextView_beacons1local=(TextView)findViewById ( R.id.textView_beacons1local );
        mtextView_beacons2local=(TextView)findViewById ( R.id.textView_beacons2loacal );
        mTextViewAxisX=(TextView)findViewById ( R.id.textView_AxisX );
        mTextViewAxisY=(TextView)findViewById ( R.id.textView_AxisY );
        mImBeacons1=(ImageView)findViewById(R.id.imageView_beacons1);
        mImBeacons2=(ImageView)findViewById(R.id.imageView_beacons2);
    }

    public void setupGUI (){
        mTextView_UUID.setText(""+UUID);
        mTextView_Major.setText(""+Major);
        mTextView_Minor.setText(""+Minor);
        mTextView_Address.setText(""+Adddress);
        mTextView_Khoangcach.setText(""+mKhoangcach_string);
    }
    public double getDistance(int rssi, float txPower) {
    /*
     * RSSI = TxPower - 10 * n * lg(d)
     * n = 2 (in free space)

     * d = 10 ^ ((TxPower - RSSI) / (10 * n))
     */
        return Math.pow(10d, ((double) txPower - rssi) / (10 * 2));
    }
    public void getlocal(){// cai dat vi tri cho beacon1 va beacon 2
        float bea1X_on_Screnn,bea1Y_on_Screen,bea2X_on_Screen,bea2Y_on_Screen;
        bea1X_on_Screnn=mImBeacons1.getX ();
        bea1Y_on_Screen=mImBeacons1.getY ();
        bea2X_on_Screen=mImBeacons2.getX ();
        bea2Y_on_Screen=mImBeacons2.getY ();
        bea1X=bea1X_on_Screnn/DonviX;
        bea1Y=bea1Y_on_Screen/DonviY;
        bea2X=bea2X_on_Screen/DonviX;
        bea2Y=bea2Y_on_Screen/DonviY;

    }
    public void WriteLogFile(String text1,String text2,String logfileD1,String logfileRSSI1)
    {

        File logFile1 = new File(logfileD1);
        File logFile2 = new File(logfileRSSI1);
        if (!logFile1.exists())
        {
            try
            {
                logFile1.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (!logFile2.exists())
        {
            try
            {
                logFile2.createNewFile();
            }
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        try
        {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf1 = new BufferedWriter(new FileWriter(logFile1, true));
            buf1.append(text1);
            buf1.newLine();
            buf1.close();

            BufferedWriter buf2 = new BufferedWriter(new FileWriter(logFile2, true));
            buf2.append(text2);
            buf2.newLine();
            buf2.close();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    public void setLocalAndroid() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mImageView.setBackground (getResources().getDrawable(R.drawable.shape2) );
            mImageViewMuctieu.setBackground(getResources().getDrawable(R.drawable.shape3));
        }

        AndroidY_old = (float) ((Math.pow ( d1_old, 2 ) - Math.pow ( d2_old, 2 ) + Math.pow ( bea2Y, 2 ) - Math.pow ( bea1Y, 2 )) / (2 * (bea2Y - bea1Y)));
        float AndroidX_old_trunggian = (float) (Math.pow ( d2_old, 2 ) - ((Math.pow ( d1_old, 2 ) - Math.pow ( d2_old, 2 )) / (2 * (bea2Y - bea1Y))) + (bea2Y - bea1Y) / 2);

        if (AndroidX_old_trunggian >= 0) {
            AndroidX_old = (float) (Math.sqrt ( AndroidX_old_trunggian ));
        }

        AndroidY = (float) ((Math.pow ( d1, 2 ) - Math.pow ( d2, 2 ) + Math.pow ( bea2Y, 2 ) - Math.pow ( bea1Y, 2 )) / (2 * (bea2Y - bea1Y)));
        float AndroidX_trunggian = (float) (Math.pow ( d2, 2 ) - ((Math.pow ( d1, 2 ) - Math.pow ( d2, 2 )) / (2 * (bea2Y - bea1Y))) + (bea2Y - bea1Y) / 2);
        if (AndroidX_trunggian >= 0) {
            AndroidX = (float) (Math.sqrt ( AndroidX_trunggian ));
        }
        mImageView.setX (AndroidX * DonviX );
        mImageView.setY (AndroidY * DonviY );

        // so sánh gia tri toa do cua muc tieu so voi vi tri hiện tại de hiển thị mũi tên điều hướng
        float ImmuctieuX=ImMucTieuTouchX/DonviX;
        float ImmuctieuY=(ImMuctieuTouchY-200)/DonviY;
        if((ImmuctieuY<AndroidY)&&(Math.abs(ImmuctieuX-AndroidX)<Math.abs(ImmuctieuY-AndroidY)))
        {
            mImageViewDanHuong.setBackground(getResources().getDrawable(R.drawable.muiten_len));
        }
        if((ImmuctieuY>AndroidY)&&(Math.abs(ImmuctieuX-AndroidX)<Math.abs(ImmuctieuY-AndroidY)))
        {
            mImageViewDanHuong.setBackground(getResources().getDrawable(R.drawable.muiten_xuong));
        }
        if((ImmuctieuX<AndroidX)&&(Math.abs(ImmuctieuX-AndroidX)>Math.abs(ImmuctieuY-AndroidY)))
        {
            mImageViewDanHuong.setBackground(getResources().getDrawable(R.drawable.muiten_trai));
        }
        if((ImmuctieuX>AndroidX)&&(Math.abs(ImmuctieuX-AndroidX)>Math.abs(ImmuctieuY-AndroidY)))
        {
            mImageViewDanHuong.setBackground(getResources().getDrawable(R.drawable.muiten_phai));
        }

        //___________________

        mTextViewAxisX.setText ( "" + AndroidX );
        mTextViewAxisY.setText ( "" + AndroidY );

    }


    public void delaylocal(){
        mtextView_beacons1local.setText ( "Bea1: X="+  bea1X +"\t Y="+bea1Y);
        mtextView_beacons2local.setText ( "Bea2: X="+  bea2X +"\t Y="+bea2Y);

    }

    public boolean dispatchTouchEvent(MotionEvent event) {
        int eventaction=event.getAction();
        ImMucTieuTouchX = (int)event.getX();
        ImMuctieuTouchY = (int)event.getY();
        switch(eventaction) {
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            default:
                break;
        }
        mImageViewMuctieu.setX(ImMucTieuTouchX);
        mImageViewMuctieu.setY(ImMuctieuTouchY-200);
        VitrimuctieuthucteX=ImMucTieuTouchX/DonviX;
        VitrimuctieuthucteY=(ImMuctieuTouchY-200)/DonviY;
        Log.d(TAG, "TouchX="+ VitrimuctieuthucteX+"m \n TouchY="+VitrimuctieuthucteY+"m");
        return super.dispatchTouchEvent(event);
    }
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind((BeaconConsumer) this);
        // this.finish();
        //  System.exit(0);
    }
    public float d1(){
        return d1;
    }
    public float d2(){
        return d2;
    }
    @Override
    public void onBeaconServiceConnect() {
        final org.altbeacon.beacon.Region region = new org.altbeacon.beacon.Region("myBeaons", null, null, null);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(org.altbeacon.beacon.Region region) {
                try {
                    Log.d(TAG, "didEnterRegion");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(org.altbeacon.beacon.Region region) {
                try {
                    Log.d(TAG, "didExitRegion");
                    beaconManager.stopRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, org.altbeacon.beacon.Region region) {

            }

        });
        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, org.altbeacon.beacon.Region region) {
                for(Beacon oneBeacon : beacons) {

                    UUID=oneBeacon.getId1 ().toString();
                    Major=oneBeacon.getId2 ().toString ();
                    Minor=oneBeacon.getId3 ().toString ();
                    mKhoangcach= ( float ) oneBeacon.getDistance ();
                    Adddress=oneBeacon.getBluetoothAddress ();
                    Txpower=oneBeacon.getTxPower();
                    RSSI=oneBeacon.getRssi();
                    List<Long> a=oneBeacon.getDataFields();

                    getupGUI();



                    Log.d(TAG, "distance: " + oneBeacon.getDistance() + " \n id:" + oneBeacon.getId1() + "\n Major" + oneBeacon.getId2() + "\n Minor" + oneBeacon.getId3()+ "\n Ten thiet bị:"+ oneBeacon.getBluetoothAddress ()+"\n Data: "+a);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            getlocal ();
                            delaylocal ();
                            String logD="\n D="+mKhoangcach;
                            String logRSSI="\n RSSI="+RSSI;
                            switch (Adddress)
                            {
                                case "00:07:80:1F:BC:BC":
                                    d1_old=d1;
                                    d1=mKhoangcach;
                                    d1();
                                    D[0]=mKhoangcach;
                                    mKhoangcach_string="Bea1:"+mKhoangcach;
                                    String logfieD1="sdcard/logfileRSSI1.txt";
                                    String logfileD1="sdcard/logfileD1.txt";
                                    WriteLogFile(logD,logRSSI,logfileD1,logfieD1);
                                    break;
                                case "00:07:80:1F:A9:63":
                                    d2_old=d2;
                                    d2=mKhoangcach;
                                    d2();
                                    D[1]=mKhoangcach;
                                    mKhoangcach_string="Bea2:"+mKhoangcach;
                                    String logfieD2="sdcard/logfileRSSI2.txt";
                                    String logfileD2="sdcard/logfileD2.txt";
                                    WriteLogFile(logD,logRSSI,logfileD2,logfieD2);
                                    break;

                            }

                            setupGUI();
                            setLocalAndroid ();
                        }
                    });
                }
            }
        });


        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
            //  setupGUI();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
    }

}
