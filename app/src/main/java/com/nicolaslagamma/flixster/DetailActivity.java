package com.nicolaslagamma.flixster;

import androidx.databinding.DataBindingUtil;
import okhttp3.Headers;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
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
                    if (results.length() == 0) {
                        // hide video player and display backup image poster
                        binding.player.setVisibility(View.GONE);
                        Glide.with(getApplicationContext())
                                .load(movie.getBackdropPath())
                                .placeholder(R.drawable.movie_placeholder)
                                .error(R.drawable.movie_placeholder)
                                .into(binding.ivPoster);
                        binding.ivPoster.setVisibility(View.VISIBLE);
                        return;
                    };
                    String youtubeKey = results.getJSONObject(0).getString("key");
                    Log.i(TAG, "Key: " + youtubeKey);
                    initializeYoutube(youtubeKey, movie.isPopular());
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