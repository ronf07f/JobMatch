package com.example.jobmatch;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.lorentzos.flingswipe.SwipeFlingAdapterView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {



    private CardsAdapter arrayAdapter;
    private FirebaseAuth mAuth;
    private String currentId;
    private String oppositeUserType;
    private  String userType;
    private Users thisUser;
    private ProgressBar loading;

    private  FirebaseFirestore DB ;
    List<Cards> rowItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onCreate");
        Intent intent = new Intent(MainActivity.this,MatchCheckService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        }
        DB = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_main);
        loading = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        checkUserType();
        rowItems = new ArrayList<>();
        arrayAdapter = new CardsAdapter(this, R.layout.item, rowItems );
        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"removeFirstObjectInAdapter");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {
                Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onLeftCardExit");
                Cards card = (Cards) dataObject;
                String userId = card.getUserId();
                Map<String,Object> data = new HashMap<>();
                data.put("swiped","swiped");
                DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentId).collection(GlobalVerbs.WATCHED).document(userId).set(data);
                DB.collection(GlobalVerbs.USERS_COLLECTION).document(userId).collection(GlobalVerbs.SWIPED_LEFT).document(currentId).set(data);
                Toast.makeText(MainActivity.this, "Left!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onRightCardExit");
                Cards card = (Cards) dataObject;
                String userId = card.getUserId();
                Map<String,Object> data = new HashMap<>();
                data.put("swiped","swiped");
                DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentId).collection(GlobalVerbs.WATCHED).document(userId).set(data);
                DB.collection(GlobalVerbs.USERS_COLLECTION).document(userId).collection(GlobalVerbs.SWIPED_RIGHT).document(currentId).set(data);
                isMatched(userId);

                Toast.makeText(MainActivity.this, "Right!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdapterAboutToEmpty(int itemsInAdapter) {
                // Ask for more data here;


                Log.d("LIST", "notified");

            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> Toast.makeText(MainActivity.this, "Clicked!",Toast.LENGTH_SHORT).show());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onCreateOptionsMenu");
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
        }
        return true;
    }

    private void isMatched(String userId) {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"isMatched");
        DocumentReference currentUserYeps = DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentId).collection(GlobalVerbs.SWIPED_RIGHT).document(userId);
        currentUserYeps.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DocumentSnapshot snapshot = task.getResult();
                if(snapshot.exists()){
                    Log.i("snap", userId);
                    Map<String,Object> data = new HashMap<>();
                    data.put("match","match");
                    DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentId).collection(GlobalVerbs.MATCH).document(userId).set(data);
                    DB.collection(GlobalVerbs.USERS_COLLECTION).document(userId).collection(GlobalVerbs.MATCH).document(currentId).set(data);
                    Toast.makeText(MainActivity.this, "Match",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void checkUserType() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"checkUserType");
        loading.setVisibility(View.VISIBLE);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        currentId = user.getUid();
        DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentId).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){

               Log.i("dog","ts");
               @SuppressWarnings("unchecked")
               Map<String,Object> userInfo = (Map<String, Object>) Objects.requireNonNull(task.getResult().getData()).get("user");
               assert userInfo != null;
               Log.i("dog",userInfo.toString());
               thisUser = new Users(userInfo);
               userType = thisUser.getUserType();
               Log.i("dog","userType: " + userType);
               switch (userType){
                   case GlobalVerbs.EMPLOYEE:
                       oppositeUserType = GlobalVerbs.EMPLOYER;
                       break;
                   case GlobalVerbs.EMPLOYER:
                       oppositeUserType = GlobalVerbs.EMPLOYEE;
                       break;
               }
               findOppositeCards();
               loading.setVisibility(View.INVISIBLE);
           }
        });

    }

    //show cards of opposite type that the user did not watch before
    public void findOppositeCards(){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"findOppositeCards");
        DB.collection(GlobalVerbs.USERS_COLLECTION).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"task.isSuccessful");
                QuerySnapshot snapshot = task.getResult();
                if(!snapshot.isEmpty()){
                    Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"!snapshot.isEmpty");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object>data = (Map<String, Object>) document.getData().get(GlobalVerbs.USERS_USER);
                        assert data != null;
                        Users tempUser = new Users(data);
                        if (tempUser.getUserType().equals(oppositeUserType)){
                            DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentId).collection(GlobalVerbs.WATCHED).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    List<String> watched = new ArrayList<>();
                                    for (QueryDocumentSnapshot doc : task1.getResult()) {
                                        watched.add(doc.getId());
                                    }
                                    if (!watched.contains(document.getId())) {
                                        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"!watched");
                                        showUserOnCard(tempUser,document.getId());
                                    }
                                }
                            });}
                    }
                }
            }
        });
    }

    public void showUserOnCard(Users oppositeUser,String id){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"showUserOnCard");
        Cards item = new Cards(oppositeUser,id);
        rowItems.add(item);
        arrayAdapter.notifyDataSetChanged();
    }


    public void logoutUser() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"logoutUser");
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
    public void openSettings() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"openSettings");
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        intent.putExtra(GlobalVerbs.USER_TYPE,userType);
        startActivity(intent);

    }
    public void openMatches() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"openMatches");
        Intent intent = new Intent(MainActivity.this,MatchesActivity.class);
        startActivity(intent);

    }
}
