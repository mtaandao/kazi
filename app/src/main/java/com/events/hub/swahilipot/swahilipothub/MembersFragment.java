package com.events.hub.swahilipot.swahilipothub;


import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.events.hub.swahilipot.swahilipothub.app.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class MembersFragment extends Fragment {
    // Log tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Movies json url
    private static final String url = "http://api.androidhive.info/json/movies.json";
    private ProgressDialog pDialog;
    private List<Movie> movieList = new ArrayList<Movie>();
    private ListView listView;
    private CustomListAdapter adapter;

    public MembersFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_members,null);
        listView = (ListView) rootView.findViewById(R.id.list);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new CustomListAdapter(getActivity(), movieList);
        listView.setAdapter(adapter);
        pDialog = new ProgressDialog(getActivity());
        // Showing progress dialog before making http request
        pDialog.setMessage("Fetching Members...");
        pDialog.show();


        // Creating volley request obj
        JsonArrayRequest movieReq = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d(TAG, response.toString());
                        hidePDialog();

                        // Parsing json
                        for (int i = 0; i < response.length(); i++) {
                            try {

                                JSONObject obj = response.getJSONObject(i);
                                Movie movie = new Movie();
                                movie.setTitle(obj.getString("title"));
                                movie.setThumbnailUrl(obj.getString("image"));
                                movie.setRating(((Number) obj.get("rating"))
                                        .doubleValue());
                                movie.setYear(obj.getInt("releaseYear"));

                                // Genre is json array
                                JSONArray genreArry = obj.getJSONArray("genre");
                                ArrayList<String> genre = new ArrayList<String>();
                                for (int j = 0; j < genreArry.length(); j++) {
                                    genre.add((String) genreArry.get(j));
                                }
                                movie.setGenre(genre);

                                // adding movie to movies array
                                movieList.add(movie);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                        // notifying list adapter about data changes
                        // so that it renders the list view with updated data
                        adapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(movieReq);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
