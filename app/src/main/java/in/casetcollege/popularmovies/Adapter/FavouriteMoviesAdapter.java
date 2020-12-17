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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Api.MoviesApi;
import in.casetcollege.popularmovies.BuildConfig;
import in.casetcollege.popularmovies.Database.AppDatabase;
import in.casetcollege.popularmovies.Database.FavouriteMoviesModel;
import in.casetcollege.popularmovies.Model.Movie;
import in.casetcollege.popularmovies.NetworkInfo.Network;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.UI.MoviesDetailsActivity;
import in.casetcollege.popularmovies.UI.MoviesFavouritesDetails;
import in.casetcollege.popularmovies.UI.MoviesMainActivity;
import in.casetcollege.popularmovies.utils.AppExecutors;
import in.casetcollege.popularmovies.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavouriteMoviesAdapter extends RecyclerView.Adapter<FavouriteMoviesAdapter.FavouriteHolder> {
    /**
     * This class is used to hold the information for Recycler View in {@link MoviesMainActivity}
     */

    private List<FavouriteMoviesModel> favouriteMovies;
    private Context context;

    String API_KEY = BuildConfig.GoogleSecAPIKEY;

    public FavouriteMoviesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public FavouriteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.favourite_list, parent, false);
        return new FavouriteHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteHolder holder, int position) {
        final FavouriteMoviesModel favouriteMovie = favouriteMovies.get(position);
        String BASE_URL = "http://image.tmdb.org/t/p/";
        String size = "w780/";
        Picasso.get().load(BASE_URL + size + favouriteMovie.getPosterPath())
                .placeholder(R.mipmap.ic_custom_launcher).into(holder.posterImage);
        holder.posterTitle.setText(favouriteMovie.getOriginalTitle());

        holder.posterImage.setOnClickListener(v -> {
            if (Network.getConnectivityStatus(context)) {
                holder.getMovieInfoOnline(favouriteMovie.getMovieId());
            } else {
                Intent intent = new Intent(context, MoviesFavouritesDetails.class);
                intent.putExtra(Constants.MOVIE_TITLE, favouriteMovie.getOriginalTitle());
                intent.putExtra(Constants.MOVIE_BACKDROP_PATH, favouriteMovie.getBackdropPath());
                intent.putExtra(Constants.MOVIE_DESCRIPTION, favouriteMovie.getOverview());
                intent.putExtra(Constants.MOVIE_RELEASE_DATE, favouriteMovie.getReleaseDate());
                intent.putExtra(Constants.MOVIE_RATINGS, favouriteMovie.getVoteAverage());
//                intent.putExtra("liked", favouriteMovie.isFavourite());
                intent.putExtra(Constants.MOVIE_ID, favouriteMovie.getMovieId());

                context.startActivity(intent);
            }
        });
    }

    public void setFavouriteMovies(List<FavouriteMoviesModel> favouriteMovies) {
        this.favouriteMovies = favouriteMovies;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (favouriteMovies == null) {
            return 0;
        }
        return favouriteMovies.size();
    }

public class FavouriteHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.poster_image)
    ImageView posterImage;
    @BindView(R.id.poster_title)
    TextView posterTitle;
    @BindView(R.id.fav_btn)
    FloatingActionButton floatingActionButton;

    public FavouriteHolder(@NonNull View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);

        floatingActionButton.setOnClickListener(v -> {
            int position = getAdapterPosition();

            FavouriteMoviesModel movie = favouriteMovies.get(position);
            String movieID = movie.getMovieId();

            AppExecutors.getInstance().diskIO().execute(() ->
                    AppDatabase.getInstance(itemView.getContext()).moviesDao().deleteFavMovie(movieID)
            );
        });
    }

    public void getMovieInfoOnline(String movieID) {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MoviesApi.Api api = retrofit.create(MoviesApi.Api.class);

        api.getMovieById(Integer.parseInt(movieID), API_KEY).enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie newMovie = response.body();
                if (newMovie != null && (newMovie.getBackdropPath() != null || newMovie.getPosterPath() != null) && !newMovie.getAdult()) {
                    Intent intent = new Intent(context, MoviesDetailsActivity.class);
                    if (newMovie.getBackdropPath() != null) {
                        intent.putExtra(Constants.MOVIE_IMAGE_URL, newMovie.getBackdropPath());
                    } else {
                        intent.putExtra(Constants.MOVIE_IMAGE_URL, newMovie.getPosterPath());
                    }
                    intent.putExtra(Constants.MOVIE_TITLE, newMovie.getTitle());
                    intent.putExtra(Constants.MOVIE_DESCRIPTION, newMovie.getOverview());
                    intent.putExtra(Constants.MOVIE_RATINGS, newMovie.getVoteAverage().toString());
                    intent.putExtra(Constants.MOVIE_RELEASE_DATE, newMovie.getReleaseDate());
                    intent.putExtra(Constants.MOVIE_ID, newMovie.getId().toString());
                    intent.putExtra(Constants.MOVIE_POSTER_IMAGE, newMovie.getPosterPath());
                    context.startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {}
        });
    }
}
}
