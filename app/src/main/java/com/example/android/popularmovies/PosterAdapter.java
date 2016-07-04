package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Rodrigo Souza on 02/07/2016.
 */
public class PosterAdapter extends ArrayAdapter<String> {

    public PosterAdapter(Context context, ArrayList<String> posterAddressList){
        super(context, 0, posterAddressList);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the poster address
        String posterAddress = getItem(position);

        // Inflate a new grid item layout
        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item, parent, false);
        }

        // Grab ImageView to be populated
        ImageView posterView = (ImageView) convertView.findViewById(R.id.grid_item_imageview);
        posterView.setAdjustViewBounds(true);
        posterView.setPadding(0, 0, 0, 0);


        // Populate image View, using Picasso library to download
        Picasso.with(getContext()).load(posterAddress).into(posterView);

        //return view
        return convertView;

    }
}
