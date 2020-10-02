package com.nicolaslagamma.flixster.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class Movie {

    int movieId;
    String backdropPath;
    String posterPath;
    String title;
    String overview;
    String releaseDate;
    boolean adult;
    double rating;

    // empty constructor required by the Parceler library
    public Movie() {}

    public Movie(JSONObject jsonObject) throws JSONException {
        movieId = jsonObject.getInt("id");
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        rating = jsonObject.getDouble("vote_average");
        releaseDate = jsonObject.getString("release_date");
        adult = jsonObject.getBoolean("adult");
    }

    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", backdropPath);
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", posterPath);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public Double getRating() { return rating; }

    public int getMovieId() { return movieId; }

    public String getReleaseDate() { return releaseDate; }

    public boolean getAdult() { return adult; }

    public boolean isPopular() { return rating > 6.0; }
}
