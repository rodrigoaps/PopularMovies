package com.example.android.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detail_activity, new DetailFragment())
                    .commit();
        }
    }

    public static class DetailFragment extends Fragment {

        public DetailFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Inflate View
            View rootView = inflater.inflate(R.layout.detail_fragment, container, false);

            // Get intent
            Intent intent = getActivity().getIntent();

            // Get all the intent data
            String posterAddress = intent.getStringExtra(getString(R.string.poster_address_extra_name));
            String title = intent.getStringExtra(getString(R.string.title_extra_name));
            String synopsis = intent.getStringExtra(getString(R.string.synopsis_extra_name));
            String userRating = intent.getStringExtra(getString(R.string.user_rating_extra_name));
            String releaseDate = intent.getStringExtra(getString(R.string.release_date_extra_name));

            // Create text views and populate each one with their data
            TextView titleTextView = (TextView) rootView.findViewById(R.id.title_text_view);
            titleTextView.setText(title);
            TextView synopsisTextView = (TextView) rootView.findViewById(R.id.synopsis_text_view);
            synopsisTextView.setText(getString(R.string.synopsis_call) + synopsis);
            TextView userRatingTextView = (TextView) rootView.findViewById(R.id.user_rating_text_view);
            userRatingTextView.setText(getString(R.string.user_rating_call) +userRating + getString(R.string.user_rating_end));
            TextView releaseDateTextView = (TextView) rootView.findViewById(R.id.release_date_text_view);
            releaseDateTextView.setText(getString(R.string.release_date_call) +releaseDate);

            // Grab ImageView to be populated
            ImageView posterView = (ImageView) rootView.findViewById(R.id.poster_image_view);

            // Populate image View, using Picasso library to download
            Picasso.with(getContext()).load(posterAddress).into(posterView);
            posterView.setAdjustViewBounds(true);

            return rootView;
        }
    }
}
