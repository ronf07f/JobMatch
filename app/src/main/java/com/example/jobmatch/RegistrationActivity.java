package com.example.jobmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegistrationActivity extends AppCompatActivity {


    private Button bRegister;
    private EditText mEmail, mPassword ,mName;
    private RadioGroup mRadioGroup;
    public String name,userType;
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener firebaseAuthStateListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
        listeners();
    }

    @Override
    protected void onStart() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onStart");
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    private void init(){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"init");
        bRegister = findViewById(R.id.register);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mName = findViewById(R.id.name);
        mRadioGroup = findViewById(R.id.radioGroup);
    }

    private void listeners(){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"listeners");
        bRegister.setOnClickListener(v -> {

            int selectId = mRadioGroup.getCheckedRadioButtonId();
            final  RadioButton radioButton = findViewById(selectId);


            final  String email = mEmail.getText().toString();
            final  String password =mPassword.getText().toString();
            mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegistrationActivity.this, task -> {
                if(!task.isSuccessful()){
                    Toast.makeText(RegistrationActivity.this, "failed to register",Toast.LENGTH_SHORT).show();
                }else{
                    Log.i("sss",radioButton.getText().toString());
                    name=mName.getText().toString();
                    userType = radioButton.getText().toString();
                    Intent intent = new Intent(RegistrationActivity.this , GetMoreInfoActivity.class);
                    intent.putExtra(GlobalVerbs.USER_NAME,name);
                    intent.putExtra(GlobalVerbs.USER_TYPE,userType);
                    startActivity(intent);




                }
            });
        });

        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null){
                finish();

            }
        };
    }

    @Override
    protected void onStop() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onStop");
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}