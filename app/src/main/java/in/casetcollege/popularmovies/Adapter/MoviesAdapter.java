package in.casetcollege.popularmovies.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Model.Movie;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.UI.MoviesDetailsActivity;
import in.casetcollege.popularmovies.UI.MoviesMainActivity;
import in.casetcollege.popularmovies.UI.MoviesSearchActivity;
import in.casetcollege.popularmovies.UI.TrendingMainActivity;
import in.casetcollege.popularmovies.utils.Constants;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {
    /**
     * This class is used to hold the information for Movies Recycler View in {@link MoviesMainActivity},
     * <p> SEARCHED Movies Recycler View in {@link MoviesSearchActivity},</p>
     * <p> SIMILAR Movies Recycler View in {@link MoviesDetailsActivity} </p>
     * <p> TRENDING Movies Recycler View in {@link TrendingMainActivity} </p>
     */

    private List<Movie> movies;
    private Context context;

    public MoviesAdapter(List<Movie> movies, Context context) {
        this.movies = movies;
        this.context = context;
    }

    @NonNull
    @Override
    public MoviesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        return new MoviesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesViewHolder holder, int position) {
        final Movie movie = movies.get(position);

        String BASE_URL = "http://image.tmdb.org/t/p/";
        String size = "w780/";
        Picasso.get().load(BASE_URL + size + movie.getPosterPath())
                .placeholder(R.mipmap.ic_custom_launcher).into(holder.posterImage);
        holder.posterTitle.setText(movie.getTitle());
    }


    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.size();
        }
        return 0;
    }

    public class MoviesViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.poster_image)
        ImageView posterImage;
        @BindView(R.id.poster_title)
        TextView posterTitle;

        public MoviesViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    Intent intent = new Intent(context, MoviesDetailsActivity.class);
                    intent.putExtra(Constants.MOVIE_IMAGE_URL, movies.get(position).getBackdropPath());
                    intent.putExtra(Constants.MOVIE_TITLE, movies.get(position).getTitle());
                    intent.putExtra(Constants.MOVIE_DESCRIPTION, movies.get(position).getOverview());
                    intent.putExtra(Constants.MOVIE_RATINGS, movies.get(position).getVoteAverage().toString());
                    intent.putExtra(Constants.MOVIE_RELEASE_DATE, movies.get(position).getReleaseDate());
                    intent.putExtra(Constants.MOVIE_ID, movies.get(position).getId().toString());
                    intent.putExtra(Constants.MOVIE_POSTER_IMAGE, movies.get(position).getPosterPath());
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
        }
    }
}
