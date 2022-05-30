package com.example.jobmatch;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class WelcomeActivity extends BaseActivity {
    private Button bLogin,bRegister;

    private FirebaseAuth mAuth;
    private  FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_page);
        init();
        listeners();
    }

    @Override
    protected void onStart() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onStart");
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    /**
     * insert views in to variables
     */
    private void init() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"init");
        bLogin = findViewById(R.id.login);
        bRegister = findViewById(R.id.register);
    }

    /**
     * set listeners.
     */
    private void listeners(){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"listeners");
        bLogin.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
        bRegister.setOnClickListener(v -> {
            Intent intent = new Intent(WelcomeActivity.this, RegistrationActivity.class);
            startActivity(intent);
            finish();
        });
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null){
                Intent intent = new Intent(WelcomeActivity.this , MainActivity.class);
                startActivity(intent);
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