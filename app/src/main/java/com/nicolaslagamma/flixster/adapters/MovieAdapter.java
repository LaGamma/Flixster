package com.nicolaslagamma.flixster.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nicolaslagamma.flixster.DetailActivity;
import com.nicolaslagamma.flixster.MainActivity;
import com.nicolaslagamma.flixster.R;
import com.nicolaslagamma.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    private Context context;
    private List<Movie> movies;
    private final int STANDARD = 0, POPULAR = 1;

    public MovieAdapter(Context context, List<Movie> movies) {
        this.context = context;
        this.movies = movies;
    }

    // Usually involves inflating a layout from XML and returning the holder
    @NonNull
    @Override
    public MovieAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter", "onCreateViewHolder " + viewType);
        MovieAdapter.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case STANDARD:
                View v1 = inflater.inflate(R.layout.item_movie1, parent, false);
                viewHolder = new ViewHolder(v1);
                break;
            case POPULAR:
                View v2 = inflater.inflate(R.layout.item_movie2, parent, false);
                viewHolder = new ViewHolder(v2);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType(int position) {
        if (movies.get(position).isPopular()) {
            return POPULAR;
        }
        return STANDARD;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(@NonNull MovieAdapter.ViewHolder holder, int position) {
        Log.d("MovieAdapter", "onBindViewHolder " + position);
        // Get the movie at the passed in position
        Movie movie = movies.get(position);
        //Bind the movie data into the ViewHolder
        holder.bind(movie);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout container;
        TextView tvTitle;
        TextView tvOverview;
        ImageView ivPoster;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvOverview = itemView.findViewById(R.id.tvOverview);
            ivPoster = itemView.findViewById(R.id.ivPoster);
            container = itemView.findViewById(R.id.container);
        }

        public void bind(final Movie movie) {
            tvTitle.setText(movie.getTitle());
            tvOverview.setText(movie.getOverview());
            String imageUrl;
            // if phone is in landscape
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // then set imageUrl = backdrop image
                imageUrl = movie.getBackdropPath();
            } else {
                // else imageUrl = poster image
                imageUrl = movie.getPosterPath();
            }
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.movie_placeholder)
                    .error(R.drawable.movie_placeholder)
                    .fitCenter()
                    .transform(new RoundedCornersTransformation(60, 0))
                    .into(ivPoster);

            // register click listener on the whole row (container)
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // navigate to a new activity on tap
                    Intent i = new Intent(context, DetailActivity.class);
                    // Pass data object in the bundle and populate details activity.
                    //i.putExtra(DetailActivity.EXTRA_CONTACT, contact);
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, ivPoster, "display");
                    i.putExtra("movie", Parcels.wrap(movie));
                    context.startActivity(i, options.toBundle());

                }
            });
        }
    }
}
