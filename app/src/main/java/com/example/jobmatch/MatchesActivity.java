package com.example.jobmatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;

public class MatchesActivity extends AppCompatActivity {

    private ArrayList<MatchesObj> matchesList;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        matchesList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);

        setUserInfo();
        setAdapter();
    }

    private void setAdapter() {
       MatchesAdapter adapter = new MatchesAdapter(matchesList);
       RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
       recyclerView.setLayoutManager(layoutManager);
       recyclerView.setItemAnimator(new DefaultItemAnimator());
       recyclerView.setAdapter(adapter);
    }

    private void setUserInfo() {
        matchesList.add(new MatchesObj("fsdfsdf"));
        matchesList.add(new MatchesObj("asdf"));
        matchesList.add(new MatchesObj("asdfasdf"));

    }
}