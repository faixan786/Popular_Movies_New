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
import in.casetcollege.popularmovies.Api.ShowsApi;
import in.casetcollege.popularmovies.BuildConfig;
import in.casetcollege.popularmovies.Database.AppDatabase;
import in.casetcollege.popularmovies.Database.FavouriteShowsModel;
import in.casetcollege.popularmovies.Model.Show;
import in.casetcollege.popularmovies.NetworkInfo.Network;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.UI.ShowsDetailsActivity;
import in.casetcollege.popularmovies.UI.ShowsFavouritesDetails;
import in.casetcollege.popularmovies.UI.ShowsMainActivity;
import in.casetcollege.popularmovies.utils.AppExecutors;
import in.casetcollege.popularmovies.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class FavouriteShowsAdapter extends RecyclerView.Adapter<FavouriteShowsAdapter.FavouriteHolder> {
    /**
     * This class is used to hold the information for Recycler View in {@link ShowsMainActivity}
     */

    private List<FavouriteShowsModel> favouriteShows;
    private Context context;

    String API_KEY = BuildConfig.GoogleSecAPIKEY;

    public FavouriteShowsAdapter(Context context) {
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
        final FavouriteShowsModel favouriteShow = favouriteShows.get(position);
        String BASE_URL = "http://image.tmdb.org/t/p/";
        String size = "w780/";
        Picasso.get().load(BASE_URL + size + favouriteShow.getPosterPath())
                .placeholder(R.mipmap.ic_custom_launcher).into(holder.posterImage);
        holder.posterTitle.setText(favouriteShow.getOriginalTitle());

        holder.posterImage.setOnClickListener(v -> {
            if (Network.getConnectivityStatus(context)) {
                holder.getMovieInfoOnline(favouriteShow.getShowId());
            } else {
                Intent intent = new Intent(context, ShowsFavouritesDetails.class);
                intent.putExtra(Constants.SHOW_TITLE, favouriteShow.getOriginalTitle());
                intent.putExtra(Constants.SHOW_BACKDROP_PATH, favouriteShow.getBackdropPath());
                intent.putExtra(Constants.SHOW_DESCRIPTION, favouriteShow.getOverview());
                intent.putExtra(Constants.SHOW_RELEASE_DATE, favouriteShow.getReleaseDate());
                intent.putExtra(Constants.SHOW_RATINGS, favouriteShow.getVoteAverage());
                intent.putExtra(Constants.SHOW_ID, favouriteShow.getShowId());

                context.startActivity(intent);
            }
        });
    }

    public void setFavouriteShows(List<FavouriteShowsModel> favouriteShows) {
        this.favouriteShows = favouriteShows;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (favouriteShows == null) {
            return 0;
        }
        return favouriteShows.size();
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

                FavouriteShowsModel show = favouriteShows.get(position);
                String showID = show.getShowId();

                AppExecutors.getInstance().diskIO().execute(() ->
                        AppDatabase.getInstance(itemView.getContext()).showsDao().deleteFavShow(showID)
                );
            });
        }

        public void getMovieInfoOnline(String showID) {
            Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(ShowsApi.Api.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();

            ShowsApi.Api api = retrofit.create(ShowsApi.Api.class);

            api.getShowById(Integer.parseInt(showID), API_KEY).enqueue(new Callback<Show>() {
                @Override
                public void onResponse(Call<Show> call, Response<Show> response) {
                    Show show = response.body();
                    if (show != null) {
                        Intent intent = new Intent(context, ShowsDetailsActivity.class);
                        if (show.getBackdropPath() != null) {
                            intent.putExtra(Constants.SHOW_IMAGE_URL, show.getBackdropPath());
                        } else {
                            intent.putExtra(Constants.SHOW_IMAGE_URL, show.getPosterPath());
                        }
                        intent.putExtra(Constants.SHOW_TITLE, show.getName());
                        intent.putExtra(Constants.SHOW_DESCRIPTION, show.getOverview());
                        intent.putExtra(Constants.SHOW_RATINGS, show.getVoteAverage());
                        intent.putExtra(Constants.SHOW_RELEASE_DATE, show.getReleaseDate());
                        intent.putExtra(Constants.SHOW_ID, show.getId());
                        intent.putExtra(Constants.SHOW_POSTER_IMAGE, show.getPosterPath());
                        context.startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<Show> call, Throwable t) {
                }
            });
        }
    }
}