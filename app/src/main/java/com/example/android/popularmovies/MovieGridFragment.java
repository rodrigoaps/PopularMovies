package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.os.AsyncTask;
import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Rodrigo Souza on 02/07/2016.
 */
public class MovieGridFragment extends Fragment {

    public MovieGridFragment() {
    }


    PosterAdapter posterAdapter;
    String[] globalPosterAddressArray;

    //List of Strings, to be populated with movie titles
    ArrayList<String> titleList = new ArrayList<String>();
    //List of Strings, to be populated with movie synopsis
    ArrayList<String> synopsisList = new ArrayList<String>();
    //List of Strings, to be populated with user Ratings
    ArrayList<String> userRatingList = new ArrayList<String>();
    //List of Strings, to be populated with release dates
    ArrayList<String> releaseDateList = new ArrayList<String>();

    @Override
    public void onStart() {
        super.onStart();

        updateMovieGrid();

    }

    private void updateMovieGrid() {

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(this.getString(R.string.pref_key_sort_by), this.getString(R.string.pref_default_value_sort_by));

        FetchMoviesTask refreshTask = new FetchMoviesTask();
        refreshTask.execute(sortBy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.movie_grid_fragment, null);
        GridView gridView = (GridView) rootView.findViewById(R.id.gridview_movies);

        ArrayList<String> posterAddressList = new ArrayList<>();
        if(posterAddressList != null)
        {
            posterAdapter = new PosterAdapter(getActivity(), posterAddressList);
            gridView.setAdapter(posterAdapter);
        }

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Context context = getActivity();

                // Get the day from the list
                String posterAddress = globalPosterAddressArray[position];
                String title = titleList.get(position);
                String synopsis = synopsisList.get(position);
                String userRating = userRatingList.get(position);
                String releaseDate = releaseDateList.get(position);

                // Intent to send to Detail Activity
                Intent detailIntent = new Intent(context, DetailActivity.class);

                // Add all the info as extras
                detailIntent.putExtra(getString(R.string.poster_address_extra_name), posterAddress);
                detailIntent.putExtra(getString(R.string.title_extra_name), title);
                detailIntent.putExtra(getString(R.string.synopsis_extra_name), synopsis);
                detailIntent.putExtra(getString(R.string.user_rating_extra_name), userRating);
                detailIntent.putExtra(getString(R.string.release_date_extra_name), releaseDate);

                // Go intent!
                startActivity(detailIntent);

            }
        });

        return rootView;
    }


    public class FetchMoviesTask extends AsyncTask<String, Void, String[]> {

        private String[] getMovieInfoFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "results";

            // JSON Array with information about the movies
            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray resultsArray = moviesJson.getJSONArray(OWM_LIST);

            //List of Strings, to be populated with poster URLs
            ArrayList<String> posterAddressList = new ArrayList<String>();

            // Declare variables to be used
            String posterAddress, title, synopsis, userRating, releaseDate;

            // Clear all lists
            if(!(titleList.isEmpty())){ titleList.clear(); }
            if(!(synopsisList.isEmpty())){ synopsisList.clear(); }
            if(!(userRatingList.isEmpty())){ userRatingList.clear(); }
            if(!(releaseDateList.isEmpty())){ releaseDateList.clear(); }

            for (int i = 0; i < resultsArray.length(); i++) {

                // Prepare Address
                posterAddress = "http://image.tmdb.org/t/p/w185";

                // Get the JSON object representing the movie
                JSONObject movie = resultsArray.getJSONObject(i);

                // Add poster path to the address
                String posterPath = movie.getString("poster_path");
                posterAddress += posterPath;

                //Add address to the list
                posterAddressList.add(posterAddress);


                // Get other info
                title = movie.getString("original_title");
                synopsis = movie.getString("overview");
                userRating = movie.getString("vote_average");
                releaseDate = movie.getString("release_date");
                title = movie.getString("original_title");

                // Pass the other info into their respective lists
                titleList.add(title);
                synopsisList.add(synopsis);
                userRatingList.add(userRating);
                releaseDateList.add(releaseDate);

            }

            String[] posterAddressArray = (String[]) posterAddressList.toArray(new String[posterAddressList.size()]);

            return posterAddressArray;

        }

        @Override
        protected void onPostExecute(String[] result) {
            // set global array to result
            globalPosterAddressArray = result;

            // add result elements to adapter
            if (result != null) {
                posterAdapter.clear();
                for (String posterAddress : result) {
                    posterAdapter.add(posterAddress);
                }
                // New data is back from the server.  Hooray!
            }
        }

        @Override
        public String[] doInBackground(String... params) {

            // Declare Connection
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the JSON response as a string.
            String moviesJsonStr = null;

            //API Key
            final String MOVIES_DB_KEY = "";

            try {
                // Construct the URL for the MoviesDB query
                final String BASE_URL = "https://api.themoviedb.org/3/movie";
                final String KEY_CALL = "api_key";

                Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                        .appendPath(params[0])
                        .appendQueryParameter(KEY_CALL, MOVIES_DB_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                // Create the request, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }

                moviesJsonStr = buffer.toString();

            } catch (IOException e) {
                // If the code didn't successfully get the data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        android.util.Log.e("FetchMoviesTask", "Error closing stream", e);
                    }
                }

            }

            try {
                String[] posterAddressArray = getMovieInfoFromJson(moviesJsonStr);
                return posterAddressArray;

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;


        }

    }
}
