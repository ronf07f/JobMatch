package com.example.jobmatch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

public class SendMessageActivity extends AppCompatActivity {

    SmsManager smsManager =SmsManager.getDefault();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);
        Log.i("sms","0546256999");
        smsManager.sendTextMessage("0586775737",null,"test",null,null);

    }

}