package in.casetcollege.popularmovies.UI;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.casetcollege.popularmovies.Adapter.FavouriteMoviesAdapter;
import in.casetcollege.popularmovies.Adapter.MoviesAdapter;
import in.casetcollege.popularmovies.Api.MoviesApi;
import in.casetcollege.popularmovies.BuildConfig;
import in.casetcollege.popularmovies.Database.FavouriteMoviesModel;
import in.casetcollege.popularmovies.Model.Movie;
import in.casetcollege.popularmovies.Model.MovieResults;
import in.casetcollege.popularmovies.NetworkInfo.Network;
import in.casetcollege.popularmovies.R;
import in.casetcollege.popularmovies.ViewModel.FavouriteMoviesViewModel;
import in.casetcollege.popularmovies.utils.Constants;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MoviesMainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;
    @BindView(R.id.contentlayout)
    ConstraintLayout constraintLayout;
    @BindView(R.id.nofav)
    TextView textView;
    private RecyclerView.LayoutManager layoutManager;
    private MoviesAdapter adapter;
    private MovieResults moviesModel;

    private List<Movie> movieMovies;

    private FavouriteMoviesAdapter favouritesAdapter;

    public final static String LIST_STATE_KEY = "recycler_list_state";
    Parcelable listState;

    String API_KEY = BuildConfig.GoogleSecAPIKEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.movies_main_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.black));
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        movieMovies = new ArrayList<>();

        //Binding View
        ButterKnife.bind(this);
        settingLayoutManager();

        boolean isConnected = Network.getConnectivityStatus(MoviesMainActivity.this);

        if (isConnected) {
            loadPopularMovies();
        } else {
            checkNetworkConnection();
            Snackbar.make(constraintLayout, "Please Check Internet Connection", Snackbar.LENGTH_LONG).show();
        }

        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.toolbarBottom);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.bottomToolbarShows:
                    startActivity(new Intent(MoviesMainActivity.this, ShowsMainActivity.class));
                    Animatoo.animateFade(this);
                    finish();
                    break;

                case R.id.bottomToolbarTrending:
                    startActivity(new Intent(MoviesMainActivity.this, TrendingMainActivity.class));
                    Animatoo.animateFade(this);
                    finish();
                    break;
            }
            return false;
        });
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save list state
        listState = layoutManager.onSaveInstanceState();
        outState.putParcelable(LIST_STATE_KEY, listState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // Retrieve list state and list/item position
        listState = savedInstanceState.getParcelable(LIST_STATE_KEY);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (listState != null) {
            layoutManager.onRestoreInstanceState(listState);
        }
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void checkNetworkConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No internet connection");
        builder.setMessage("Please turn on internet connection and then Refresh to continue Or press close and check favourite lists");
        builder.setPositiveButton("Favourites", (dialog, which) -> {
            loadFavouriteMovies();
        });
        builder.setNegativeButton("Close", (dialog, which) -> {
            dialog.dismiss();
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void settingLayoutManager() {
        int noOfColumns = 2;
        layoutManager = new GridLayoutManager(this, noOfColumns);
        recyclerView.setLayoutManager(layoutManager);
        LayoutAnimationController controller = AnimationUtils.loadLayoutAnimation(getApplicationContext(), R.anim.layout_animation_fade_in);
        recyclerView.setLayoutAnimation(controller);
        recyclerView.setHasFixedSize(true);
    }

    private void loadPopularMovies() {
        new ViewModelProvider(this).get(FavouriteMoviesViewModel.class).getFavourites().removeObserver(favouritesObserver);

        getSupportActionBar().setTitle(getString(R.string.popular_movies));

        //  Creating a retrofit object
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // Creating the Api interface
        MoviesApi.Api api = retrofit.create(MoviesApi.Api.class);

        // now making the call object
        Call<MovieResults> call = api.getPopularMovies(API_KEY);

        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                moviesModel = response.body();
                movieMovies = new ArrayList<>();
                movieMovies = moviesModel.getMovies();
                adapter = new MoviesAdapter(movieMovies, getApplicationContext());
//                adapter.setOnItemClickListener(MoviesMainActivity.this);

                recyclerView.setAdapter(adapter);

                // Animation
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {
                Log.d(TAG, "onFailure: Message " + t.getMessage());
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here
        int id = item.getItemId();

        if (id == R.id.nav_popular) {
            boolean isConnected = Network.getConnectivityStatus(MoviesMainActivity.this);
            if (isConnected) {
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                loadPopularMovies();
            } else {
                checkNetworkConnection();
                Snackbar.make(constraintLayout, "Please check your internet connection", Snackbar.LENGTH_LONG).show();
            }
        } else if (id == R.id.nav_Top_rated) {
            boolean isConnected = Network.getConnectivityStatus(MoviesMainActivity.this);
            if (isConnected) {
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                loadTopRatedMovies();
            } else {
                checkNetworkConnection();
                Snackbar.make(constraintLayout, "Please check your internet connection", Snackbar.LENGTH_LONG).show();
            }
        } else if (id == R.id.favourites) {
            loadFavouriteMovies();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void loadFavouriteMovies() {
        getSupportActionBar().setTitle(getString(R.string.favourite_movies));

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        if(favouritesAdapter == null) {
            favouritesAdapter = new FavouriteMoviesAdapter(this);
        }
        recyclerView.setAdapter(favouritesAdapter);

        // Animation
        recyclerView.scheduleLayoutAnimation();
        setupViewModel();
    }

    private void setupViewModel() {
        FavouriteMoviesViewModel viewModel = new ViewModelProvider(this).get(FavouriteMoviesViewModel.class);
        viewModel.getFavourites().observe(this, favouritesObserver);
    }

    private final Observer<List<FavouriteMoviesModel>> favouritesObserver = new Observer<List<FavouriteMoviesModel>>() {
        @Override
        public void onChanged(List<FavouriteMoviesModel> favouriteMoviesModels) {
            if (favouriteMoviesModels != null && favouriteMoviesModels.size() > 0) {
                recyclerView.setVisibility(View.VISIBLE);
                favouritesAdapter.setFavouriteMovies(favouriteMoviesModels);
                textView.setVisibility(View.GONE);
            } else {
                favouritesAdapter.setFavouriteMovies(null);
                recyclerView.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
            }
        }
    };

    private void loadTopRatedMovies() {
        new ViewModelProvider(this).get(FavouriteMoviesViewModel.class).getFavourites().removeObserver(favouritesObserver);
        getSupportActionBar().setTitle(getString(R.string.top_rated_movies));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        MoviesApi.Api api = retrofit.create(MoviesApi.Api.class);

        Call<MovieResults> call = api.getTopRatedMovies(API_KEY);
        call.enqueue(new Callback<MovieResults>() {
            @Override
            public void onResponse(Call<MovieResults> call, Response<MovieResults> response) {
                moviesModel = response.body();
                movieMovies = new ArrayList<>();
                movieMovies = moviesModel.getMovies();
                adapter = new MoviesAdapter(movieMovies, getApplicationContext());
//                adapter.setOnItemClickListener(MoviesMainActivity.this);
                recyclerView.setAdapter(adapter);

                // Animation
                recyclerView.getAdapter().notifyDataSetChanged();
                recyclerView.scheduleLayoutAnimation();
            }

            @Override
            public void onFailure(Call<MovieResults> call, Throwable t) {
                Log.d(TAG, "onFailure: Message = " + t.getMessage());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.refresh) {
            boolean isConnected = Network.getConnectivityStatus(MoviesMainActivity.this);
            if (isConnected) {
                recyclerView.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);
                loadPopularMovies();
            } else {
                checkNetworkConnection();
                Snackbar.make(constraintLayout, "Please check your internet connection", Snackbar.LENGTH_LONG).show();
            }
        } else if (id == R.id.random) {
            getRandomMovie();
        } else if(id == R.id.search) {
            startActivity(new Intent(MoviesMainActivity.this, MoviesSearchActivity.class));
        }

        return true;
    }

    private void getRandomMovie() {
        Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(MoviesApi.Api.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        MoviesApi.Api api = retrofit.create(MoviesApi.Api.class);

        Call<Movie> call = api.getLatestMovie(API_KEY);
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movie = response.body();
                int id = movie.getId();
                int randomId = (new Random().nextInt(id)) + 1;

                api.getMovieById(randomId, API_KEY).enqueue(new Callback<Movie>() {
                    @Override
                    public void onResponse(Call<Movie> call, Response<Movie> response) {
                        Movie newMovie = response.body();
                        if (newMovie != null && (newMovie.getBackdropPath() != null || newMovie.getPosterPath() != null) && !newMovie.getAdult()) {
                            Intent intent = new Intent(MoviesMainActivity.this, MoviesDetailsActivity.class);
                            if(newMovie.getBackdropPath() != null){
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
                            startActivity(intent);
                        } else {
                            Snackbar.make(constraintLayout, "A movie with no photo or caption was found. Sorry for the trouble. Please try again", Snackbar.LENGTH_INDEFINITE)
                                    .setAction("Retry", new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            getRandomMovie();
                                        }
                                    }).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Movie> call, Throwable t) {
                        Log.d(TAG, "onFailure: Message " + t.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Log.d(TAG, "onFailure: Message " + t.getMessage());
            }
        });
    }
}