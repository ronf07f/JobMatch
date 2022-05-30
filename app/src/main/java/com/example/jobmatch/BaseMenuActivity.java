package com.example.jobmatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.Objects;

public class BaseMenuActivity extends BaseActivity {

    private FirebaseAuth mAuth;
    private String userType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base_menu);
        init();
    }
    public void init(){
        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onCreateOptionsMenu");
        getUserType();
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onOptionsItemSelected");
        int id = item.getItemId();
        switch (id){
            case R.id.menu_logout:
                logoutUser();
                break;
            case R.id.menu_settings:
                openSettings();
                break;
            case R.id.menu_matches:
                openMatches();
                break;
            case R.id.menu_main:
                openMain();
                break;
        }
        return true;
    }

    public void logoutUser() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"logoutUser");
        mAuth.signOut();
        Intent intent = new Intent(BaseMenuActivity.this,WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
    public void openSettings() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"openSettings");
        Intent intent = new Intent(BaseMenuActivity.this,SettingsActivity.class);
        Log.i(GlobalVerbs.TAG,"user type : " + userType);
        intent.putExtra(GlobalVerbs.USER_TYPE,userType);
        startActivity(intent);
    }
    public void openMatches() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"openMatches");
        Intent intent = new Intent(BaseMenuActivity.this,MatchesActivity.class);
        startActivity(intent);

    }
    public void openMain() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"openMatches");
        Intent intent = new Intent(BaseMenuActivity.this,MainActivity.class);
        startActivity(intent);

    }

    /**
     * find the user type and saves it in to a variable
     */
    public void getUserType() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"checkUserType");
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore DB = FirebaseFirestore.getInstance();
        assert user != null;
        String currentId = user.getUid();
        DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentId).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                @SuppressWarnings("unchecked")
                Map<String,Object> userInfo = (Map<String, Object>) Objects.requireNonNull(task.getResult().getData()).get("user");
                assert userInfo != null;
                Users tempUser = new Users(userInfo);
                userType = tempUser.getUserType();

            }
        });
    }



}