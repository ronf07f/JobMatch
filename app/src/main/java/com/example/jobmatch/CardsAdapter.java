package com.example.jobmatch;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class CardsAdapter extends ArrayAdapter<Cards> {
    public CardsAdapter(Context context,int resourceId,List<Cards> item){
        super(context,resourceId,item);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards card_item = getItem(position);
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        TextView name = convertView.findViewById(R.id.item_name);
        TextView age =  convertView.findViewById(R.id.item_age);
        TextView xp =  convertView.findViewById(R.id.xp);
        ImageView image = convertView.findViewById(R.id.image);

        name.setText(card_item.getUserNameForCard());
        age.setText(card_item.getAgeForCard());
        xp.setText(card_item.getExperienceForCard());
        Glide.with(getContext()).load(card_item.getProfileImageUrl()).into(image);
        return convertView;
    }
}
