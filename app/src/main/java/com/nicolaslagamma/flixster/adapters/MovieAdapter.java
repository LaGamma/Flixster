package com.nicolaslagamma.flixster.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.nicolaslagamma.flixster.DetailActivity;
import com.nicolaslagamma.flixster.R;
import com.nicolaslagamma.flixster.databinding.ItemMovie1Binding;
import com.nicolaslagamma.flixster.databinding.ItemMovie2Binding;
import com.nicolaslagamma.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
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
        LayoutInflater inflater = LayoutInflater.from(context);
        switch (viewType) {
            case STANDARD:
                viewType = R.layout.item_movie1;
                break;
            case POPULAR:
                viewType = R.layout.item_movie2;
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + viewType);
        }
        ViewDataBinding binding = DataBindingUtil.inflate(inflater, viewType, parent, false);
        return new ViewHolder(binding, viewType);
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

        private ItemMovie1Binding binding1;
        private ItemMovie2Binding binding2;

        public ViewHolder(ViewDataBinding binding, int viewType) {
            super(binding.getRoot());
            if (viewType == R.layout.item_movie1) {
                this.binding1 = (ItemMovie1Binding) binding;
            } else {
                this.binding2 = (ItemMovie2Binding) binding;
            }
        }

        public void bind(final Movie movie) {
            String imageUrl;
            // if phone is in landscape
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // then set imageUrl = backdrop image
                imageUrl = movie.getBackdropPath();
            } else {
                // else imageUrl = poster image
                imageUrl = movie.getPosterPath();
            }
            if (binding1 != null) {
                binding1.tvTitle.setText(movie.getTitle());
                binding1.tvOverview.setText(movie.getOverview());
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.movie_placeholder)
                        .error(R.drawable.movie_placeholder)
                        .fitCenter()
                        .transform(new RoundedCornersTransformation(60, 0))
                        .into(binding1.ivPoster);
                // register click listener on the whole row (container)
                binding1.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // navigate to a new activity on tap
                        Intent i = new Intent(context, DetailActivity.class);
                        // Pass data object in the bundle and populate details activity.
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) context, binding1.ivPoster, "display");
                        i.putExtra("movie", Parcels.wrap(movie));
                        context.startActivity(i, options.toBundle());
                    }
                });
                binding1.executePendingBindings();
            } else {
                binding2.tvTitle.setText(movie.getTitle());
                binding2.tvOverview.setText(movie.getOverview());
                Glide.with(context)
                        .load(imageUrl)
                        .placeholder(R.drawable.movie_placeholder)
                        .error(R.drawable.movie_placeholder)
                        .fitCenter()
                        .transform(new RoundedCornersTransformation(60, 0))
                        .into(binding2.ivPoster);
                // register click listener on the whole row (container)
                binding2.container.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // navigate to a new activity on tap
                        Intent i = new Intent(context, DetailActivity.class);
                        // Pass data object in the bundle and populate details activity.
                        ActivityOptionsCompat options = ActivityOptionsCompat.
                                makeSceneTransitionAnimation((Activity) context, binding2.ivPoster, "display");
                        i.putExtra("movie", Parcels.wrap(movie));
                        context.startActivity(i, options.toBundle());
                    }
                });
                binding2.executePendingBindings();
            }
        }
    }
}
