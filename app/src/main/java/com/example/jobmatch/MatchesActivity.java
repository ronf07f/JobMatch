package com.example.jobmatch;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

public class MatchesActivity extends BaseActivity {

    private ArrayList<MatchesObj> matchesList;
    private RecyclerView recyclerView;
    private FirebaseFirestore DB ;


    private String currentUserId ;


    private String currentUserName ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        DB = FirebaseFirestore.getInstance();
        currentUserId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        setUserName();
        matchesList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        getMatchesID();


    }

    private void setUserName(){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"setUserName");
        DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentUserId).get().addOnCompleteListener(task -> {
           if(task.isSuccessful()){
               //currentUserName= task.getResult().getString(GlobalVerbs.USER_PHONE);
               currentUserName= "abc";
           }
        });
    }

    private void getMatchesID() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"getMatchesID");
        CollectionReference matchesRef = DB.collection(GlobalVerbs.USERS_COLLECTION).document(currentUserId).collection(GlobalVerbs.MATCH);
        matchesRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {

                for (QueryDocumentSnapshot doc : task.getResult()) {
                    Log.i("dog","getMatchesID");
                    addMatchToRecycler(doc.getId());
                }
            }
        });
    }

    private void addMatchToRecycler(String matchId) {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"addMatchToRecycler");

        DocumentReference userDB = DB.collection(GlobalVerbs.USERS_COLLECTION).document(matchId);
        userDB.get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                @SuppressWarnings("unchecked")
                Map<String,Object> userInfo = (Map<String, Object>) Objects.requireNonNull(task.getResult().getData()).get("user");
                assert userInfo != null;
                Log.i("dog",userInfo.toString());
                Users tempUser = new Users(userInfo);
                Log.i("fff","success");
                String name = tempUser.getUserName();
                String profileImage = tempUser.getProfileImageUrl();
                String phone = tempUser.getPhone();
                Log.i("fff",name);
                matchesList.add(new MatchesObj(matchId,name,profileImage,phone,currentUserName));
                setAdapter();

            }
        });

    }


    private void setAdapter() {
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"setAdapter");
        MatchesAdapter adapter = new MatchesAdapter(matchesList,getApplicationContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

}