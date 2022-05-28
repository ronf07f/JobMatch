package com.example.jobmatch;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.util.Log;

public class CheckInternetBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        final AlertDialog alertDialog = BaseActivity.alertDialog;
        if(ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())){
           // Log.i(GlobalVerbs.TAG,"NO INTERNET");
            boolean noConnectivity =intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
            if(noConnectivity) {
                Log.i(GlobalVerbs.TAG, "NO INTERNET");
                alertDialog.show();
            }else{
                Log.i(GlobalVerbs.TAG, "yes INTERNET");
                alertDialog.cancel();
            }
        }
    }
}
