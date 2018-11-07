package com.example.gabu.datn_bluetooth;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by gabu on 5/9/2016.
 */
public class FirebaseService extends Thread {
    private Firebase mFirebase;
    private Handler mHandler;

    public FirebaseService(Context context, Handler handler) {
        Firebase.setAndroidContext(context);
        mFirebase = new Firebase("https://researchcar.firebaseio.com/");
        mHandler = handler;
    }

    public void write(String field, String value) {
        mFirebase.child("update").child(field).setValue(value);
    }

    public void write(String field, String child, String value) {
        mFirebase.child("update").child(field).child(child).setValue(value);
    }

    public void run() {
        mFirebase.child("update").child("Control").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String msg = dataSnapshot.getValue() + "";
                mHandler.obtainMessage(MainActivity.FIREBASE_CONTROL, msg);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        mFirebase.child("update").child("State").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int x = Integer.parseInt(dataSnapshot.getValue() + "");
                mHandler.obtainMessage(MainActivity.FIREBASE_STATE, x).sendToTarget();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }
}
