package com.example.jobmatch;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public  class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MyViewHolder> {

    private ArrayList<MatchesObj> matchesList ;

    public MatchesAdapter(ArrayList<MatchesObj> matchesList){
        this.matchesList = matchesList;
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder{
        private TextView matchID;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            matchID=itemView.findViewById(R.id.matchID);
        }

    }

    @NonNull
    @Override
    public MatchesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.maches_item,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MatchesAdapter.MyViewHolder holder, int position) {
        String name = matchesList.get(position).getUserid();
        holder.matchID.setText(name);
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}
