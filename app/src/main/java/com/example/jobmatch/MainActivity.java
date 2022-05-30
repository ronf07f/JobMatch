package com.example.jobmatch;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
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

public class MainActivity extends BaseMenuActivity {

    private CardsAdapter arrayAdapter;
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
        checkUserType();
        rowItems = new ArrayList<>();
        arrayAdapter = new CardsAdapter(this, R.layout.item, rowItems );
        SwipeFlingAdapterView flingContainer = findViewById(R.id.frame);
        flingContainer.setAdapter(arrayAdapter);
        flingContainer.setFlingListener(new SwipeFlingAdapterView.onFlingListener() {
            /**
             * remove the top card when card slides.
             */
            @Override
            public void removeFirstObjectInAdapter() {
                // this is the simplest way to delete an object from the Adapter (/AdapterView)
                Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"removeFirstObjectInAdapter");
                rowItems.remove(0);
                arrayAdapter.notifyDataSetChanged();
            }
            /**
             * add to the data base that the card was watched and slided to the left(no).
             */
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

            /**
             * add to the data base that the card was watched and slided to the right(yes).
             * call a function to check for a possible match
             */
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
                Log.d("LIST", "notified");
            }

            @Override
            public void onScroll(float scrollProgressPercent) {
            }
        });


        // Optionally add an OnItemClickListener
        flingContainer.setOnItemClickListener((itemPosition, dataObject) -> Toast.makeText(MainActivity.this, "Clicked!",Toast.LENGTH_SHORT).show());

    }

    /**
     * check if there is a match and add to the dataBase if there is a match.
     */
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
    /**
     * check the type of the user and saves it to a variable.
     */
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

    /**
     * check for all the cards of the opposite type users the has not been watched yet.
     */
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

    /**
     * show cards on the display.
     * @param oppositeUser the user it needs to show.
     * @param id the id of the user.
     */
    public void showUserOnCard(Users oppositeUser,String id){
        Log.i(GlobalVerbs.TAG,getLocalClassName()+" "+"showUserOnCard");
        Cards item = new Cards(oppositeUser,id);
        rowItems.add(item);
        arrayAdapter.notifyDataSetChanged();
    }
}
