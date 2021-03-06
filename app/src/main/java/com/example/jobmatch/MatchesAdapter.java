package com.example.jobmatch;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public  class MatchesAdapter extends RecyclerView.Adapter<MatchesAdapter.MyViewHolder> {

    private ArrayList<MatchesObj> matchesList ;
    private Context context;
    private String currentName;

    public MatchesAdapter(ArrayList<MatchesObj> matchesList, Context context){
        this.matchesList = matchesList;
        this.context = context;
    }

    public class MyViewHolder extends  RecyclerView.ViewHolder  {
        private TextView matchID;
        private TextView matchName;
        private ImageView matchImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            matchID=itemView.findViewById(R.id.matchID);
            matchName = itemView.findViewById(R.id.matchName);
            matchImage = itemView.findViewById(R.id.matchProfilePic);

           /* itemView.setOnClickListener(v -> {

                Log.i("sms","item clicked");
                Intent intent = new Intent(v.getContext(),SendMessageActivity.class);
                v.getContext().startActivity(intent);


            });*/
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
        String name = matchesList.get(position).getMatchName();
        holder.matchName.setText(name);
        String id = matchesList.get(position).getMatchId();
        holder.matchID.setText(id);
        String image = matchesList.get(position).getMatchImageUrl();
        Glide.with(context).load(image).into(holder.matchImage);
        holder.itemView.setOnClickListener(v -> {

            Log.i("sms","item clicked");
            Intent intent = new Intent(v.getContext(),SendMessageActivity.class);
            intent.putExtra(GlobalVerbs.USER_NAME,matchesList.get(position).getUserName());
            intent.putExtra(GlobalVerbs.OTHER_USER_NAME,matchesList.get(position).getMatchName());
            intent.putExtra(GlobalVerbs.USER_PHONE,matchesList.get(position).getMatchPhone());

            v.getContext().startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return matchesList.size();
    }
}
