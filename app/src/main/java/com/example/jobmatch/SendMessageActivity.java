package com.example.jobmatch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SendMessageActivity extends AppCompatActivity {

    private EditText mEditText ;
    private TextView to;
    private TextView msgStarter;
    private Button send;
    private String matchName;
    private String matchPhone;
    private String currentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        init();
        setUpXml(matchName,currentName);
        listeners();

    }
    private void listeners(){
        send.setOnClickListener(v -> {
            Log.i("sms","send button clicked");
            String smsMsg = msgStarter.getText()+" "+mEditText.getText();
            Log.i("sms","msg"+smsMsg);
            sendSMS(matchPhone,smsMsg);
        });

    }
    private void init(){
        mEditText = (EditText) findViewById(R.id.msg);
        to = findViewById(R.id.to);
        send= findViewById(R.id.send);
        msgStarter = findViewById(R.id.msgStarter);
        matchName = getIntent().getStringExtra(GlobalVerbs.OTHER_USER_NAME);
        currentName = getIntent().getStringExtra(GlobalVerbs.USER_NAME);
        matchPhone = getIntent().getStringExtra(GlobalVerbs.USER_PHONE);
    }
    private void setUpXml(String name_to,String name_this){

        // Initialize a new GradientDrawable instance
        GradientDrawable gd = new GradientDrawable();
        // Set the gradient drawable background to transparent
        gd.setColor(Color.parseColor("#00ffffff"));
        // Set a border for the gradient drawable
        gd.setStroke(2,Color.BLACK);
        // Finally, apply the gradient drawable to the edit text background
        mEditText.setBackground(gd);
        to.setText("To:"+name_to);
        msgStarter.setText("you got a new message form your match "+name_this+" the massage is:");


    }
    private void sendSMS(String phone,String msg){
        Log.i("sms","sendSMS");
        try {
            SmsManager smsManager =SmsManager.getDefault();
            smsManager.sendTextMessage(phone,null,msg,null,null);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}