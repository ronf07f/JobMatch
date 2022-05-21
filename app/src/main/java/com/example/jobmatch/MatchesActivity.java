package com.example.jobmatch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MatchesActivity extends AppCompatActivity {

    private ArrayList<MatchesObj> matchesList;
    private RecyclerView recyclerView;
    private FirebaseFirestore DB ;


    private String currentUserId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        DB = FirebaseFirestore.getInstance();
        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        matchesList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        getMatchesID();


    }

    private void getMatchesID() {
        CollectionReference matchesRef = DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentUserId).collection(GlobalVerbs.MATCH);
        matchesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (QueryDocumentSnapshot doc : task.getResult()) {
                        Log.i("fff","getMatchesID");
                        addMatchToRecycler(doc.getId());
                    }
                }
            }
        });
    }

    private void addMatchToRecycler(String matchId) {

        DocumentReference userDB = DB.collection(GlobalVerbs.USERS_COLLECTION).document(matchId);
        userDB.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.i("fff","secces");
                    String name = task.getResult().getString(GlobalVerbs.USER_NAME);
                    String profileImage = task.getResult().getString(GlobalVerbs.PROFILE_IMAGE_URL);
                    Log.i("fff",name);
                    matchesList.add(new MatchesObj(matchId,name,profileImage));
                    setAdapter();

                }
            }
        });

    }


    private void setAdapter() {
       MatchesAdapter adapter = new MatchesAdapter(matchesList,getApplicationContext());
       RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
       recyclerView.setLayoutManager(layoutManager);
       recyclerView.setItemAnimator(new DefaultItemAnimator());
       recyclerView.setAdapter(adapter);
    }

    private void setUserInfo() {
        matchesList.add(new MatchesObj("123123","namee","url"));


    }
}