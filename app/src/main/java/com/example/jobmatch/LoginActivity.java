package com.example.jobmatch;




import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BaseActivity {

    private Button bLogin;
    private EditText mEmail, mPassword;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        listeners();
    }


    @Override
    protected void onStart() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onStart");
        super.onStart();
        mAuth.addAuthStateListener(firebaseAuthStateListener);
    }

    private void init() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"init");
        bLogin = findViewById(R.id.login);
        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
    }

    private void listeners() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"listeners");
        bLogin.setOnClickListener(view -> {
                final String email = mEmail.getText().toString();
                final String password = mPassword.getText().toString();
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task ->  {
                        if (!task.isSuccessful()) {Toast.makeText(LoginActivity.this, "failed to login", Toast.LENGTH_SHORT).show();}
                }).addOnFailureListener(e -> Log.i(GlobalVerbs.TAG,e.toString()));
        });
        mAuth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = firebaseAuth -> {
                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
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