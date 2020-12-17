package in.casetcollege.popularmovies.UI;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Adapter.CastAdapter;
import in.casetcollege.popularmovies.Adapter.ImagesAdapter;
import in.casetcollege.popularmovies.Adapter.ReviewAdapter;
import in.casetcollege.popularmovies.Adapter.ShowsAdapter;
import in.casetcollege.popularmovies.Adapter.TrailerAdapter;
import in.casetcollege.popularmovies.Api.ShowsApi;
import in.casetcollege.popularmovies.BuildConfig;
import in.casetcollege.popularmovies.Database.AppDatabase;
import in.casetcollege.popularmovies.Database.FavouriteShowsModel;
import in.casetcollege.popularmovies.Model.Cast;
import in.casetcollege.popularmovies.Model.CastResults;
import in.casetcollege.popularmovies.Model.Image;
import in.casetcollege.popularmovies.Model.ImageResults;
import in.casetcollege.popularmovies.Model.Review;
import in.casetcollege.popularmovies.Model.ReviewResults;
import in.casetcollege.popularmovies.Model.Show;
import in.casetcollege.popularmovies.Model.ShowResults;
import in.casetcollege.popularmovies.Model.Trailer;
import in.casetcollege.popularmovies.Model.TrailerResults;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.utils.AppExecutors;
import in.casetcollege.popularmovies.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ShowsDetailsActivity extends AppCompatActivity {
    @BindView(R.id.rootLayout)
    CoordinatorLayout coordinatorLayout;
    @BindView(R.id.collapsing)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.img_poster)
    ImageView backdrop;
    @BindView(R.id.movie_title)
    TextView title;
    @BindView(R.id.movie_overview)
    TextView overview;
    @BindView(R.id.release_date)
    TextView releaseDate;
    @BindView(R.id.movie_ratings)
    TextView ratings;
    @BindView(R.id.poster)
    ImageView circleBackdrop;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.recyclerviewtrailer)
    RecyclerView recyclerViewTrailer;
    @BindView(R.id.recyclerviewreview)
    RecyclerView recyclerViewReview;
    @BindView(R.id.recyclerViewImages)
    RecyclerView recyclerViewImages;
    @BindView(R.id.recyclerViewSimilarMovies)
    RecyclerView recyclerViewSimilarMovies;
    @BindView(R.id.recyclerViewCast)
    RecyclerView recyclerViewCast;
    @BindView(R.id.fav_btn)
    FloatingActionButton favBtn;

    RecyclerView.LayoutManager layoutManager;
    String API_KEY = BuildConfig.GoogleSecAPIKEY;

    boolean isFavourite;
    private AppDatabase mdb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_details_activity);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mdb = AppDatabase.getInstance(this);
        isFavourite();

        Intent intent = getIntent();
        String BASE_URL = "http://image.tmdb.org/t/p/";
        String size = "w780/";
        String image = intent.getStringExtra(Constants.SHOW_IMAGE_URL);
        String movieTitle = intent.getStringExtra(Constants.SHOW_TITLE);
        String description = intent.getStringExtra(Constants.SHOW_DESCRIPTION);
        String release = intent.getStringExtra(Constants.SHOW_RELEASE_DATE);
        String average = intent.getStringExtra(Constants.SHOW_RATINGS);

        Picasso.get().load(BASE_URL + size + image)
                .placeholder(R.mipmap.ic_custom_launcher)
                .into(backdrop);
        overview.setText(description);
        title.setText(movieTitle);
        DateFormat inputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");
        Date date1;
        String output1;
        try {
            date1 = inputFormatter1.parse(release);
            DateFormat outputFormatter1 = new SimpleDateFormat("dd-MMM-yyyy");
            output1 = outputFormatter1.format(date1);
            releaseDate.setText(output1);
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ratings.setText(average);
        Picasso.get().load(BASE_URL + size + image).into(circleBackdrop);

        layoutManagerCast();
        loadCast();

        layoutManagerImages();
        loadImages();

        layoutManagerTrailers();
        loadTrailers();

        layoutSimilarShows();
        loadSimilarShows();

        layoutManagerReview();
        loadReviews();

        favBtn.setOnClickListener(v -> {
            if (isFavourite) {
                removeShowFromList();
                isFavourite = false;
                favBtn.setImageDrawable(ContextCompat.getDrawable(ShowsDetailsActivity.this, R.drawable.ic_favorite_border_black_24dp));
                Toast.makeText(ShowsDetailsActivity.this, "Removed from favourites", Toast.LENGTH_SHORT).show();
            } else {
                addShowToFavouriteList();
                isFavourite = true;
                favBtn.setImageDrawable(ContextCompat.getDrawable(ShowsDetailsActivity.this, R.drawable.ic_favorite_red_24dp));
                Toast.makeText(ShowsDetailsActivity.this, "Added to favourites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void layoutManagerCast() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewCast.setHasFixedSize(true);
        recyclerViewCast.setLayoutManager(layoutManager);
    }

    private void loadCast() {
        Intent intent = getIntent();
        String showID = intent.getStringExtra(Constants.SHOW_ID);

        // Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ShowsApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Creating the Api interface
        ShowsApi.Api api = retrofit.create(ShowsApi.Api.class);

        //now making the call object
        Call<CastResults> call = api.getCast(showID, API_KEY);
        call.enqueue(new Callback<CastResults>() {
            @Override
            public void onResponse(Call<CastResults> call, Response<CastResults> response) {
                if (response.body() != null) {
                    List<Cast> castList = response.body().getCastList();
                    recyclerViewCast.setAdapter(new CastAdapter(castList, getApplicationContext()));
                }
            }

            @Override
            public void onFailure(Call<CastResults> call, Throwable t) {
            }
        });
    }

    private void layoutManagerImages() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewImages.setLayoutManager(layoutManager);
        recyclerViewImages.setHasFixedSize(true);
    }

    private void loadImages() {
        Intent intent = getIntent();
        String showID = intent.getStringExtra(Constants.SHOW_ID);

        // Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ShowsApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Creating the Api interface
        ShowsApi.Api api = retrofit.create(ShowsApi.Api.class);

        //now making the call object
        Call<ImageResults> call = api.getImages(showID, API_KEY);
        call.enqueue(new Callback<ImageResults>() {
            @Override
            public void onResponse(Call<ImageResults> call, Response<ImageResults> response) {
                if (response.body() != null) {
                    List<Image> images = response.body().getPhotos();
                    recyclerViewImages.setAdapter(new ImagesAdapter(images, getApplicationContext()));
                }
            }

            @Override
            public void onFailure(Call<ImageResults> call, Throwable t) {
            }
        });
    }

    private void layoutManagerTrailers() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewTrailer.setLayoutManager(layoutManager);
        recyclerViewTrailer.setHasFixedSize(true);
    }

    private void loadTrailers() {
        Intent intent = getIntent();
        String showID = intent.getStringExtra(Constants.SHOW_ID);

        // Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ShowsApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Creating the Api interface
        ShowsApi.Api api = retrofit.create(ShowsApi.Api.class);

        //now making the call object
        Call<TrailerResults> call = api.getTrailer(showID, API_KEY);
        call.enqueue(new Callback<TrailerResults>() {
            @Override
            public void onResponse(Call<TrailerResults> call, @NonNull Response<TrailerResults> response) {
                if (response.body() != null) {
                    List<Trailer> trailerList = response.body().getResults();
                    recyclerViewTrailer.setAdapter(new TrailerAdapter(trailerList, getApplicationContext()));
                }
            }

            @Override
            public void onFailure(Call<TrailerResults> call, Throwable t) {
                Log.d("onFailure", "" + t);
            }
        });
    }

    private void layoutSimilarShows() {
        layoutManager = new GridLayoutManager(this, 2);
        recyclerViewSimilarMovies.setLayoutManager(layoutManager);
        recyclerViewSimilarMovies.setHasFixedSize(true);
    }

    private void loadSimilarShows() {
        Intent intent = getIntent();
        String showID = intent.getStringExtra(Constants.SHOW_ID);

        // Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ShowsApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Creating the Api interface
        ShowsApi.Api api = retrofit.create(ShowsApi.Api.class);

        //now making the call object
        Call<ShowResults> call = api.getSimilarShows(showID, API_KEY);
        call.enqueue(new Callback<ShowResults>() {
            @Override
            public void onResponse(Call<ShowResults> call, Response<ShowResults> response) {
                if (response.body() != null) {
                    List<Show> shows = response.body().getShows();
                    ShowsAdapter adapter = new ShowsAdapter(shows, getApplicationContext());
                    recyclerViewSimilarMovies.setAdapter(adapter);
                }
            }

            @Override
            public void onFailure(Call<ShowResults> call, Throwable t) {
            }
        });
    }

    private void layoutManagerReview() {
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewReview.setHasFixedSize(false);
        recyclerViewReview.setLayoutManager(layoutManager);
    }

    private void loadReviews() {
        Intent intent = getIntent();
        String showID = intent.getStringExtra(Constants.SHOW_ID);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ShowsApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ShowsApi.Api api = retrofit.create(ShowsApi.Api.class);
        Call<ReviewResults> call = api.getReviews(showID, API_KEY);
        call.enqueue(new Callback<ReviewResults>() {
            @Override
            public void onResponse(Call<ReviewResults> call, Response<ReviewResults> response) {
                if (response.body() != null) {
                    List<Review> reviews = response.body().getReviews();
                    recyclerViewReview.setAdapter(new ReviewAdapter(reviews, getApplicationContext()));
                }
            }

            @Override
            public void onFailure(Call<ReviewResults> call, Throwable t) {
            }
        });
    }

    private void removeShowFromList() {
        Intent intent = getIntent();

        final String showID = intent.getStringExtra(Constants.SHOW_ID);
        AppExecutors.getInstance().diskIO().execute(() -> mdb.showsDao().deleteFavShow(showID));
    }

    private void addShowToFavouriteList() {
        Intent intent = getIntent();
        String image = intent.getStringExtra(Constants.SHOW_IMAGE_URL);
        final String movieId = intent.getStringExtra(Constants.SHOW_ID);
        String imageName = intent.getStringExtra(Constants.SHOW_TITLE);
        String description = intent.getStringExtra(Constants.SHOW_DESCRIPTION);
        String release = intent.getStringExtra(Constants.SHOW_RELEASE_DATE);
        String average = intent.getStringExtra(Constants.SHOW_RATINGS);
        String poster = intent.getStringExtra(Constants.SHOW_POSTER_IMAGE);

        final FavouriteShowsModel favouriteShow = new FavouriteShowsModel(movieId, average, imageName, poster, image, description, release, true);

        AppExecutors.getInstance().diskIO().execute(() -> {
            if (favouriteShow.isFavourite()) {
                mdb.showsDao().insertFavShow(favouriteShow);
                isFavourite = true;
            }
        });
    }

    private void isFavourite() {
        final String showID = getIntent().getStringExtra(Constants.SHOW_ID);
        final FavouriteShowsModel[] showsResponse = new FavouriteShowsModel[1];

        AppExecutors.getInstance().diskIO().execute(() -> {
            showsResponse[0] = mdb.showsDao().checkIfExists(showID);
            //If the movie belongs to user favourites then it will be shown as liked
            if (showsResponse[0] != null) {
                favBtn.setImageDrawable(ContextCompat.getDrawable(ShowsDetailsActivity.this, R.drawable.ic_favorite_red_24dp));
                isFavourite = true;
            } else {
                favBtn.setImageDrawable(ContextCompat.getDrawable(ShowsDetailsActivity.this, R.drawable.ic_favorite_border_black_24dp));
                isFavourite = false;
            }
        });
    }
}
