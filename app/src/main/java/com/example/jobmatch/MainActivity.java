package com.example.jobmatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;


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
                Log.d("LIST", "removed object!");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onLeftCardExit(Object dataObject) {

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
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return true;
    }
    @SuppressLint("NonConstantResourceId")
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
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
        loading.setVisibility(View.VISIBLE);
        Log.i(GlobalVerbs.TAG,"checkUserType");
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
        Log.i("dog","find");
        Log.i("dog",userType);
        DB.collection(GlobalVerbs.USERS_COLLECTION).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Log.i("dog","ts");
                QuerySnapshot snapshot = task.getResult();
                if(!snapshot.isEmpty()){
                    Log.i("dog","not empty");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        @SuppressWarnings("unchecked")
                        Map<String,Object>data = (Map<String, Object>) document.getData().get(GlobalVerbs.USERS_USER);
                        assert data != null;
                        Log.i("dog","data: "+ data);
                        Users tempUser = new Users(data);
                        Log.i("dog","temp usertype "+tempUser.getUserType());
                        Log.i("dog", "oppositeUserType  " + oppositeUserType );
                        if (tempUser.getUserType().equals(oppositeUserType)){
                            DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentId).collection(GlobalVerbs.WATCHED).get().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    List<String> watched = new ArrayList<>();
                                    for (QueryDocumentSnapshot doc : task1.getResult()) {
                                        watched.add(doc.getId());
                                    }
                                    Log.i("snap", watched.toString());
                                    if (!watched.contains(document.getId())) {
                                        Log.i("dog","show!!!!!!!");
                                        showUserOnCard(tempUser,document.getId());
                                      //  showUserOnCard((String)document.getId(),document.getString(GlobalVerbs.USER_NAME),document.getString(GlobalVerbs.PROFILE_IMAGE_URL));
                                    }
                                }
                            });}
                    }
                }else{
                    Log.i("dog","empty");
                }
            }
        });
    }

    public void showUserOnCard(Users oppositeUser,String id){
        Cards item = new Cards(oppositeUser,id);
        rowItems.add(item);
        arrayAdapter.notifyDataSetChanged();
    }


    public void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
        startActivity(intent);
        finish();
    }
    public void openSettings() {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        intent.putExtra(GlobalVerbs.USER_TYPE,userType);
        startActivity(intent);

    }
    public void openMatches() {
        Intent intent = new Intent(MainActivity.this,MatchesActivity.class);
        startActivity(intent);

    }
}
