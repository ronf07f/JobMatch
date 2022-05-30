package com.example.jobmatch;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class CheckInternetBroadcastReceiver extends BroadcastReceiver {
    /**
     * show an alert if there is no internet connection and remove the alert when the connection is back
     *
     * @param context
     * @param intent
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        final AlertDialog noInternetAlert = BaseActivity.noInternetAlert;
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
            boolean noConnectivity =intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
            if(noConnectivity) {
                Log.i(GlobalVerbs.TAG, "NO INTERNET");
                noInternetAlert.show();
            }else{
                Log.i(GlobalVerbs.TAG, "yes INTERNET");
                noInternetAlert.cancel();
            }
        }
    }
}
