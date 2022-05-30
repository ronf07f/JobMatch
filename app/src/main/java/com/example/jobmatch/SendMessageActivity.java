package com.example.jobmatch;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SendMessageActivity extends BaseMenuActivity {

    private EditText mEditText ;
    private TextView to;
    private TextView msgStarter;
    private Button send;
    private String matchName;
    private String matchPhone;
    private String currentName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        init();
        setUpXml(matchName,currentName);
        listeners();

    }
    public void listeners(){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"listeners");
        send.setOnClickListener(v -> {
            String smsMsg = msgStarter.getText()+" "+mEditText.getText();
            sendSMS(matchPhone,smsMsg);
        });

    }
    public void init(){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"init");
        mEditText = findViewById(R.id.msg);
        to = findViewById(R.id.to);
        send= findViewById(R.id.send);
        msgStarter = findViewById(R.id.msgStarter);
        matchName = getIntent().getStringExtra(GlobalVerbs.OTHER_USER_NAME);
        currentName = getIntent().getStringExtra(GlobalVerbs.USER_NAME);
        Log.i(GlobalVerbs.TAG,"current name : " + currentName +"match name : " +matchName);
        matchPhone = getIntent().getStringExtra(GlobalVerbs.USER_PHONE);
    }
    @SuppressLint("SetTextI18n")
    public void setUpXml(String name_to, String name_this){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"setUpXml");
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
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"sendSMS");

        try {
            SmsManager smsManager =SmsManager.getDefault();
            smsManager.sendTextMessage(phone,null,msg,null,null);
            //add msg sent alert and move to main
        }catch (Exception e){
            e.printStackTrace();
        }

    }

}