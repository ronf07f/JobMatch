package com.example.jobmatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegistrationActivity extends AppCompatActivity {


    private Button bRegister;
    private EditText mEmail, mPassword ,mName;
    private RadioGroup mRadioGroup;
    public String name,userType;
    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener firebaseAuthStateListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        init();
        listeners();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    private void init(){
        bRegister = findViewById(R.id.register);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mName = findViewById(R.id.name);
        mRadioGroup = findViewById(R.id.radioGroup);
    }

    private void listeners(){






        bRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int selectId = mRadioGroup.getCheckedRadioButtonId();
                final  RadioButton radioButton = (RadioButton)  findViewById(selectId);


                final  String email = mEmail.getText().toString();
                final  String password =mPassword.getText().toString();
                mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this, "failed to register",Toast.LENGTH_SHORT).show();
                        }else{
                            String userId = mAuth.getCurrentUser().getUid();
                            Log.i("sss",radioButton.getText().toString());
                            final FirebaseFirestore DB = FirebaseFirestore.getInstance();
                            name=mName.getText().toString();
                            userType = radioButton.getText().toString();
                            Intent intent = new Intent(RegistrationActivity.this , GetMoreInfoActivity.class);
                            intent.putExtra(GlobalVerbs.USER_NAME,name);
                            intent.putExtra(GlobalVerbs.USER_TYPE,userType);
                            startActivity(intent);




                        }
                    }
                });
            }
        });



        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null){
                    /*
                    Intent intent = new Intent(RegistrationActivity.this , GetMoreInfoActivity.class);
                    intent.putExtra(GlobalVerbs.USER_NAME,"asdfasdf");
                    intent.putExtra(GlobalVerbs.USER_TYPE,userType+"fsa");
                    startActivity(intent);*/
                    finish();
                    return;
                }
            }
        };
    }

    @Override
    protected void onStop() {
        super.onStop();
        mAuth.removeAuthStateListener(firebaseAuthStateListener);
    }
}