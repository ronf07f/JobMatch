package com.example.jobmatch;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;

public class BaseActivity extends AppCompatActivity {
    private final CheckInternetBroadcastReceiver checkInternetBroadcastReceiver = new CheckInternetBroadcastReceiver();
    public static AlertDialog noInternetAlert;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("no internet connection")
                .setCancelable(false);
        noInternetAlert = builder.create();
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onCreate");
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(checkInternetBroadcastReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onDestroy");
        unregisterReceiver(checkInternetBroadcastReceiver);
    }
}