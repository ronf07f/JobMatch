package com.example.jobmatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
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


public class MainActivity extends AppCompatActivity {

    private Cards cards_data[];
    private CardsAdapter arrayAdapter;
    private FirebaseAuth mAuth;
    private String currentId;
    private String oppositeUserType;
    private  String userType;

    private ProgressBar loading;

    private  FirebaseFirestore DB ;
    ListView listView;
    List<Cards> rowItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DB = FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_main);
        loading = findViewById(R.id.progressBar);
        mAuth = FirebaseAuth.getInstance();
        checkUserType();
        rowItems = new ArrayList<Cards>();
       // al.add("php");
      //  Log.i("snap",oppositeUserType + "");
        arrayAdapter = new CardsAdapter(this, R.layout.item, rowItems );
        SwipeFlingAdapterView flingContainer = (SwipeFlingAdapterView) findViewById(R.id.frame);
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
                View view = flingContainer.getSelectedView();
                //view.findViewById(R.id.item_swipe_right_indicator).setAlpha(scrollProgressPercent < 0 ? -scrollProgressPercent : 0);
                //view.findViewById(R.id.item_swipe_left_indicator).setAlpha(scrollProgressPercent > 0 ? scrollProgressPercent : 0);
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener(new SwipeFlingAdapterView.OnItemClickListener() {
            @Override
            public void onItemClicked(int itemPosition, Object dataObject) {
                Toast.makeText(MainActivity.this, "Clicked!",Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.my_menu,menu);
        return true;
    }
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
        currentUserYeps.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot snapshot = task.getResult();
                    if(snapshot.exists()){
                        Log.i("snap",(String) userId);
                        Map<String,Object> data = new HashMap<>();
                        data.put("match","match");
                        DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentId).collection(GlobalVerbs.MATCH).document(userId).set(data);
                        DB.collection(GlobalVerbs.USERS_COLLECTION).document(userId).collection(GlobalVerbs.MATCH).document(currentId).set(data);
                        Toast.makeText(MainActivity.this, "Match",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }



    public void checkUserType() {
        loading.setVisibility(View.VISIBLE);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        currentId = user.getUid();
         DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
             @Override
             public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                 if(task.isSuccessful()){
                     userType = task.getResult().getString(GlobalVerbs.USER_TYPE);
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
             }
         });
    }
    /*
    public void checkUserTypeold() {
        loading.setVisibility(View.VISIBLE);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
       // final FirebaseFirestore DB = FirebaseFirestore.getInstance();
        final CollectionReference employeeCollectionRef = DB.collection("Employee");
        final CollectionReference employerCollectionRef = DB.collection("Employer");
        currentId = user.getUid();
        DocumentReference doc = employeeCollectionRef.document(user.getUid());
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Log.i("snap", "Employee");
                        userType = "Employee";
                        oppositeUserType = "Employer";
                        getOppositeTypeUsers();
                        loading.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
        doc = employerCollectionRef.document(user.getUid());
        doc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot snapshot = task.getResult();
                    if (snapshot.exists()) {
                        Log.i("snap", "Employer");
                        userType = "Employer";
                        oppositeUserType = "Employee";
                        getOppositeTypeUsers();
                        loading.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });

    }*/
    //show cards of opposite type that the user did not watch before
    public void findOppositeCards(){
        DB.collection(GlobalVerbs.USERS_COLLECTION).whereEqualTo(GlobalVerbs.USER_TYPE,oppositeUserType).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot snapshot = task.getResult();
                    if(!snapshot.isEmpty()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                          //  Map<String,Object>data = document.getData();
                            //check if user saw this card before
                            DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentId).collection(GlobalVerbs.WATCHED).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        List<String> watched = new ArrayList<>();
                                        for (QueryDocumentSnapshot doc : task.getResult()) {
                                            watched.add(doc.getId());
                                        }
                                        Log.i("snap", watched.toString());
                                        if (!watched.contains(document.getId())) {
                                            showUserOnCard((String)document.getId(),document.getString(GlobalVerbs.USER_NAME),document.getString(GlobalVerbs.PROFILE_IMAGE_URL));
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }
    /*
   public void getOppositeTypeUsers(){
        final FirebaseFirestore DB = FirebaseFirestore.getInstance();
        final CollectionReference oppositeTypeUsersDB = DB.collection(oppositeUserType);
        oppositeTypeUsersDB.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    QuerySnapshot snapshot = task.getResult();
                    if(!snapshot.isEmpty()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Map<String,Object>data = document.getData();
                            //check if user saw this card before
                            DB.collection(userType).document((String)currentId).collection("watched").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if(task.isSuccessful()){
                                        List<String> watched= new ArrayList<>();
                                        for(QueryDocumentSnapshot doc : task.getResult()){
                                            watched.add(doc.getId());
                                        }
                                        Log.i("snap",watched.toString());
                                        if(!watched.contains(document.getId())){
                                            showUserOnCard((String)document.getId(),(String) data.get("name"));
                                        }
                                    }
                                }
                            });
                        }
                    }
                }
            }
        });
    }

     */


    public void showUserOnCard(String id,String name,String profileImageUrl){
        Cards item = new Cards(id,name,profileImageUrl);
        rowItems.add(item);
        arrayAdapter.notifyDataSetChanged();
    }


    public void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
        startActivity(intent);
        finish();
        return;
    }
    public void openSettings() {
        Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
        intent.putExtra("userType",userType);
        startActivity(intent);
        return;

    }
    public void openMatches() {
        Intent intent = new Intent(MainActivity.this,MatchesActivity.class);
        startActivity(intent);
        return;

    }
}
