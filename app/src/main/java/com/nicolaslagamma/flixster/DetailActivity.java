package com.nicolaslagamma.flixster;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.databinding.DataBindingUtil;
import okhttp3.Headers;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.nicolaslagamma.flixster.databinding.ActivityDetailBinding;
import com.nicolaslagamma.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

public class DetailActivity extends YouTubeBaseActivity {

    private ActivityDetailBinding binding;
    private static final String TAG = "DetailActivity";
    private static final String VIDEOS_URL = "https://api.themoviedb.org/3/movie/%d/videos?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        final Movie movie = Parcels.unwrap(getIntent().getParcelableExtra("movie"));
        binding.tvTitle.setText(movie.getTitle());
        binding.tvOverview.setText(movie.getOverview());
        binding.ratingBar.setRating(movie.getRating().floatValue());

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(String.format(VIDEOS_URL, movie.getMovieId()), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess " + String.format(VIDEOS_URL, movie.getMovieId()));
                try {
                    JSONArray results = json.jsonObject.getJSONArray("results");
                    boolean found = false;
                    for (int i = 0; i < results.length(); i++) {
                        if (results.getJSONObject(i).getString("site").equals("YouTube")) {
                            String youtubeKey = results.getJSONObject(i).getString("key");
                            Log.i(TAG, "Key: " + youtubeKey);
                            initializeYoutube(youtubeKey, movie.isPopular());
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        // display backup image poster
                        Glide.with(getApplicationContext())
                                .load(movie.getBackdropPath())
                                .placeholder(R.drawable.movie_placeholder)
                                .error(R.drawable.movie_placeholder)
                                .into(new CustomTarget<Drawable>() {
                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) { binding.player.setForeground(resource); }

                                    @RequiresApi(api = Build.VERSION_CODES.M)
                                    @Override
                                    public void onLoadCleared(@Nullable Drawable placeholder) { binding.player.setForeground(placeholder); }
                                });
                    }
                } catch (JSONException e) {
                    Log.e(TAG, "Failed to parse JSON", e);
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });


    }

    private void initializeYoutube(final String youtubeKey, final boolean start) {
        binding.player.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                Log.d(TAG, "onInitializationSuccess");
                if (start) {
                    youTubePlayer.loadVideo(youtubeKey);
                } else {
                    youTubePlayer.cueVideo(youtubeKey);
                }
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Log.d(TAG, "onInitializationFailure");
            }
        });
    }
}