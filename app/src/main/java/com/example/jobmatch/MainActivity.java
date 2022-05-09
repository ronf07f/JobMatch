package com.example.jobmatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity extends AppCompatActivity {

    private Cards cards_data[];
    private CardsAdapter arrayAdapter;
    private FirebaseAuth mAuth;
    private String currentId;
    private String oppositeUserType;

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
                data.put( currentId,true);
                DB.collection(oppositeUserType).document(userId).collection("connections").document("nope").set(data);
                Toast.makeText(MainActivity.this, "Left!",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRightCardExit(Object dataObject) {
                Cards card = (Cards) dataObject;
                String userId = card.getUserId();
                Map<String,Object> data = new HashMap<>();
                data.put( currentId,true);
                DB.collection(oppositeUserType).document(userId).collection("connections").document("yep").set(data);
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

    private  String userType;

    public void checkUserType() {
        loading.setVisibility(View.VISIBLE);
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore DB = FirebaseFirestore.getInstance();
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

    }

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
                            final DocumentReference nopeRef= oppositeTypeUsersDB.document(document.getId()).collection("connections").document("nope");
                            final DocumentReference yepRef= oppositeTypeUsersDB.document(document.getId()).collection("connections").document("yep");
                            nopeRef.get().addOnSuccessListener(documentSnapshot -> {
                                if (documentSnapshot.exists()){
                                    Map<String,Object>nope = documentSnapshot.getData();
                                    Log.i("snap","current id : "+(String) currentId);
                                    if(!(boolean) nope.get(currentId)){
                                        yepRef.get().addOnSuccessListener(documentSnapshot -> {
                                            if (documentSnapshot.exists()){
                                                Map<String,Object>yep = documentSnapshot.getData();
                                                if(!(boolean) yep.get(currentId)){
                                                    Log.i("snap", (String) document.getId());
                                                    Cards item = new Cards((String) document.getId(), (String) data.get("name"));
                                                    rowItems.add(item);
                                                    arrayAdapter.notifyDataSetChanged();
                                                }
                                            }
                                        });
                                    }
                                }
                            });



                        }
                    }
                }
            }
        });

    }


    public void logoutUser(View view) {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this,WelcomeActivity.class);
        startActivity(intent);
        finish();
        return;
    }
}
