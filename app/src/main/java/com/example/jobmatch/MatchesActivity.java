package com.example.jobmatch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MatchesActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private  RecyclerView.Adapter matchesAdapter;
    private RecyclerView.LayoutManager matchesLayoutManager;

    private ArrayList<MatchesObject> resultMatches = new ArrayList<MatchesObject>();


    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matches);
        Log.i("text","MatchesActivity");
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        matchesLayoutManager = new LinearLayoutManager(MatchesActivity.this);
        recyclerView.setLayoutManager(matchesLayoutManager);
        matchesAdapter = new MatchesAdapter(geDataSetMatches(),MatchesActivity.this);
        recyclerView.setAdapter(matchesAdapter);

        /*
        for (int i=0;i<100;i++){
            MatchesObject obj = new MatchesObject("asdasd");
            resultMatches.add(obj);
            matchesAdapter.notifyDataSetChanged();
        }*/
        MatchesObject obj = new MatchesObject("asdasd");
        Log.i("text","sdf" + obj.getUserId());
        resultMatches.add(obj);
        resultMatches.add(obj);
        resultMatches.add(obj);
        matchesAdapter.notifyDataSetChanged();



    }
    MatchesObject obj = new MatchesObject("aasdfasdfasd");

    private ArrayList<MatchesObject> geDataSetMatches() {
        Log.i("text","geDataSetMatches");
        Log.i("text", resultMatches.toString());
        return resultMatches;

    }
}